package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.context.TokenCounter
import com.mweshimiwa.assistant.ai.model.Message
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenCounterTest {

    @Test
    fun emptyTextHasZeroTokens() {
        assertEquals(0, TokenCounter.countTokens(""))
        assertEquals(0, TokenCounter.countTokens("   "))
        assertEquals(0, TokenCounter.estimateTokens(""))
    }

    @Test
    fun simpleWordCounting() {
        assertEquals(2, TokenCounter.countTokens("hello world"))
        assertEquals(1, TokenCounter.countTokens("hello"))
        assertEquals(4, TokenCounter.countTokens("one two three four"))
    }

    @Test
    fun punctuationAddsTokens() {
        assertEquals(2, TokenCounter.countTokens("hello, world!"))
        assertEquals(2, TokenCounter.countTokens("a.b.c.d"))
        assertEquals(1, TokenCounter.countTokens("word"))
    }

    @Test
    fun cjkCharactersCountedIndividually() {
        assertEquals(4, TokenCounter.countTokens("你好世界"))
        assertEquals(3, TokenCounter.countTokens("日本語"))
    }

    @Test
    fun emojiCountedHigher() {
        assertEquals(2, TokenCounter.countTokens("😀"))
        assertEquals(4, TokenCounter.countTokens("😀😀"))
    }

    @Test
    fun mixedContent() {
        val tokens = TokenCounter.countTokens("hello 你好 😀")
        assertEquals(5, tokens)
    }

    @Test
    fun messageTokensIncludeRoleAndOverhead() {
        val tokens = TokenCounter.countMessageTokens("user", "hello world")
        assertEquals(2 + 1 + 4, tokens)
    }

    @Test
    fun conversationTokensSumMessages() {
        val messages = listOf(
            Message("user", "hello world"),
            Message("assistant", "hi there friend")
        )
        val expected = TokenCounter.countMessageTokens("user", "hello world") +
            TokenCounter.countMessageTokens("assistant", "hi there friend")
        assertEquals(expected, TokenCounter.countConversationTokens(messages))
    }

    @Test
    fun fitsWithinLimitChecks() {
        val messages = listOf(Message("user", "hello"))
        assertTrue(TokenCounter.fitsWithinLimit(messages, 100))
        assertFalse(TokenCounter.fitsWithinLimit(messages, 1))
    }

    @Test
    fun estimateTokensUsesLengthHeuristic() {
        assertEquals(2, TokenCounter.estimateTokens("12345678"))
        assertEquals(1, TokenCounter.estimateTokens("abc"))
        assertEquals(25, TokenCounter.estimateTokens("a".repeat(100)))
    }

    @Test
    fun longTextTokenCountIsReasonable() {
        val text = "word ".repeat(100)
        val tokens = TokenCounter.countTokens(text)
        assertTrue(tokens in 100..200)
    }
}
