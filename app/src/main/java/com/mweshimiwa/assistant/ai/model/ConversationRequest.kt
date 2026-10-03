package com.mweshimiwa.assistant.ai.model

data class ConversationRequest(
    val systemPrompt: String,
    val messages: List<Message>,
    val userMessage: String,
    val generationConfig: GenerationConfig = GenerationConfig()
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val maxTokens: Int = 512,
    val repetitionPenalty: Float = 1.1f
)

data class Message(
    val role: String,
    val content: String
)
