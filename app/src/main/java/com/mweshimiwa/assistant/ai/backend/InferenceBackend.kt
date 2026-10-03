package com.mweshimiwa.assistant.ai.backend

import com.mweshimiwa.assistant.ai.model.ConversationRequest
import com.mweshimiwa.assistant.ai.model.ModelResponse

interface InferenceBackend {
    suspend fun load(modelPath: String): Boolean
    suspend fun infer(request: ConversationRequest): ModelResponse
    fun cancel()
    fun release()
    fun supportsStreaming(): Boolean
}
