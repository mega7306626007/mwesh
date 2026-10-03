package com.mweshimiwa.assistant.ai.model

data class ModelMetadata(
    val name: String = "Mweshimiwa Custom",
    val version: String = "1.0.0",
    val backend: InferenceBackendType = InferenceBackendType.MOCK,
    val format: String = "mock",
    val quantization: String = "none",
    val contextLength: Int = 4096,
    val modelSizeBytes: Long = 0L,
    val loadTimeMs: Long = 0L,
    val tokensPerSecond: Float = 0f
)
