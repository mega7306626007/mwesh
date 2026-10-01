package com.jarvis.assistant.ai.backend

import com.jarvis.assistant.ai.model.ConversationRequest
import com.jarvis.assistant.ai.model.ModelResponse

interface InferenceBackend {
    suspend fun load(modelPath: String): Boolean
    suspend fun infer(request: ConversationRequest): ModelResponse
    fun cancel()
    fun release()
    fun supportsStreaming(): Boolean
}
