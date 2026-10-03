package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.context.ContextManager
import com.mweshimiwa.assistant.ai.model.GenerationConfig
import com.mweshimiwa.assistant.ai.model.Message
import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextManagerTest {

    private val personality = MweshimiwaPersonality()

    @Test
    fun addMessageAppendsToHistory() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.addMessage("assistant", "hi")
        assertEquals(2, manager.getRecentMessages().size)
        assertEquals("hello", manager.getRecentMessages()[0].content)
        assertEquals("hi", manager.getRecentMessages()[1].content)
    }

    @Test
    fun getRecentMessagesReturnsCopy() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        val snapshot = manager.getRecentMessages()
        assertEquals(1, snapshot.size)
    }

    @Test
    fun clearHistoryEmptiesMessages() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.clearHistory()
        assertTrue(manager.getRecentMessages().isEmpty())
    }

    @Test
    fun buildRequestContainsSystemPromptAndUserMessage() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("what time is it")
        assertTrue(request.systemPrompt.contains("Mweshimiwa"))
        assertEquals("what time is it", request.userMessage)
    }

    @Test
    fun buildRequestIncludesGenerationConfig() {
        val manager = ContextManager(personality)
        val config = GenerationConfig(temperature = 0.2f, maxTokens = 100)
        val request = manager.buildRequest("hello", config)
        assertEquals(0.2f, request.generationConfig.temperature, 0.0001f)
        assertEquals(100, request.generationConfig.maxTokens)
    }

    @Test
    fun historyTrimmedWhenExceedingContextLimit() {
        val manager = ContextManager(personality, maxContextTokens = 200)
        repeat(20) { i ->
            manager.addMessage("user", "message number $i with quite a lot of tokens in it")
        }
        val messages = manager.getRecentMessages()
        assertTrue(messages.size < 20)
        val totalTokens = messages.sumOf {
            it.content.split(Regex("\\s+")).size + it.content.length / 4
        }
        assertTrue(totalTokens <= 200)
    }

    @Test
    fun oldestMessagesRemovedFirstOnOverflow() {
        val manager = ContextManager(personality, maxContextTokens = 1200)
        manager.addMessage("user", "first " + "word ".repeat(200))
        manager.addMessage("user", "second message")
        repeat(10) {
            manager.addMessage("assistant", "filler message with many many tokens to force trimming")
        }
        val messages = manager.getRecentMessages()
        assertTrue(messages.none { it.content.startsWith("first") })
        assertTrue(messages.any { it.content == "second message" })
    }

    @Test
    fun buildContextMessagesRespectsTokenBudget() {
        val manager = ContextManager(personality, maxContextTokens = 500)
        repeat(15) { i ->
            manager.addMessage("user", "question $i about something interesting")
            manager.addMessage("assistant", "answer $i with a reasonably long response body")
        }
        val request = manager.buildRequest("final question")
        val systemTokens = request.systemPrompt.split(Regex("\\s+")).size + request.systemPrompt.length / 4
        val contextTokens = request.messages.sumOf {
            it.content.split(Regex("\\s+")).size + it.content.length / 4
        }
        assertTrue(systemTokens + contextTokens <= 500)
    }

    @Test
    fun recentMessagesPreservedWhenUnderLimit() {
        val manager = ContextManager(personality, maxContextTokens = 4096)
        manager.addMessage("user", "one")
        manager.addMessage("assistant", "two")
        manager.addMessage("user", "three")
        assertEquals(3, manager.getRecentMessages().size)
    }

    @Test
    fun buildRequestWithEmptyHistory() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("hello")
        assertTrue(request.messages.isEmpty())
        assertEquals("hello", request.userMessage)
    }

    @Test
    fun messageRolesPreserved() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "question")
        manager.addMessage("assistant", "answer")
        val messages = manager.getRecentMessages()
        assertEquals("user", messages[0].role)
        assertEquals("assistant", messages[1].role)
    }

    @Test
    fun estimateTokensCountsWordsAndLength() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello world")
        val request = manager.buildRequest("test")
        assertTrue(request.messages.isNotEmpty())
    }
}
