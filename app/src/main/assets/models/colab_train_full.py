# Jarvis full training — run on Colab GPU (A100/T4). No APIs, your data only.
# Steps in Colab:
#   1. Upload jarvis-corpus.jsonl (from jarvis-train folder) to /content/
#   2. !pip install torch --index-url https://download.pytorch.org/whl/cu121
#   3. Run this file: !python colab_train_full.py
import json, os, re, collections, random
import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader
from torch.cuda.amp import autocast, GradScaler

CORPUS = "/content/jarvis-corpus.jsonl"   # upload your file here
OUT = "/content/jarvis-custom-v1-full"
os.makedirs(OUT, exist_ok=True)
random.seed(7)

assert torch.cuda.is_available(), "Enable GPU in Colab: Runtime > Change runtime type > T4/A100"
print("GPU:", torch.cuda.get_device_name(0))

def tok_words(s):
    return re.findall(r"<user>|<jarvis>|[\w']+|[.,!?;:]", s.lower())

texts = [json.loads(l)["text"] for l in open(CORPUS, encoding="utf-8") if l.strip()]
random.shuffle(texts)
print(f"full corpus texts={len(texts)}")

counter = collections.Counter()
for t in texts:
    counter.update(tok_words(t))
SPECIAL = ["<pad>", "<unk>", "<bos>", "<eos>"]
most = [w for w, _ in counter.most_common(8000 - len(SPECIAL))]
stoi = {t: i for i, t in enumerate(SPECIAL + most)}
json.dump({"stoi": stoi}, open(os.path.join(OUT, "vocab.json"), "w", encoding="utf-8"), ensure_ascii=False)
print(f"vocab={len(stoi)}")

SEQ, D, L, H = 64, 512, 8, 8
def encode(s):
    return [stoi["<bos>"]] + [stoi.get(t, stoi["<unk>"]) for t in tok_words(s)][:SEQ-2] + [stoi["<eos>"]]

data = []
for t in texts:
    ids = encode(t)
    if len(ids) < 8:
        continue
    ids += [stoi["<pad>"]] * (SEQ - len(ids))
    data.append(ids)
data = torch.tensor(data, dtype=torch.long)
print(f"seqs={len(data)} tokens~{len(data)*SEQ/1e6:.1f}M")

class DS(Dataset):
    def __len__(self): return len(data)
    def __getitem__(self, i): return data[i]

class JarvisGPT(nn.Module):
    def __init__(self, v):
        super().__init__()
        self.tok = nn.Embedding(v, D)
        self.pos = nn.Embedding(SEQ, D)
        layer = nn.TransformerEncoderLayer(d_model=D, nhead=H, dim_feedforward=D*4,
                                           batch_first=True, dropout=0.1)
        self.tr = nn.TransformerEncoder(layer, num_layers=L)
        self.ln = nn.LayerNorm(D)
        self.head = nn.Linear(D, v, bias=False)
        self.register_buffer("mask", torch.triu(torch.ones(SEQ, SEQ), diagonal=1).bool())
    def forward(self, x):
        s = x.size(1)
        h = self.tok(x) + self.pos(torch.arange(s, device=x.device)).unsqueeze(0)
        h = self.tr(h, mask=self.mask[:s, :s])
        return self.head(self.ln(h))

device = torch.device("cuda")
torch.manual_seed(7)
model = JarvisGPT(len(stoi)).to(device)
print(f"params={sum(p.numel() for p in model.parameters())/1e6:.1f}M")
loader = DataLoader(DS(), batch_size=64, shuffle=True, num_workers=2, pin_memory=True)
opt = torch.optim.AdamW(model.parameters(), lr=4e-4, weight_decay=0.01)
sched = torch.optim.lr_scheduler.CosineAnnealingLR(opt, T_max=15*len(loader))
lossf = nn.CrossEntropyLoss(ignore_index=stoi["<pad>"])
scaler = GradScaler()

model.train()
for ep in range(15):
    tot = n = 0
    for batch in loader:
        batch = batch.to(device, non_blocking=True)
        inp, tgt = batch[:, :-1], batch[:, 1:]
        opt.zero_grad()
        with autocast():
            loss = lossf(model(inp).reshape(-1, len(stoi)), tgt.reshape(-1))
        scaler.scale(loss).backward()
        scaler.unscale_(opt)
        torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
        scaler.step(opt); scaler.update(); sched.step()
        tot += loss.item(); n += 1
    print(f"EPOCH {ep+1}/15 loss={tot/n:.3f}", flush=True)
    torch.save({"model_state": model.state_dict(),
                "config": {"vocab": len(stoi), "d": D, "layers": L, "heads": H, "seq": SEQ}},
               os.path.join(OUT, f"pytorch_model_ep{ep+1}.bin"))

torch.save({"model_state": model.state_dict(),
            "config": {"vocab": len(stoi), "d": D, "layers": L, "heads": H, "seq": SEQ}},
           os.path.join(OUT, "pytorch_model.bin"))

# ONNX for mwesh OnnxBackend (fixed seq input = SEQ-1)
model.eval().cpu()
dummy = torch.ones(1, SEQ-1, dtype=torch.long)
torch.onnx.export(model, dummy, os.path.join(OUT, "model.onnx"),
                  input_names=["input"], output_names=["logits"],
                  dynamic_axes={"input": {0: "batch"}, "logits": {0: "batch"}},
                  opset_version=18, dynamo=False)
print("saved pytorch_model.bin + model.onnx + vocab.json")
print("Next: convert to GGUF for smaller phone size if wanted:")
print("  python llama.cpp/convert-hf-to-gguf.py . --outfile jarvis-custom-v1-q4_0.gguf")
