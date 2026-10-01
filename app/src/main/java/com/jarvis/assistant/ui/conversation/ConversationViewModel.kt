package com.jarvis.assistant.ui.conversation

import androidx.lifecycle.ViewModel
import com.jarvis.assistant.ai.model.ModelState
import com.jarvis.assistant.commands.executor.CommandExecutor
import com.jarvis.assistant.commands.handlers.CalculatorCommandHandler
import com.jarvis.assistant.commands.handlers.TimeCommandHandler
import com.jarvis.assistant.commands.handlers.TimerCommandHandler
import com.jarvis.assistant.commands.router.CommandRouter
import com.jarvis.assistant.conversation.chat.ConversationManager
import com.jarvis.assistant.conversation.chat.ConversationUiState
import com.jarvis.assistant.voice.speech.InputNormalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConversationViewModel(
    private val conversationManager: ConversationManager,
    private val commandExecutor: CommandExecutor
) : ViewModel() {

    private val commandRouter = CommandRouter(
        listOf(
            TimerCommandHandler(),
            CalculatorCommandHandler(),
            TimeCommandHandler()
        )
    )

    val conversationState: StateFlow<ConversationUiState> = conversationManager.uiState

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput.asStateFlow()

    init {
    }

    fun onInputChanged(input: String) {
        _currentInput.value = input
    }

    fun onSendClicked() {
        val input = _currentInput.value.trim()
        if (input.isBlank()) return

        _currentInput.value = ""
        processInput(input)
    }

    fun onVoiceResult(text: String) {
        _currentInput.value = ""
        _isListening.value = false
        processInput(text)
    }

    fun onVoicePartialResult(text: String) {
        _currentInput.value = text
    }

    fun onListeningStateChanged(listening: Boolean) {
        _isListening.value = listening
    }

    fun onStopGeneration() {
        conversationManager.cancelGeneration()
    }

    fun onClearConversation() {
        conversationManager.clearConversation()
    }

    fun onRetryLastMessage() {
        conversationManager.retryLastMessage()
    }

    private fun processInput(rawInput: String) {
        val normalized = InputNormalizer.normalize(rawInput)

        val commandIntent = commandRouter.route(normalized)

        if (commandIntent != null) {
            val result = commandExecutor.execute(
                commandIntent.command.name,
                commandIntent.parameters
            )
            if (result.success) {
                conversationManager.sendMessage("[System: ${result.response}]")
                return
            }
        }

        conversationManager.sendMessage(normalized)
    }

    override fun onCleared() {
        super.onCleared()
        conversationManager.cancelGeneration()
    }
}
