package com.mweshimiwa.assistant.conversation.chat

import com.mweshimiwa.assistant.ai.context.ContextManager
import com.mweshimiwa.assistant.ai.model.ConversationModel
import com.mweshimiwa.assistant.ai.model.ModelState
import com.mweshimiwa.assistant.core.logging.MweshimiwaLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationManager(
    private val model: ConversationModel,
    private val contextManager: ContextManager,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    private var generationJob: Job? = null

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ConversationMessage(role = MessageRole.USER, content = text.trim())
        appendMessage(userMessage)
        contextManager.addMessage("user", text.trim())

        generateResponse(text.trim())
    }

    private fun generateResponse(userText: String) {
        generationJob?.cancel()
        generationJob = scope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(modelState = ModelState.GENERATING) }

                val request = contextManager.buildRequest(userText)
                val streamingMessage = ConversationMessage(
                    role = MessageRole.ASSISTANT,
                    content = "",
                    isStreaming = true
                )
                appendMessage(streamingMessage)

                val response = model.generateStreaming(request) { partialText ->
                    updateStreamingMessage(streamingMessage.id, partialText)
                }

                finalizeStreamingMessage(streamingMessage.id, response.text, response.tokensPerSecond)
                contextManager.addMessage("assistant", response.text)

                updateState {
                    it.copy(
                        modelState = ModelState.READY,
                        tokensPerSecond = response.tokensPerSecond
                    )
                }
            } catch (e: Exception) {
                MweshimiwaLogger.e("Generation failed", e)
                val errorMessage = ConversationMessage(
                    role = MessageRole.ERROR,
                    content = "I apologize, but I encountered an issue. Please try again."
                )
                appendMessage(errorMessage)
                updateState {
                    it.copy(
                        modelState = ModelState.ERROR,
                        lastError = e.message
                    )
                }
            }
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        model.cancel()
        updateState { it.copy(modelState = ModelState.READY) }

        val lastMessage = _uiState.value.messages.lastOrNull()
        if (lastMessage != null && lastMessage.isStreaming) {
            finalizeStreamingMessage(lastMessage.id, lastMessage.content, 0f)
        }
    }

    fun clearConversation() {
        generationJob?.cancel()
        contextManager.clearHistory()
        updateState { it.copy(messages = emptyList(), lastError = null) }
    }

    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages
            .lastOrNull { it.role == MessageRole.USER }
            ?: return

        val lastAssistantIndex = _uiState.value.messages
            .indexOfLast { it.role == MessageRole.ASSISTANT || it.role == MessageRole.ERROR }

        if (lastAssistantIndex >= 0) {
            val trimmedMessages = _uiState.value.messages.subList(0, lastAssistantIndex)
            updateState { it.copy(messages = trimmedMessages) }
        }

        sendMessage(lastUserMessage.content)
    }

    private fun appendMessage(message: ConversationMessage) {
        updateState { it.copy(messages = it.messages + message) }
    }

    private fun updateStreamingMessage(messageId: String, content: String) {
        updateState { state ->
            val updated = state.messages.map { msg ->
                if (msg.id == messageId) msg.copy(content = content) else msg
            }
            state.copy(messages = updated)
        }
    }

    private fun finalizeStreamingMessage(messageId: String, content: String, tps: Float) {
        updateState { state ->
            val updated = state.messages.map { msg ->
                if (msg.id == messageId) msg.copy(
                    content = content,
                    isStreaming = false,
                    tokensGenerated = content.split(" ").size
                ) else msg
            }
            state.copy(messages = updated, tokensPerSecond = tps)
        }
    }

    private fun updateState(update: (ConversationUiState) -> ConversationUiState) {
        _uiState.value = update(_uiState.value)
    }
}
