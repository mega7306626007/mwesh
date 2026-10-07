"""Mweshimiwa model core: tokenizer, dataset, pre-LN GPT. Shared by trainers."""
import json
import math
import re
from collections import Counter

import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.utils.data import IterableDataset

TAG_RE = re.compile(r"<user>|<mweshimiwa>|[\w']+|[.,!?;:]")
SPECIAL = ["<pad>", "<unk>", "<bos>", "<eos>"]


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
        print(f"Building vocab from {corpus_path}...", flush=True)
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
                if n % 100000 == 0:
                    print(f"  {n:,} lines...", flush=True)
        print(f"  lines={n:,} unique={len(counter):,}", flush=True)
        most = [w for w, _ in counter.most_common(max_vocab - len(SPECIAL))]
        self.stoi = {t: i for i, t in enumerate(SPECIAL + most)}
        self.itos = {i: t for t, i in self.stoi.items()}
        print(f"  vocab={len(self.stoi):,}", flush=True)

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
                    yield (torch.tensor(seq[:-1], dtype=torch.long),
                           torch.tensor(seq[1:], dtype=torch.long))


class Block(nn.Module):
    """Pre-LN transformer block (post-LN freezes deep nets: ~30x/layer)."""

    def __init__(self, d, h, dff, drop):
        super().__init__()
        self.attn = nn.MultiheadAttention(d, h, dropout=drop,
                                          batch_first=True)
        self.ln1 = nn.LayerNorm(d)
        self.ln2 = nn.LayerNorm(d)
        self.ff = nn.Sequential(nn.Linear(d, dff), nn.GELU(),
                               nn.Linear(dff, d))
        self.drop = nn.Dropout(drop)

    def forward(self, x, mask):
        a, _ = self.attn(self.ln1(x), self.ln1(x), self.ln1(x),
                         attn_mask=mask, need_weights=False)
        x = x + self.drop(a)
        return x + self.drop(self.ff(self.ln2(x)))


class JarvisGPT(nn.Module):
    def __init__(self, v, d, layers, heads, seq, dff, drop):
        super().__init__()
        self.tok = nn.Embedding(v, d)
        self.pos = nn.Embedding(seq, d)
        self.blocks = nn.ModuleList(
            [Block(d, heads, dff, drop) for _ in range(layers)])
        self.ln = nn.LayerNorm(d)
        self.head = nn.Linear(d, v, bias=False)
        self.head.weight = self.tok.weight  # tied embeddings
        self.register_buffer(
            "mask", torch.triu(torch.ones(seq, seq), diagonal=1).bool())

    def forward(self, x):
        s = x.size(1)
        h = self.tok(x) + self.pos(
            torch.arange(s, device=x.device)).unsqueeze(0)
        m = self.mask[:s, :s]
        for b in self.blocks:
            h = b(h, m)
        return self.head(self.ln(h))
