package com.jarvis.assistant.ai.model

data class ModelResponse(
    val text: String,
    val tokensGenerated: Int = 0,
    val generationTimeMs: Long = 0L,
    val tokensPerSecond: Float = 0f,
    val wasCancelled: Boolean = false
)
