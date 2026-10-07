"""HUGE training: 1024d/6L/16H (~83M) on v4 corpus, fp32 CPU.
Usage: python3 train_huge.py [total_steps]
Resumes from latest checkpoint automatically. Checkpoints every 60 steps
(keeps last 3). NaN guard aborts without saving poison.
"""
import glob
import json
import math
import os
import sys
import time

import torch
import torch.nn as nn
from torch.utils.data import DataLoader

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import model_core as M

HERE = os.path.dirname(os.path.abspath(__file__))
CORPUS = os.path.join(os.path.dirname(HERE), "mweshimiwa-corpus-v4.jsonl")
SAVE = os.path.join(HERE, "huge-v3")
TOTAL_STEPS = int(sys.argv[1]) if len(sys.argv) > 1 else 31500

D_MODEL, N_LAYERS, N_HEADS, SEQ_LEN, D_FF = 1024, 6, 16, 64, 4096
BATCH, LR = 8, 2e-4
CKPT_EVERY = 60

os.makedirs(SAVE, exist_ok=True)
torch.set_num_threads(4)

tok = M.Tokenizer()
vpath = os.path.join(SAVE, "vocab.json")
if os.path.exists(vpath):
    tok.stoi = json.load(open(vpath, encoding="utf-8"))["stoi"]
    tok.itos = {i: t for t, i in tok.stoi.items()}
    print("vocab loaded", len(tok.stoi), flush=True)
else:
    tok.build_vocab(CORPUS, 8000)
    tok.save(vpath)

model = M.JarvisGPT(len(tok.stoi), D_MODEL, N_LAYERS, N_HEADS,
                    SEQ_LEN, D_FF, 0.1)
torch.nn.init.normal_(model.tok.weight, std=0.02)
print(f"params={sum(p.numel() for p in model.parameters())/1e6:.1f}M",
      flush=True)

step = 0
steps = sorted(glob.glob(os.path.join(SAVE, "ckpt_step_*.bin")),
               key=lambda p: int(p.split("_step_")[1].split(".")[0]))
if steps:
    d = torch.load(steps[-1], map_location="cpu", weights_only=False)
    model.load_state_dict(d["model_state"])
    step = d.get("global_step", 0)
    print(f"resumed step{step}", flush=True)

loader = DataLoader(M.CorpusDataset(CORPUS, tok, SEQ_LEN),
                    batch_size=BATCH, num_workers=0)
opt = torch.optim.AdamW(model.parameters(), lr=LR, weight_decay=0.01)
lossf = nn.CrossEntropyLoss(ignore_index=tok.stoi["<pad>"])

count_path = os.path.join(SAVE, "windows_per_pass.txt")
if os.path.exists(count_path):
    W = int(open(count_path).read().strip())
else:
    W = sum(1 for _ in M.CorpusDataset(CORPUS, tok, SEQ_LEN))
    open(count_path, "w").write(str(W))
print(f"windows/pass={W}", flush=True)


def window_stream():
    while True:
        for x, y in loader:
            yield x, y


model.train()
stream = window_stream()
for _ in range(step % W if W else 0):
    next(stream)
print("stream ready", flush=True)
t0 = time.time()
tot, nb = 0.0, 0
while step < TOTAL_STEPS:
    x, y = next(stream)
    opt.zero_grad()
    loss = lossf(model(x).reshape(-1, len(tok.stoi)), y.reshape(-1))
    lv = loss.item()
    if not math.isfinite(lv):
        print(f"NON-FINITE at step {step}; aborting WITHOUT saving",
              flush=True)
        sys.exit(1)
    if lv > 15.0:
        print(f"spike skip step {step} loss={lv:.1f}", flush=True)
        continue
    loss.backward()
    torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
    opt.step()
    tot += lv
    nb += 1
    step += 1
    if step % CKPT_EVERY == 0:
        torch.save({"model_state": model.state_dict(), "global_step": step},
                   os.path.join(SAVE, f"ckpt_step_{step}.bin"))
        olds = sorted(
            glob.glob(os.path.join(SAVE, "ckpt_step_*.bin")),
            key=lambda p: int(p.split("_step_")[1].split(".")[0]))
        for old in olds[:-3]:
            try:
                os.remove(old)
            except OSError:
                pass
        if step % W < CKPT_EVERY:
            torch.save({"model_state": model.state_dict(),
                        "global_step": step},
                       os.path.join(SAVE, f"model_ep{step // max(W, 1)}.bin"))
        dt = time.time() - t0
        print(f"step {step} loss={tot / max(nb, 1):.3f} "
              f"elapsed={dt / 60:.0f}m", flush=True)

torch.save({"model_state": model.state_dict()},
           os.path.join(SAVE, "pytorch_model.bin"))
print("done", flush=True)
