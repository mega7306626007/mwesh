# Mweshimiwa — On-Device Trilingual AI Assistant for Android

Mweshimiwa ("Your Honor" / a respectful address in Swahili) is a private,
offline-first conversational assistant for Android. It chats in
**English, Kiswahili (incl. Sheng), and French**, runs its own
**custom-trained language model fully on-device**, and sends nothing to
any cloud API — ever.

> Formerly codenamed "Jarvis". Every occurrence was renamed to Mweshimiwa
> across code, resources, models, corpora, and docs.

## Highlights

- **Your own LLM, trained from your own data** — a 1.6M-parameter GPT
  (`models/model.onnx`, ~10 MB) trained from scratch on 23k hand-built
  prompt→response pairs. No pretrained weights, no APIs, no tracking.
- **Trilingual by construction** — tokenizer, vocab, and training pairs
  cover EN/SW/FR with Sheng prompts.
- **Rule-based brain + neural personality** — deterministic intent engine
  and 280+ hand-written response banks for reliability, with the ONNX
  model adding generative variety (top-k sampling + repetition penalty).
- **Fully offline** — inference via ONNX Runtime (`onnxruntime-android`),
  models ship in `assets/models/`.

## Project layout

```
app/src/main/java/com/mweshimiwa/assistant/
  MweshimiwaApplication.kt      # app entry, model init
  ai/backend/OnnxBackend.kt     # on-device inference (auto seq-len, top-k)
  ai/model/OnnxConversationModel.kt
  conversation/personality/     # personality + persona
  core/logging/                 # MweshimiwaLogger
  data/                         # database, preferences, repositories
  voice/tts/                    # TTS
app/src/main/assets/models/
  model.onnx                    # trained GPT (input "input", output "logits")
  vocab.json                    # {"stoi": {...}} word vocab + <pad>/<unk>/<bos>/<eos>
  colab_train_*.py              # training engines (Colab GPU + local CPU)
mweshimiwa-corpus-v2/v3.jsonl   # training corpora (v3 is the shipped one)
```

## Build

Requirements: Android Studio (or JDK 17 + Android SDK), ~8 GB free.

```powershell
cd Documents\mwesh
.\gradlew :app:assembleDebug --no-daemon -x test
# APK: app\build\outputs\apk\debug\app-debug.apk
```

Install on-device (enable *Install unknown apps*), open, grant mic/audio
as prompted. All inference stays on the phone.

## Training your own model (same pipeline we used)

1. **Corpus** — every line is one JSON object:
   `{"text": "<user> prompt <mweshimiwa> response"}`.
   Build from the response banks with `build_corpus_v3.py`-style pairing:
   one prompt set per response group so each prompt maps to few responses.
2. **Train** — `colab_train_mweshimiwa.py` on a Colab T4, or the compact
   config on CPU. Tokenizer regex: `<user>|<mweshimiwa>|[\w']+|[.,!?;:]`,
   lowercased; specials `<pad>=0 <unk>=1 <bos>=2 <eos>=3`.
3. **Export** — ONNX opset 18, input name `input`, output `logits`.
4. **Ship** — drop `model.onnx` + `vocab.json` into
   `app/src/main/assets/models/` and rebuild. `OnnxBackend` derives the
   sequence length from the model graph — no code changes needed.

## Data & privacy

- All training material originates from local response banks, story text,
  and templates in this workspace. Nothing is fetched from the internet.
- The app makes zero network calls for inference.

## License

Private project — all rights reserved.
