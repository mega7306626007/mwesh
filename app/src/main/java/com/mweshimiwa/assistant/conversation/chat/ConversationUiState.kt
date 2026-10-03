package com.mweshimiwa.assistant.conversation.chat

import com.mweshimiwa.assistant.ai.model.ModelState
import com.mweshimiwa.assistant.ai.model.ModelMetadata

data class ConversationUiState(
    val messages: List<ConversationMessage> = emptyList(),
    val modelState: ModelState = ModelState.UNINITIALIZED,
    val modelMetadata: ModelMetadata = ModelMetadata(),
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentInput: String = "",
    val lastError: String? = null,
    val tokensPerSecond: Float = 0f
)
