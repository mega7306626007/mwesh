"""Mweshimiwa LLM training engine for Google Colab (GPU). Your data only, no APIs.

Matches the mwesh app exactly:
  - tokenizer: lowercase, regex <user>|<mweshimiwa>|words|punct (same as OnnxBackend.kt)
  - vocab.json format {"stoi": {...}} with <pad>=0 <unk>=1 <bos>=2 <eos>=3
  - ONNX export: input name "input", output "logits", opset 18
"""
import os, re, json, math
from collections import Counter

import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.utils.data import IterableDataset, DataLoader


class Config:
    CORPUS_PATH = "/content/mweshimiwa-corpus-200x.jsonl"  # upload your file here
    SAVE_DIR = "/content/mweshimiwa-custom-v1-mega"
    # NOTE: 1024-dim + 64 layers + 79k vocab would be ~890M params, not 100M
    # (embeddings alone would be 79k*1024 = 81M, plus 64 layers x 12.6M).
    # This config is the honest ~100M version: 1024-dim, 6 layers, 32k vocab.
    # 64 layers would also be unusable on a phone (minutes per reply) and
    # would need weeks on a free T4. Grow layers only after this trains well.
    VOCAB_SIZE = 32000
    D_MODEL = 1024
    N_LAYERS = 6
    N_HEADS = 16
    SEQ_LEN = 64
    D_FF = 4096
    DROPOUT = 0.1
    BATCH_SIZE = 8
    ACCUM_STEPS = 8        # effective batch = 64
    EPOCHS = 10
    STEPS_PER_EPOCH = 2000  # ~20k steps total; resume supported, see below
    LR = 4e-4
    WARMUP_STEPS = 500
    LOG_EVERY = 100
    MAX_NEW_TOKENS = 40


SPECIAL = ["<pad>", "<unk>", "<bos>", "<eos>"]
TAG_RE = re.compile(r"<user>|<mweshimiwa>|[\w']+|[.,!?;:]")


