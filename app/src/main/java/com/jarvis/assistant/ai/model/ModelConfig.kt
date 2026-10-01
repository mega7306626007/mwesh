package com.jarvis.assistant.ai.model

data class ModelConfig(
    val modelId: String = "jarvis-custom-v1",
    val modelVersion: String = "1.0.0",
    val backend: InferenceBackendType = InferenceBackendType.MOCK,
    val modelPath: String = "",
    val contextLength: Int = 4096,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val maxOutputTokens: Int = 512,
    val repetitionPenalty: Float = 1.1f
)

enum class InferenceBackendType {
    MOCK,
    GGUF,
    ONNX,
    TFLITE
}
