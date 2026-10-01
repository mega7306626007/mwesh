package com.jarvis.assistant

import com.jarvis.assistant.ai.context.PromptBuilder
import com.jarvis.assistant.ai.context.TokenCounter
import com.jarvis.assistant.ai.model.GenerationConfig
import com.jarvis.assistant.ai.model.Message
import com.jarvis.assistant.conversation.personality.JarvisPersonality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptBuilderTest {

    private val personality = JarvisPersonality()
    private val builder = PromptBuilder(personality, TokenCounter)

    @Test
    fun buildIncludesSystemPrompt() {
        val request = builder.build("hello", emptyList())
        assertTrue(request.systemPrompt.contains("Jarvis"))
        assertTrue(request.systemPrompt.contains("Emmanuel"))
    }

    @Test
    fun buildIncludesUserMessage() {
        val request = builder.build("what is the weather", emptyList())
        assertEquals("what is the weather", request.userMessage)
    }

    @Test
    fun buildIncludesHistoryWhenItFits() {
        val history = listOf(
            Message("user", "earlier question"),
            Message("assistant", "earlier answer")
        )
        val request = builder.build("latest question", history)
        assertEquals(2, request.messages.size)
        assertEquals("earlier question", request.messages[0].content)
    }

    @Test
    fun buildDropsOldHistoryWhenOverBudget() {
        val history = (1..50).flatMap {
            listOf(
                Message("user", "question $it with a fairly long body of text"),
                Message("assistant", "answer $it with a fairly long body of text")
            )
        }
        val request = builder.build("final question", history, contextTokenLimit = 1000)
        val totalTokens = TokenCounter.countConversationTokens(request.messages)
        assertTrue(totalTokens < 1000)
        assertTrue(request.messages.size < history.size)
    }

    @Test
    fun buildKeepsMostRecentMessages() {
        val history = (1..20).map { Message("user", "question number $it") }
        val request = builder.build("now", history, contextTokenLimit = 2000)
        assertTrue(request.messages.isNotEmpty())
        assertEquals("question number 20", request.messages.last().content)
    }

    @Test
    fun buildPropagatesGenerationConfig() {
        val config = GenerationConfig(temperature = 0.1f, maxTokens = 42, topP = 0.3f)
        val request = builder.build("hi", emptyList(), config)
        assertEquals(0.1f, request.generationConfig.temperature, 0.0001f)
        assertEquals(42, request.generationConfig.maxTokens)
        assertEquals(0.3f, request.generationConfig.topP, 0.0001f)
    }

    @Test
    fun longUserMessageIsTruncated() {
        val longMessage = "word ".repeat(3000)
        val request = builder.build(longMessage, emptyList())
        assertTrue(request.userMessage.length < longMessage.length)
        assertTrue(TokenCounter.countTokens(request.userMessage) <= 1000)
    }

    @Test
    fun shortUserMessageUnchanged() {
        val request = builder.build("short message", emptyList())
        assertEquals("short message", request.userMessage)
    }

    @Test
    fun truncateToTokensPreservesShortText() {
        val text = "hello world"
        assertEquals(text, builder.truncateToTokens(text, 100))
    }

    @Test
    fun truncateToTokensCutsLongText() {
        val text = "word ".repeat(500)
        val truncated = builder.truncateToTokens(text, 50)
        assertTrue(truncated.length < text.length)
        assertTrue(TokenCounter.countTokens(truncated) <= 50)
    }

    @Test
    fun truncateToTokensExactFit() {
        val text = "one two three"
        assertEquals(text, builder.truncateToTokens(text, 3))
    }

    @Test
    fun buildWithEmptyHistory() {
        val request = builder.build("hello", emptyList())
        assertTrue(request.messages.isEmpty())
        assertEquals("hello", request.userMessage)
    }

    @Test
    fun systemPromptStaysConstant() {
        val first = builder.build("one", emptyList())
        val second = builder.build("two", emptyList())
        assertEquals(first.systemPrompt, second.systemPrompt)
    }

    @Test
    fun contextTokenLimitAffectsHistoryInclusion() {
        val history = (1..30).map { Message("user", "message $it with some tokens") }
        val small = builder.build("q", history, contextTokenLimit = 500)
        val large = builder.build("q", history, contextTokenLimit = 4096)
        assertTrue(large.messages.size >= small.messages.size)
    }
}
