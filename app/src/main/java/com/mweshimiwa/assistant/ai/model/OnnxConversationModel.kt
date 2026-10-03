package com.mweshimiwa.assistant.ai.model

import android.content.Context
import com.mweshimiwa.assistant.ai.backend.OnnxBackend
import com.mweshimiwa.assistant.ai.inference.ModelInferenceEngine
import com.mweshimiwa.assistant.core.logging.MweshimiwaLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Your own LLM plugged into the app's [ConversationModel] contract (no cloud APIs).
 * Wraps [ModelInferenceEngine] + [OnnxBackend] (models/model.onnx in assets).
 * Falls back to [MockConversationModel] if the real model fails to load,
 * so the app always boots.
 */
class OnnxConversationModel(
    private val appContext: Context,
    private val config: ModelConfig = ModelConfig(
        modelId = "mweshimiwa-custom-v1",
        modelVersion = "1.0.0",
        backend = InferenceBackendType.ONNX,
        modelPath = "models/model.onnx",
        contextLength = 4096
    ),
    private val fallback: ConversationModel = MockConversationModel(ModelConfig())
) : ConversationModel {

    private val backend = OnnxBackend()
    private val engine = ModelInferenceEngine(backend)
    private val mutex = Mutex()

    @Volatile
    private var state: ModelState = ModelState.UNINITIALIZED

    private var metadata = ModelMetadata(
        name = "Mweshimiwa Custom v1 (on-device)",
        version = config.modelVersion,
        backend = InferenceBackendType.ONNX,
        format = "onnx",
        quantization = "none",
        contextLength = config.contextLength
    )

    @Volatile
    private var usingFallback = false

    override suspend fun initialize() {
        state = ModelState.LOADING
        backend.attachContext(appContext)
        val start = System.currentTimeMillis()
        val ok = try {
            engine.loadModel(config.modelPath)
        } catch (e: Exception) {
            MweshimiwaLogger.e("[OnnxModel] load threw: ${e.message}")
            false
        }
        val elapsed = System.currentTimeMillis() - start
        if (ok && backend.isLoaded()) {
            usingFallback = false
            metadata = metadata.copy(
                modelSizeBytes = modelSizeOf(config.modelPath),
                loadTimeMs = elapsed
            )
            state = ModelState.READY
            MweshimiwaLogger.i("[OnnxModel] READY in ${elapsed}ms vocab=${backend.vocabSize()}")
        } else {
            MweshimiwaLogger.e("[OnnxModel] real model failed to load — using mock fallback")
            usingFallback = true
            fallback.initialize()
            state = fallback.getState()
            metadata = fallback.getMetadata()
        }
    }

    override suspend fun generate(request: ConversationRequest): ModelResponse {
        if (usingFallback) return fallback.generate(request)
        return mutex.withLock {
            state = ModelState.GENERATING
            try {
                engine.runInference(request)
            } finally {
                if (state == ModelState.GENERATING) state = ModelState.READY
            }
        }
    }

    override suspend fun generateStreaming(
        request: ConversationRequest,
        onToken: (String) -> Unit
    ): ModelResponse {
        if (usingFallback) return fallback.generateStreaming(request, onToken)
        // OnnxBackend is non-streaming: generate fully, then replay word-by-word
        // so the chat UI still streams. delay() gives natural pacing plus
        // cancellation points (job cancel aborts the replay).
        val response = generate(request)
        if (response.wasCancelled || response.text.isBlank()) return response
        val words = response.text.split(" ")
        val sb = StringBuilder()
        try {
            for (w in words) {
                if (sb.isNotEmpty()) sb.append(' ')
                sb.append(w)
                onToken(sb.toString())
                delay(40)
            }
        } catch (_: Exception) {
            // cancelled mid-replay: keep partial text
        }
        return response.copy(text = sb.toString().ifBlank { response.text })
    }

    override fun isReady(): Boolean =
        if (usingFallback) fallback.isReady() else state == ModelState.READY

    override fun cancel() {
        if (usingFallback) {
            fallback.cancel()
            return
        }
        backend.cancel()
        engine.cancelInference()
        if (state == ModelState.GENERATING) state = ModelState.READY
    }

    override fun release() {
        if (usingFallback) {
            fallback.release()
        } else {
            engine.release()
        }
        state = ModelState.UNINITIALIZED
    }

    override fun getState(): ModelState = state

    override fun getMetadata(): ModelMetadata = metadata

    fun isUsingFallback(): Boolean = usingFallback

    private fun modelSizeOf(modelPath: String): Long {
        return try {
            val f = java.io.File(modelPath)
            if (f.exists()) return f.length()
            var size = 0L
            appContext.assets.open("models/model.onnx").use { ins ->
                val buf = ByteArray(8192)
                while (true) {
                    val n = ins.read(buf)
                    if (n <= 0) break
                    size += n
                }
            }
            size
        } catch (_: Exception) {
            0L
        }
    }
}
