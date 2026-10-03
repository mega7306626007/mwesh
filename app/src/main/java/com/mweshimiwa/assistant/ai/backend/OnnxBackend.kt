package com.mweshimiwa.assistant.ai.backend

import android.content.Context
import com.mweshimiwa.assistant.ai.model.ConversationRequest
import com.mweshimiwa.assistant.ai.model.InferenceBackendType
import com.mweshimiwa.assistant.ai.model.ModelResponse
import com.mweshimiwa.assistant.core.logging.MweshimiwaLogger
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.nio.LongBuffer
import kotlin.math.exp
import kotlin.random.Random

/**
 * Real on-device inference for your own Mweshimiwa LLM (no cloud APIs).
 * Loads models/model.onnx + models/vocab.json from assets (copied to cache on first run),
 * tokenizes with the same regex as training (<user>|<mweshimiwa>|words|punct, lowercased),
 * and autoregressively generates with greedy/top-k sampling.
 *
 * Call [attachContext] once from MweshimiwaApplication before load(), or pass an
 * absolute file path to [load] to skip assets entirely.
 */
class OnnxBackend : InferenceBackend {

    private var appContext: Context? = null
    private var env: OrtEnvironment? = null
    private var session: OrtSession? = null
    private var stoi: Map<String, Int> = emptyMap()
    private var itos: Map<Int, String> = emptyMap()
    private var seqLen: Int = 39 // trained SEQ-1; always feeds exactly this many ids
    private var modelPath: String = ""
    private var cancelled = false
    private var executionProvider = "CPU"
    private var intraOpThreads: Int = Runtime.getRuntime().availableProcessors().coerceAtMost(4)

    fun attachContext(context: Context) {
        appContext = context.applicationContext
    }

