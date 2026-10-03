package com.mweshimiwa.assistant.conversation.chat

import java.util.UUID

data class ConversationMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val tokensGenerated: Int = 0
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
    ERROR
}
