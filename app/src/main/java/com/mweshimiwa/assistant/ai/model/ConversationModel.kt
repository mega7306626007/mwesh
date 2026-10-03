package com.mweshimiwa.assistant.ai.model

interface ConversationModel {

    suspend fun initialize()

    suspend fun generate(request: ConversationRequest): ModelResponse

    suspend fun generateStreaming(
        request: ConversationRequest,
        onToken: (String) -> Unit
    ): ModelResponse

    fun isReady(): Boolean

    fun cancel()

    fun release()

    fun getState(): ModelState

    fun getMetadata(): ModelMetadata
}