class Tokenizer:
    def __init__(self):
        self.stoi = {}
        self.itos = {}

    def encode(self, text):
        return [self.stoi.get(t, self.stoi["<unk>"])
                for t in TAG_RE.findall(text.lower())]

    def decode(self, ids):
        out = []
        for i in ids:
            t = self.itos.get(i, "")
            if t == "<eos>":
                break
            if t in ("<pad>", "<bos>"):
                continue
            out.append(t)
        s = " ".join(out)
        return s.replace("<user>", "").replace("<mweshimiwa>", "").strip()

    def build_vocab(self, corpus_path, max_vocab):
        print(f"Building vocab from {corpus_path}...")
        counter = Counter()
        n = 0
        with open(corpus_path, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                try:
                    text = json.loads(line)["text"]
                except Exception:
                    text = line
                counter.update(TAG_RE.findall(text.lower()))
                n += 1
                if n % 1_000_000 == 0:
                    print(f"  {n:,} lines...")
        print(f"  lines={n:,} unique={len(counter):,}")
        most = [w for w, _ in counter.most_common(max_vocab - len(SPECIAL))]
        self.stoi = {t: i for i, t in enumerate(SPECIAL + most)}
        self.itos = {i: t for t, i in self.stoi.items()}
        print(f"  vocab={len(self.stoi):,}")

    def save(self, path):
        with open(path, "w", encoding="utf-8") as f:
            json.dump({"stoi": self.stoi}, f, ensure_ascii=False)


class CorpusDataset(IterableDataset):
    def __init__(self, corpus_path, tokenizer, seq_len):
        self.corpus_path = corpus_path
        self.tokenizer = tokenizer
        self.seq_len = seq_len

    def __iter__(self):
        buf = []
        with open(self.corpus_path, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                try:
                    text = json.loads(line)["text"]
                except Exception:
                    text = line
                ids = ([self.tokenizer.stoi["<bos>"]]
                       + self.tokenizer.encode(text)[:self.seq_len - 2]
                       + [self.tokenizer.stoi["<eos>"]])
                buf.extend(ids)
                while len(buf) >= self.seq_len + 1:
                    seq = buf[:self.seq_len + 1]
                    buf = buf[self.seq_len:]
                    x = torch.tensor(seq[:-1], dtype=torch.long)
                    y = torch.tensor(seq[1:], dtype=torch.long)
                    yield x, y


class Block(nn.Module):
    def __init__(self, d, h, dff, drop):
        super().__init__()
        self.attn = nn.MultiheadAttention(d, h, dropout=drop, batch_first=True)
        self.ln1 = nn.LayerNorm(d)
        self.ln2 = nn.LayerNorm(d)
        self.ff = nn.Sequential(nn.Linear(d, dff), nn.GELU(), nn.Linear(dff, d))
        self.drop = nn.Dropout(drop)

    def forward(self, x, mask):
        a, _ = self.attn(x, x, x, attn_mask=mask, need_weights=False)
        x = self.ln1(x + self.drop(a))
        return self.ln2(x + self.drop(self.ff(x)))


class MweshimiwaGPT(nn.Module):
    def __init__(self, v, d, layers, heads, seq, dff, drop):
        super().__init__()
        self.tok = nn.Embedding(v, d)
        self.pos = nn.Embedding(seq, d)
        self.blocks = nn.ModuleList([Block(d, heads, dff, drop) for _ in range(layers)])
        self.ln = nn.LayerNorm(d)
        self.head = nn.Linear(d, v, bias=False)
        self.head.weight = self.tok.weight  # tied embeddings
        self.register_buffer("mask", torch.triu(torch.ones(seq, seq), diagonal=1).bool())

    def forward(self, x):
        s = x.size(1)
        h = self.tok(x) + self.pos(torch.arange(s, device=x.device)).unsqueeze(0)
        m = self.mask[:s, :s]
        for b in self.blocks:
            h = b(h, m)
        return self.head(self.ln(h))


def schedule(step, total, warmup, lr):
    if step < warmup:
        return lr * step / max(warmup, 1)
    p = (step - warmup) / max(total - warmup, 1)
    return lr * 0.5 * (1 + math.cos(math.pi * p))


def train():
    assert torch.cuda.is_available(), "Enable GPU: Runtime > Change runtime type > T4"
    device = torch.device("cuda")
    print(f"GPU: {torch.cuda.get_device_name(0)}")
    os.makedirs(Config.SAVE_DIR, exist_ok=True)

    tok = Tokenizer()
    vpath = os.path.join(Config.SAVE_DIR, "vocab.json")
    if os.path.exists(vpath):
        print("Loading existing vocab...")
        tok.stoi = json.load(open(vpath, encoding="utf-8"))["stoi"]
        tok.itos = {i: t for t, i in tok.stoi.items()}
    else:
        tok.build_vocab(Config.CORPUS_PATH, Config.VOCAB_SIZE)
        tok.save(vpath)

    model = MweshimiwaGPT(len(tok.stoi), Config.D_MODEL, Config.N_LAYERS,
                      Config.N_HEADS, Config.SEQ_LEN, Config.D_FF,
                      Config.DROPOUT).to(device)
    # Tied embeddings init N(0,1) gives gigantic logits -> initial loss ~150.
    # Scale to std 0.02 so training starts at loss ~ln(vocab) ~ 10.
    torch.nn.init.normal_(model.tok.weight, std=0.02)
    n = sum(p.numel() for p in model.parameters())
    print(f"params={n / 1e6:.1f}M")

    loader = DataLoader(CorpusDataset(Config.CORPUS_PATH, tok, Config.SEQ_LEN),
                        batch_size=Config.BATCH_SIZE, num_workers=0)
    opt = torch.optim.AdamW(model.parameters(), lr=Config.LR, weight_decay=0.01)
    lossf = nn.CrossEntropyLoss(ignore_index=tok.stoi["<pad>"])
    scaler = torch.amp.GradScaler("cuda")

    # Resume: 10 epochs exceeds one free-Colab session, so pick up from the
    # latest per-epoch checkpoint if present. Download checkpoints after each
    # epoch and re-upload them next session to continue seamlessly.
    start_ep = 0
    step = 0
    for ep in range(Config.EPOCHS, 0, -1):
        ckpt = os.path.join(Config.SAVE_DIR, f"pytorch_model_ep{ep}.bin")
        if os.path.exists(ckpt):
            print(f"Resuming from {ckpt}")
            data = torch.load(ckpt, map_location="cpu", weights_only=False)
            model.load_state_dict(data["model_state"])
            if "optimizer_state" in data:
                opt.load_state_dict(data["optimizer_state"])
            step = data.get("global_step", ep * Config.STEPS_PER_EPOCH)
            start_ep = ep  # ep is 1-indexed completed count; loop continues after it
            break
    model.to(device)

    total = Config.EPOCHS * Config.STEPS_PER_EPOCH
    model.train()
    for ep in range(start_ep, Config.EPOCHS):
        print(f"\n=== Epoch {ep + 1}/{Config.EPOCHS} ===")
        tot = 0.0
        nb = 0
        opt.zero_grad()
        for bi, (x, y) in enumerate(loader):
            if bi >= Config.STEPS_PER_EPOCH * Config.ACCUM_STEPS:
                break
            x, y = x.to(device, non_blocking=True), y.to(device, non_blocking=True)
            with torch.amp.autocast("cuda"):
                loss = lossf(model(x).reshape(-1, len(tok.stoi)), y.reshape(-1))
                loss = loss / Config.ACCUM_STEPS
            scaler.scale(loss).backward()
            tot += loss.item() * Config.ACCUM_STEPS
            nb += 1
            if (bi + 1) % Config.ACCUM_STEPS == 0:
                lr = schedule(step, total, Config.WARMUP_STEPS, Config.LR)
                for g in opt.param_groups:
                    g["lr"] = lr
                scaler.unscale_(opt)
                torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
                scaler.step(opt)
                scaler.update()
                opt.zero_grad()
                step += 1
                if step % Config.LOG_EVERY == 0:
                    print(f"  step {step:,} loss={tot / nb:.3f} lr={lr:.2e}", flush=True)
        print(f"Epoch {ep + 1} loss={tot / max(nb, 1):.3f}")
        torch.save({"model_state": model.state_dict(),
                    "optimizer_state": opt.state_dict(),
                    "global_step": step,
                    "config": {"vocab": len(tok.stoi), "d": Config.D_MODEL,
                               "layers": Config.N_LAYERS, "heads": Config.N_HEADS,
                               "seq": Config.SEQ_LEN}},
                   os.path.join(Config.SAVE_DIR, f"pytorch_model_ep{ep + 1}.bin"))

    torch.save({"model_state": model.state_dict(),
                "config": {"vocab": len(tok.stoi), "d": Config.D_MODEL,
                           "layers": Config.N_LAYERS, "heads": Config.N_HEADS,
                           "seq": Config.SEQ_LEN}},
               os.path.join(Config.SAVE_DIR, "pytorch_model.bin"))

    # ONNX export (input name "input" — matches OnnxBackend.kt)
    model.eval().cpu()
    dummy = torch.ones(1, Config.SEQ_LEN - 1, dtype=torch.long)
    torch.onnx.export(model, dummy,
                      os.path.join(Config.SAVE_DIR, "model.onnx"),
                      input_names=["input"], output_names=["logits"],
                      dynamic_axes={"input": {0: "batch"}, "logits": {0: "batch"}},
                      opset_version=18, dynamo=False)
    print("Saved pytorch_model.bin + model.onnx + vocab.json")
    return model, tok


def demo(model, tok):
    model.eval()
    device = next(model.parameters()).device
    for p in ["<user> hello", "<user> habari yako", "<user> who are you",
              "<user> niaje", "<user> what can you do"]:
        ids = ([tok.stoi["<bos>"]] + tok.encode(p)[-20:])
        with torch.no_grad():
            for _ in range(Config.MAX_NEW_TOKENS):
                xi = torch.tensor([ids[-(Config.SEQ_LEN - 1):]], dtype=torch.long, device=device)
                nxt = int(model(xi)[0, -1].argmax())
                if nxt == tok.stoi["<eos>"]:
                    break
                ids.append(nxt)
        print(f"GEN {p} -> {tok.decode(ids)}", flush=True)


if __name__ == "__main__":
    m, t = train()
    demo(m, t)
    print(f"\nDONE. Download from {Config.SAVE_DIR}: pytorch_model.bin, model.onnx, vocab.json")
