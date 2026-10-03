package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.context.ContextManager
import com.mweshimiwa.assistant.ai.model.ConversationModel
import com.mweshimiwa.assistant.ai.model.ConversationRequest
import com.mweshimiwa.assistant.ai.model.ModelMetadata
import com.mweshimiwa.assistant.ai.model.ModelResponse
import com.mweshimiwa.assistant.ai.model.ModelState
import com.mweshimiwa.assistant.conversation.chat.ConversationManager
import com.mweshimiwa.assistant.conversation.chat.MessageRole
import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamingTest {

    private class FakeStreamingModel(
        private val chunks: List<String> = listOf("Hello", " there", "!"),
        private val chunkDelayMs: Long = 20,
        private val fail: Boolean = false
    ) : ConversationModel {
        var cancelled = false
        var initializeCalled = false
        var releaseCalled = false
        var lastRequest: ConversationRequest? = null

        override suspend fun initialize() {
            initializeCalled = true
        }

        override suspend fun generate(request: ConversationRequest): ModelResponse {
            lastRequest = request
            if (fail) throw RuntimeException("generation failed")
            return ModelResponse(
                text = chunks.joinToString(""),
                tokensGenerated = chunks.size,
                generationTimeMs = 100,
                tokensPerSecond = 50f
            )
        }

        override suspend fun generateStreaming(
            request: ConversationRequest,
            onToken: (String) -> Unit
        ): ModelResponse {
            lastRequest = request
            if (fail) throw RuntimeException("streaming failed")
            for (chunk in chunks) {
                delay(chunkDelayMs)
                onToken(chunk)
            }
            return ModelResponse(
                text = chunks.joinToString(""),
                tokensGenerated = chunks.size,
                generationTimeMs = 100,
                tokensPerSecond = 50f
            )
        }

        override fun isReady(): Boolean = true
        override fun cancel() {
            cancelled = true
        }
        override fun release() {
            releaseCalled = true
        }
        override fun getState(): ModelState = ModelState.READY
        override fun getMetadata(): ModelMetadata = ModelMetadata()
    }

    private fun managerWith(model: ConversationModel): Pair<ConversationManager, ContextManager> {
        val contextManager = ContextManager(MweshimiwaPersonality())
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return ConversationManager(model, contextManager, scope) to contextManager
    }

    private fun waitFor(timeoutMs: Long = 5000, condition: () -> Boolean) = runBlocking {
        withTimeout(timeoutMs) {
            while (!condition()) {
                delay(10)
            }
        }
    }

    @Test
    fun streamingProducesPartialAndFinalContent() {
        val model = FakeStreamingModel(listOf("Hello", " world", "!"))
        val (manager, _) = managerWith(model)
        manager.sendMessage("say hello")
        waitFor {
            manager.uiState.value.messages.lastOrNull { it.role == MessageRole.ASSISTANT && !it.isStreaming } != null
        }
        val assistantMessages = manager.uiState.value.messages.filter { it.role == MessageRole.ASSISTANT }
        assertEquals(1, assistantMessages.size)
        assertEquals("Hello world!", assistantMessages[0].content)
        assertFalse(assistantMessages[0].isStreaming)
    }

    @Test
    fun streamingUpdatesTokensPerSecond() {
        val model = FakeStreamingModel()
        val (manager, _) = managerWith(model)
        manager.sendMessage("hi")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        assertTrue(manager.uiState.value.tokensPerSecond > 0f)
    }

    @Test
    fun intermediateStreamingContentObserved() {
        val model = FakeStreamingModel(listOf("a", "b", "c", "d", "e"), chunkDelayMs = 30)
        val (manager, _) = managerWith(model)
        manager.sendMessage("stream")
        val observed = mutableListOf<String>()
        runBlocking {
            withTimeout(5000) {
                while (true) {
                    val content = manager.uiState.value.messages.lastOrNull { it.role == MessageRole.ASSISTANT }?.content
                    if (content != null && content.isNotEmpty() && content !in observed) {
                        observed.add(content)
                    }
                    if (content == "abcde") break
                    delay(5)
                }
            }
        }
        assertTrue(observed.size >= 2)
        assertEquals("abcde", observed.last())
    }

    @Test
    fun cancelMidStreamFinalizesMessage() {
        val model = FakeStreamingModel((1..100).map { "chunk$it " }, chunkDelayMs = 10)
        val (manager, _) = managerWith(model)
        manager.sendMessage("long stream")
        waitFor {
            (manager.uiState.value.messages.lastOrNull { it.role == MessageRole.ASSISTANT }?.content?.length ?: 0) > 5
        }
        manager.cancelGeneration()
        assertTrue(model.cancelled)
        val last = manager.uiState.value.messages.last()
        assertEquals(MessageRole.ASSISTANT, last.role)
        assertFalse(last.isStreaming)
        assertEquals(ModelState.READY, manager.uiState.value.modelState)
    }

    @Test
    fun streamingErrorProducesErrorMessage() {
        val model = FakeStreamingModel(fail = true)
        val (manager, _) = managerWith(model)
        manager.sendMessage("boom")
        waitFor { manager.uiState.value.modelState == ModelState.ERROR }
        val errorMessage = manager.uiState.value.messages.lastOrNull { it.role == MessageRole.ERROR }
        assertTrue(errorMessage != null)
        assertEquals("I apologize, but I encountered an issue. Please try again.", errorMessage!!.content)
    }

    @Test
    fun cancelDuringSilentGenerationCompletes() {
        val model = FakeStreamingModel()
        val (manager, _) = managerWith(model)
        manager.sendMessage("hi")
        waitFor { manager.uiState.value.modelState == ModelState.GENERATING }
        manager.cancelGeneration()
        assertEquals(ModelState.READY, manager.uiState.value.modelState)
    }

    @Test
    fun requestContainsSystemPromptAndUserMessage() {
        val model = FakeStreamingModel()
        val (manager, _) = managerWith(model)
        manager.sendMessage("test request")
        waitFor { model.lastRequest != null }
        val request = model.lastRequest!!
        assertTrue(request.systemPrompt.contains("Mweshimiwa"))
        assertEquals("test request", request.userMessage)
    }

    @Test
    fun blankMessageDoesNotTriggerGeneration() {
        val model = FakeStreamingModel()
        val (manager, _) = managerWith(model)
        manager.sendMessage("   ")
        assertEquals(0, manager.uiState.value.messages.size)
    }
}
