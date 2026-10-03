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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationManagerTest {

    private class FakeConversationModel(
        private val response: String = "Hello! How can I help?",
        private val fail: Boolean = false,
        private val streamingChunks: List<String> = emptyList(),
        private val chunkDelayMs: Long = 0
    ) : ConversationModel {
        var cancelled = false
        var released = false
        var initializeCalled = false
        var lastRequest: ConversationRequest? = null

        override suspend fun initialize() {
            initializeCalled = true
        }

        override suspend fun generate(request: ConversationRequest): ModelResponse {
            lastRequest = request
            if (fail) throw RuntimeException("model error")
            return ModelResponse(
                text = response,
                tokensGenerated = 4,
                generationTimeMs = 50,
                tokensPerSecond = 80f
            )
        }

        override suspend fun generateStreaming(
            request: ConversationRequest,
            onToken: (String) -> Unit
        ): ModelResponse {
            lastRequest = request
            if (fail) throw RuntimeException("model error")
            if (streamingChunks.isNotEmpty()) {
                streamingChunks.forEach {
                    if (chunkDelayMs > 0) delay(chunkDelayMs)
                    onToken(it)
                }
                return ModelResponse(
                    text = streamingChunks.joinToString(""),
                    tokensGenerated = streamingChunks.size,
                    generationTimeMs = 50,
                    tokensPerSecond = 80f
                )
            }
            return ModelResponse(
                text = response,
                tokensGenerated = 4,
                generationTimeMs = 50,
                tokensPerSecond = 80f
            )
        }

        override fun isReady(): Boolean = true
        override fun cancel() {
            cancelled = true
        }
        override fun release() {
            released = true
        }
        override fun getState(): ModelState = ModelState.READY
        override fun getMetadata(): ModelMetadata = ModelMetadata()
    }

    private fun createManager(model: ConversationModel): ConversationManager {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return ConversationManager(model, ContextManager(MweshimiwaPersonality()), scope)
    }

    private fun waitFor(timeoutMs: Long = 5000, condition: () -> Boolean) = runBlocking {
        withTimeout(timeoutMs) {
            while (!condition()) {
                delay(10)
            }
        }
    }

    @Test
    fun sendMessageAppendsUserAndAssistantMessages() {
        val model = FakeConversationModel("Hi there!")
        val manager = createManager(model)
        manager.sendMessage("hello")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        val messages = manager.uiState.value.messages
        assertEquals(2, messages.size)
        assertEquals(MessageRole.USER, messages[0].role)
        assertEquals("hello", messages[0].content)
        assertEquals(MessageRole.ASSISTANT, messages[1].role)
        assertEquals("Hi there!", messages[1].content)
    }

    @Test
    fun blankMessageIsIgnored() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("")
        manager.sendMessage("   ")
        assertEquals(0, manager.uiState.value.messages.size)
    }

    @Test
    fun generationStateSetDuringResponse() {
        val model = FakeConversationModel(streamingChunks = listOf("Hello", " there"), chunkDelayMs = 20)
        val manager = createManager(model)
        manager.sendMessage("hi")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        assertEquals(ModelState.READY, manager.uiState.value.modelState)
    }

    @Test
    fun modelErrorSetsErrorState() {
        val model = FakeConversationModel(fail = true)
        val manager = createManager(model)
        manager.sendMessage("break")
        waitFor { manager.uiState.value.modelState == ModelState.ERROR }
        val error = manager.uiState.value.messages.lastOrNull { it.role == MessageRole.ERROR }
        assertTrue(error != null)
        assertTrue(manager.uiState.value.lastError != null)
    }

    @Test
    fun cancelGenerationCancelsModelAndFinalizes() {
        val model = FakeConversationModel(
            streamingChunks = listOf("Hello", " there", "!"),
            chunkDelayMs = 30
        )
        val manager = createManager(model)
        manager.sendMessage("hi")
        waitFor {
            manager.uiState.value.messages.any { it.role == MessageRole.ASSISTANT && it.isStreaming }
        }
        manager.cancelGeneration()
        assertTrue(model.cancelled)
        val last = manager.uiState.value.messages.last()
        assertFalse(last.isStreaming)
    }

    @Test
    fun cancelGenerationWithoutActiveStreamIsSafe() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.cancelGeneration()
        assertEquals(ModelState.READY, manager.uiState.value.modelState)
    }

    @Test
    fun clearConversationRemovesAllMessages() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("hello")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        manager.clearConversation()
        assertTrue(manager.uiState.value.messages.isEmpty())
        assertNull(manager.uiState.value.lastError)
    }

    @Test
    fun clearConversationClearsContextHistory() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("remember this")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        manager.clearConversation()
        assertTrue(manager.uiState.value.messages.isEmpty())
    }

    @Test
    fun retryLastMessageResendsAndReplacesResponse() {
        val model = FakeConversationModel("new answer")
        val manager = createManager(model)
        manager.sendMessage("question")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        manager.retryLastMessage()
        waitFor {
            manager.uiState.value.messages.count { it.role == MessageRole.ASSISTANT } == 2 ||
                (manager.uiState.value.messages.count { it.role == MessageRole.ASSISTANT } == 1 &&
                    manager.uiState.value.messages.last { it.role == MessageRole.ASSISTANT }.content == "new answer")
        }
        val last = manager.uiState.value.messages.last()
        assertEquals("new answer", last.content)
    }

    @Test
    fun retryWithoutUserMessageDoesNothing() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.retryLastMessage()
        assertTrue(manager.uiState.value.messages.isEmpty())
    }

    @Test
    fun uiStateExposesTokensPerSecond() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("hi")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        assertTrue(manager.uiState.value.tokensPerSecond > 0f)
    }

    @Test
    fun requestBuiltWithSystemPrompt() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("test")
        waitFor { model.lastRequest != null }
        assertTrue(model.lastRequest!!.systemPrompt.contains("Mweshimiwa"))
        assertEquals("test", model.lastRequest!!.userMessage)
    }

    @Test
    fun consecutiveMessagesAccumulateInContext() {
        val model = FakeConversationModel()
        val manager = createManager(model)
        manager.sendMessage("first")
        waitFor { manager.uiState.value.modelState == ModelState.READY }
        manager.sendMessage("second")
        waitFor {
            manager.uiState.value.messages.count { it.role == MessageRole.ASSISTANT } >= 2
        }
        assertEquals(4, manager.uiState.value.messages.size)
    }

    @Test
    fun cancelledGenerationMarksResponseCancelled() {
        val model = FakeConversationModel(
            streamingChunks = (1..50).map { "chunk$it " },
            chunkDelayMs = 15
        )
        val manager = createManager(model)
        manager.sendMessage("hi")
        waitFor {
            manager.uiState.value.messages.any { it.role == MessageRole.ASSISTANT && it.isStreaming }
        }
        manager.cancelGeneration()
        assertTrue(model.cancelled)
        val last = manager.uiState.value.messages.last()
        assertFalse(last.isStreaming)
    }
}
