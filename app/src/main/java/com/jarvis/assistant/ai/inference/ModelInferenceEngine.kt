package com.jarvis.assistant.ai.inference

import com.jarvis.assistant.ai.backend.InferenceBackend
import com.jarvis.assistant.ai.model.ConversationRequest
import com.jarvis.assistant.ai.model.ModelResponse

class ModelInferenceEngine(
    private val backend: InferenceBackend
) {
    suspend fun loadModel(modelPath: String): Boolean {
        return backend.load(modelPath)
    }

    suspend fun runInference(request: ConversationRequest): ModelResponse {
        return backend.infer(request)
    }

    suspend fun runInferenceStreaming(
        request: ConversationRequest,
        onToken: (String) -> Unit
    ): ModelResponse {
        if (!backend.supportsStreaming()) {
            val response = backend.infer(request)
            onToken(response.text)
            return response
        }
        return backend.infer(request)
    }

    fun cancelInference() = backend.cancel()

    fun release() = backend.release()
}