    override suspend fun load(modelPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            closeSession()
            val modelFile = resolveModelFile(modelPath)
            val vocabMap = loadVocabFor(modelPath, modelFile)
            if (vocabMap.isEmpty()) {
                MweshimiwaLogger.e("[ONNX] vocab not found for $modelPath")
                return@withContext false
            }
            stoi = vocabMap
            itos = vocabMap.entries.associate { (k, v) -> v to k }
            env = OrtEnvironment.getEnvironment()
            val opts = OrtSession.SessionOptions().apply {
                setIntraOpNumThreads(intraOpThreads)
                setInterOpNumThreads(1)
                setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            }
            session = env!!.createSession(modelFile.absolutePath, opts)
            // Derive fixed input seq length from the model graph so bigger
            // future models (e.g. SEQ-1 = 63) work without code changes.
            try {
                val info = session!!.inputInfo["input"]?.info
                if (info is TensorInfo) {
                    val shape = info.shape
                    if (shape.size >= 2 && shape[1] > 0) {
                        seqLen = shape[1].toInt()
                    }
                }
                MweshimiwaLogger.i("[ONNX] input seq=$seqLen")
            } catch (_: Exception) { }
            this@OnnxBackend.modelPath = modelFile.absolutePath
            MweshimiwaLogger.i("[ONNX] Loaded ${modelFile.name} vocab=${stoi.size} provider=$executionProvider")
            true
        } catch (e: Exception) {
            MweshimiwaLogger.e("[ONNX] load failed: ${e.message}")
            false
        }
    }

    override suspend fun infer(request: ConversationRequest): ModelResponse = withContext(Dispatchers.IO) {
        val sess = session
        if (sess == null || stoi.isEmpty()) {
            return@withContext ModelResponse(text = "Error: Model not loaded", wasCancelled = true)
        }
        cancelled = false
        val start = System.currentTimeMillis()
        val cfg = request.generationConfig
        val maxNew = cfg.maxTokens.coerceIn(1, 120).coerceAtMost(60)
        val prompt = "<user> ${request.userMessage.trim()} <mweshimiwa>"
        val ids = encode(prompt).takeLast(seqLen).toMutableList()
        val generated = mutableListOf<Int>()
        val pad = stoi["<pad>"] ?: 0
        val eos = stoi["<eos>"] ?: 3
        val unk = stoi["<unk>"] ?: 1

        repeat(maxNew) {
            if (cancelled) return@repeat
            val window = (ids.takeLast(seqLen).let { w ->
                if (w.size < seqLen) List(seqLen - w.size) { pad } + w else w
            })
            val logits = runStep(sess, window) ?: return@repeat
            // Repetition penalty: our training windows pack multiple turns,
            // so the model loves echoing prompt tokens. Suppress anything
            // already in context to force it forward into the answer.
            for (id in window) {
                if (id >= 0 && id < logits.size) logits[id] -= 1.0f
            }
            for (id in generated) {
                if (id >= 0 && id < logits.size) logits[id] -= 1.0f
            }
            var next = sampleNext(logits, cfg.topK, cfg.temperature)
            if (next !in itos) next = unk
            if (next == eos) return@repeat
            ids.add(next)
            generated.add(next)
        }
        val text = decode(generated).trim().ifBlank { "(no output — train longer)" }
        val elapsed = System.currentTimeMillis() - start
        ModelResponse(
            text = text,
            tokensGenerated = generated.size,
            generationTimeMs = elapsed,
            tokensPerSecond = if (elapsed > 0) generated.size * 1000f / elapsed else 0f,
            wasCancelled = cancelled
        )
    }

    override fun cancel() {
        cancelled = true
        MweshimiwaLogger.i("[ONNX] Inference cancelled")
    }

    override fun release() {
        closeSession()
        stoi = emptyMap()
        itos = emptyMap()
        modelPath = ""
        MweshimiwaLogger.i("[ONNX] Model released")
    }

    override fun supportsStreaming(): Boolean = false

    fun getBackendType(): InferenceBackendType = InferenceBackendType.ONNX
    fun isLoaded(): Boolean = session != null
    fun getModelPath(): String = modelPath
    fun vocabSize(): Int = stoi.size

    fun setExecutionProvider(provider: String) {
        executionProvider = provider
    }

    fun getExecutionProvider(): String = executionProvider
    fun getOptimizationLevel(): String = "all"
    fun setOptimizationLevel(@Suppress("UNUSED_PARAMETER") level: String) { }

    fun getIntraOpNumThreads(): Int = intraOpThreads
    fun setIntraOpNumThreads(threads: Int) {
        intraOpThreads = threads.coerceIn(1, 8)
    }

    // ---- internals ----

    private fun resolveModelFile(requested: String): File {
        File(requested).takeIf { it.isAbsolute && it.exists() }?.let { return it }
        val ctx = appContext
        val assetPath = when {
            requested.isBlank() -> "models/model.onnx"
            File(requested).exists() -> return File(requested)
            else -> requested.removePrefix("assets/").let {
                if (it.startsWith("models/")) it else "models/${it.substringAfterLast('/')}"
            }
        }
        requireNotNull(ctx) { "No Context attached and no absolute path: $requested" }
        val out = File(File(ctx.cacheDir, "models"), File(assetPath).name)
        if (!out.exists()) {
            out.parentFile?.mkdirs()
            ctx.assets.open(assetPath).use { ins ->
                out.outputStream().use { outs -> ins.copyTo(outs) }
            }
        }
        return out
    }

    private fun loadVocabFor(requested: String, modelFile: File): Map<String, Int> {
        // 1. sibling vocab.json next to an absolute model file
        val sibling = File(modelFile.parentFile, "vocab.json")
        if (sibling.exists()) return readVocab(sibling.readText())
        // 2. bundled assets/models/vocab.json
        val ctx = appContext ?: return emptyMap()
        return try {
            ctx.assets.open("models/vocab.json").bufferedReader().use { r ->
                readVocab(r.readText())
            }
        } catch (_: Exception) {
            MweshimiwaLogger.e("[ONNX] models/vocab.json missing from assets")
            emptyMap()
        }
    }

    private fun readVocab(json: String): Map<String, Int> {
        val obj = JSONObject(json).getJSONObject("stoi")
        val map = HashMap<String, Int>(obj.length())
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k] = obj.getInt(k)
        }
        return map
    }

    private val tokenRegex = Regex("<user>|<mweshimiwa>|[\\w']+|[.,!?;:]")

    private fun encode(text: String): MutableList<Int> {
        val bos = stoi["<bos>"] ?: 2
        val unk = stoi["<unk>"] ?: 1
        val out = mutableListOf(bos)
        tokenRegex.findAll(text.lowercase()).forEach { m ->
            out.add(stoi[m.value] ?: unk)
        }
        return out
    }

    private fun decode(ids: List<Int>): String {
        val words = ids.map { itos[it] ?: "" }
        // detokenize: no space before punctuation, keep tags readable
        val sb = StringBuilder()
        for (w in words) {
            if (w in setOf(".", ",", "!", "?", ";", ":")) sb.append(w)
            else {
                if (sb.isNotEmpty()) sb.append(' ')
                sb.append(w)
            }
        }
        return sb.toString()
            .replace("<user>", "", ignoreCase = true)
            .replace("<mweshimiwa>", "", ignoreCase = true)
            .replace("<bos>", "").replace("<eos>", "")
            .trim()
    }

    private fun runStep(sess: OrtSession, window: List<Int>): FloatArray? {
        return try {
            val buf = LongBuffer.allocate(window.size)
            window.forEach { buf.put(it.toLong()) }
            buf.rewind()
            val tensor = OnnxTensor.createTensor(
                env, buf, longArrayOf(1, window.size.toLong())
            )
            tensor.use {
                sess.run(mapOf("input" to it)).use { results ->
                    @Suppress("UNCHECKED_CAST")
                    val logits = (results[0].value as Array<Array<FloatArray>>)[0]
                    logits.last()
                }
            }
        } catch (e: Exception) {
            MweshimiwaLogger.e("[ONNX] infer step failed: ${e.message}")
            null
        }
    }

    private fun sampleNext(logits: FloatArray, topK: Int, temperature: Float): Int {
        if (temperature <= 0.05f) {
            return logits.indices.maxByOrNull { logits[it] } ?: 0
        }
        val k = topK.coerceIn(1, logits.size).coerceAtMost(50)
        val top = logits.indices.sortedByDescending { logits[it] }.take(k)
        val temp = temperature.coerceIn(0.1f, 2.0f)
        val exps = top.map { exp((logits[it] / temp).toDouble()) }
        val sum = exps.sum()
        var r = Random.nextDouble() * sum
        for (i in top.indices) {
            r -= exps[i]
            if (r <= 0) return top[i]
        }
        return top.last()
    }

    private fun closeSession() {
        try { session?.close() } catch (_: Exception) { }
        try { env?.close() } catch (_: Exception) { }
        session = null
        env = null
    }
}
