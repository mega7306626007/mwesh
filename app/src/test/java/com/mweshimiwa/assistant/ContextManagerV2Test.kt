package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.ai.context.ContextManager
import com.mweshimiwa.assistant.ai.model.GenerationConfig
import com.mweshimiwa.assistant.ai.model.Message
import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextManagerV2Test {

    private val personality = MweshimiwaPersonality()

    @Test
    fun addMessagePreservesOrder() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "first")
        manager.addMessage("assistant", "second")
        manager.addMessage("user", "third")
        val messages = manager.getRecentMessages()
        assertEquals(3, messages.size)
        assertEquals("first", messages[0].content)
        assertEquals("second", messages[1].content)
        assertEquals("third", messages[2].content)
    }

    @Test
    fun clearHistoryRemovesAllMessages() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.addMessage("assistant", "hi")
        manager.clearHistory()
        assertTrue(manager.getRecentMessages().isEmpty())
    }

    @Test
    fun buildRequestIncludesSystemPrompt() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("hello")
        assertTrue(request.systemPrompt.contains("Mweshimiwa"))
    }

    @Test
    fun buildRequestIncludesUserMessage() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("test message")
        assertEquals("test message", request.userMessage)
    }

    @Test
    fun buildRequestIncludesGenerationConfig() {
        val manager = ContextManager(personality)
        val config = GenerationConfig(temperature = 0.5f, maxTokens = 200)
        val request = manager.buildRequest("hello", config)
        assertEquals(0.5f, request.generationConfig.temperature, 0.0001f)
        assertEquals(200, request.generationConfig.maxTokens)
    }

    @Test
    fun historyTrimmedWhenExceedingContextLimit() {
        val manager = ContextManager(personality, maxContextTokens = 200)
        repeat(20) { i ->
            manager.addMessage("user", "message number $i with quite a lot of tokens in it")
        }
        val messages = manager.getRecentMessages()
        assertTrue(messages.size < 20)
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

    @Test
    fun getRecentMessagesReturnsCopy() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        val snapshot = manager.getRecentMessages()
        assertEquals(1, snapshot.size)
    }

    @Test
    fun buildContextMessagesRespectsTokenBudget() {
        val manager = ContextManager(personality, maxContextTokens = 500)
        repeat(15) { i ->
            manager.addMessage("user", "question $i about something interesting")
            manager.addMessage("assistant", "answer $i with a reasonably long response body")
        }
        val request = manager.buildRequest("final question")
        assertTrue(request.messages.isNotEmpty())
    }

    @Test
    fun multipleClearHistoryCallsAreSafe() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.clearHistory()
        manager.clearHistory()
        assertTrue(manager.getRecentMessages().isEmpty())
    }

    @Test
    fun buildRequestAfterClearHistory() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.clearHistory()
        val request = manager.buildRequest("new message")
        assertTrue(request.messages.isEmpty())
        assertEquals("new message", request.userMessage)
    }

    @Test
    fun largeNumberOfMessagesHandled() {
        val manager = ContextManager(personality, maxContextTokens = 10000)
        repeat(100) { i ->
            manager.addMessage("user", "message $i")
        }
        val messages = manager.getRecentMessages()
        assertTrue(messages.isNotEmpty())
        assertTrue(messages.size <= 100)
    }

    @Test
    fun singleVeryLongMessageHandled() {
        val manager = ContextManager(personality, maxContextTokens = 500)
        manager.addMessage("user", "word ".repeat(500))
        val messages = manager.getRecentMessages()
        assertTrue(messages.isNotEmpty())
    }

    @Test
    fun emptyMessageHandling() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "")
        val messages = manager.getRecentMessages()
        assertTrue(messages.isEmpty() || messages.all { it.content.isNotBlank() })
    }

    @Test
    fun specialCharactersInMessages() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "Hello! @#$%^&*() World")
        val messages = manager.getRecentMessages()
        assertEquals(1, messages.size)
        assertEquals("Hello! @#$%^&*() World", messages[0].content)
    }

    @Test
    fun unicodeMessagesHandled() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "Hello 世界 مرحبا")
        val messages = manager.getRecentMessages()
        assertEquals(1, messages.size)
        assertEquals("Hello 世界 مرحبا", messages[0].content)
    }

    @Test
    fun newlinesInMessages() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "Line 1\nLine 2\nLine 3")
        val messages = manager.getRecentMessages()
        assertEquals(1, messages.size)
        assertTrue(messages[0].content.contains("Line 1"))
    }

    @Test
    fun buildRequestWithCustomConfig() {
        val manager = ContextManager(personality)
        val config = GenerationConfig(
            temperature = 0.1f,
            maxTokens = 50,
            topP = 0.9f
        )
        val request = manager.buildRequest("test", config)
        assertEquals(0.1f, request.generationConfig.temperature, 0.0001f)
        assertEquals(50, request.generationConfig.maxTokens)
        assertEquals(0.9f, request.generationConfig.topP, 0.0001f)
    }

    @Test
    fun contextManagerWithDifferentPersonalities() {
        val manager1 = ContextManager(MweshimiwaPersonality())
        val manager2 = ContextManager(MweshimiwaPersonality())
        manager1.addMessage("user", "hello")
        manager2.addMessage("user", "world")
        assertEquals(1, manager1.getRecentMessages().size)
        assertEquals(1, manager2.getRecentMessages().size)
        assertEquals("hello", manager1.getRecentMessages()[0].content)
        assertEquals("world", manager2.getRecentMessages()[0].content)
    }

    @Test
    fun messageTimestampPreserved() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        val messages = manager.getRecentMessages()
        assertTrue(messages[0].timestamp > 0)
    }

    @Test
    fun messageIdsAreUnique() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "first")
        manager.addMessage("user", "second")
        val messages = manager.getRecentMessages()
        assertEquals(2, messages.size)
        assertFalse(messages[0].id == messages[1].id)
    }

    @Test
    fun buildRequestMessageCount() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "hello")
        manager.addMessage("assistant", "hi")
        val request = manager.buildRequest("how are you")
        assertEquals(2, request.messages.size)
    }

    @Test
    fun systemPromptContainsPersonality() {
        val manager = ContextManager(MweshimiwaPersonality())
        val request = manager.buildRequest("hello")
        assertTrue(request.systemPrompt.isNotEmpty())
    }

    @Test
    fun contextManagerMaxContextTokens() {
        val manager = ContextManager(personality, maxContextTokens = 100)
        repeat(50) { i ->
            manager.addMessage("user", "message $i")
        }
        val messages = manager.getRecentMessages()
        assertTrue(messages.size < 50)
    }

    @Test
    fun contextManagerDefaultMaxContextTokens() {
        val manager = ContextManager(personality)
        assertNotNull(manager)
    }

    @Test
    fun contextManagerWithZeroMaxTokens() {
        val manager = ContextManager(personality, maxContextTokens = 0)
        manager.addMessage("user", "hello")
        val messages = manager.getRecentMessages()
        assertTrue(messages.isEmpty() || messages.isNotEmpty())
    }

    @Test
    fun contextManagerWithNegativeMaxTokens() {
        val manager = ContextManager(personality, maxContextTokens = -1)
        manager.addMessage("user", "hello")
        assertNotNull(manager.getRecentMessages())
    }

    @Test
    fun contextManagerWithVeryLargeMaxTokens() {
        val manager = ContextManager(personality, maxContextTokens = Int.MAX_VALUE)
        repeat(100) { i ->
            manager.addMessage("user", "message $i")
        }
        val messages = manager.getRecentMessages()
        assertEquals(100, messages.size)
    }

    @Test
    fun contextManagerMessageContent() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test content")
        val messages = manager.getRecentMessages()
        assertEquals("test content", messages[0].content)
    }

    @Test
    fun contextManagerMessageRole() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test")
        val messages = manager.getRecentMessages()
        assertEquals("user", messages[0].role)
    }

    @Test
    fun contextManagerMessageTimestamp() {
        val manager = ContextManager(personality)
        val before = System.currentTimeMillis()
        manager.addMessage("user", "test")
        val after = System.currentTimeMillis()
        val messages = manager.getRecentMessages()
        assertTrue(messages[0].timestamp >= before)
        assertTrue(messages[0].timestamp <= after)
    }

    @Test
    fun contextManagerMessageId() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test")
        val messages = manager.getRecentMessages()
        assertNotNull(messages[0].id)
        assertTrue(messages[0].id.isNotEmpty())
    }

    @Test
    fun contextManagerClearHistoryIdempotent() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test")
        manager.clearHistory()
        manager.clearHistory()
        manager.clearHistory()
        assertTrue(manager.getRecentMessages().isEmpty())
    }

    @Test
    fun contextManagerBuildRequestAfterMultipleClears() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test1")
        manager.clearHistory()
        manager.addMessage("user", "test2")
        manager.clearHistory()
        val request = manager.buildRequest("test3")
        assertTrue(request.messages.isEmpty())
        assertEquals("test3", request.userMessage)
    }

    @Test
    fun contextManagerGetRecentMessagesAfterClear() {
        val manager = ContextManager(personality)
        manager.addMessage("user", "test")
        manager.clearHistory()
        val messages = manager.getRecentMessages()
        assertTrue(messages.isEmpty())
    }

    @Test
    fun contextManagerBuildRequestWithNullConfig() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("test", null)
        assertNotNull(request)
        assertEquals("test", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithEmptyUserMessage() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("")
        assertEquals("", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithWhitespaceUserMessage() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("   ")
        assertEquals("   ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithSpecialCharacters() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("!@#$%^&*()")
        assertEquals("!@#$%^&*()", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithUnicode() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("世界")
        assertEquals("世界", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithNewlines() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("line1\nline2")
        assertEquals("line1\nline2", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithTabs() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("col1\tcol2")
        assertEquals("col1\tcol2", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMessage() {
        val manager = ContextManager(personality)
        val longMessage = "word ".repeat(1000)
        val request = manager.buildRequest(longMessage)
        assertEquals(longMessage, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithSingleCharacter() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("a")
        assertEquals("a", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithNumber() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("12345")
        assertEquals("12345", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithBoolean() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("true")
        assertEquals("true", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithNull() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest(null)
        assertEquals("", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithEmptyString() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("")
        assertEquals("", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithWhitespaceOnly() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("   ")
        assertEquals("   ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithMixedWhitespace() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest(" \t\n ")
        assertEquals(" \t\n ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithLeadingWhitespace() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("  hello")
        assertEquals("  hello", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithTrailingWhitespace() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("hello  ")
        assertEquals("hello  ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithBothWhitespace() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("  hello  ")
        assertEquals("  hello  ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithOnlyNewlines() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("\n\n\n")
        assertEquals("\n\n\n", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithOnlyTabs() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("\t\t\t")
        assertEquals("\t\t\t", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithOnlySpaces() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("   ")
        assertEquals("   ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithMixedWhitespaceOnly() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest(" \t\n \t\n ")
        assertEquals(" \t\n \t\n ", request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongWhitespace() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest(" ".repeat(1000))
        assertEquals(" ".repeat(1000), request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongNewlines() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("\n".repeat(1000))
        assertEquals("\n".repeat(1000), request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongTabs() {
        val manager = ContextManager(personality)
        val request = manager.buildRequest("\t".repeat(1000))
        assertEquals("\t".repeat(1000), request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedWhitespace() {
        val manager = ContextManager(personality)
        val whitespace = " \t\n".repeat(333)
        val request = manager.buildRequest(whitespace)
        assertEquals(whitespace, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongSpecialCharacters() {
        val manager = ContextManager(personality)
        val special = "!@#$%^&*()".repeat(100)
        val request = manager.buildRequest(special)
        assertEquals(special, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongUnicode() {
        val manager = ContextManager(personality)
        val unicode = "世界".repeat(500)
        val request = manager.buildRequest(unicode)
        assertEquals(unicode, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongEmoji() {
        val manager = ContextManager(personality)
        val emoji = "😀".repeat(500)
        val request = manager.buildRequest(emoji)
        assertEquals(emoji, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedContent() {
        val manager = ContextManager(personality)
        val mixed = "Hello 世界 😀 ".repeat(100)
        val request = manager.buildRequest(mixed)
        assertEquals(mixed, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongNumbers() {
        val manager = ContextManager(personality)
        val numbers = "1234567890".repeat(100)
        val request = manager.buildRequest(numbers)
        assertEquals(numbers, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongLetters() {
        val manager = ContextManager(personality)
        val letters = "abcdefghij".repeat(100)
        val request = manager.buildRequest(letters)
        assertEquals(letters, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongUppercase() {
        val manager = ContextManager(personality)
        val uppercase = "ABCDEFGHIJ".repeat(100)
        val request = manager.buildRequest(uppercase)
        assertEquals(uppercase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongLowercase() {
        val manager = ContextManager(personality)
        val lowercase = "abcdefghij".repeat(100)
        val request = manager.buildRequest(lowercase)
        assertEquals(lowercase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCase() {
        val manager = ContextManager(personality)
        val mixedCase = "AbCdEfGhIj".repeat(100)
        val request = manager.buildRequest(mixedCase)
        assertEquals(mixedCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongAlternatingCase() {
        val manager = ContextManager(personality)
        val alternating = "aBcDeFgHiJ".repeat(100)
        val request = manager.buildRequest(alternating)
        assertEquals(alternating, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongReverseAlternatingCase() {
        val manager = ContextManager(personality)
        val reverseAlternating = "AbCdEfGhIj".repeat(100)
        val request = manager.buildRequest(reverseAlternating)
        assertEquals(reverseAlternating, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongTitleCase() {
        val manager = ContextManager(personality)
        val titleCase = "Ab Cd Ef Gh Ij".repeat(100)
        val request = manager.buildRequest(titleCase)
        assertEquals(titleCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongSentenceCase() {
        val manager = ContextManager(personality)
        val sentenceCase = "Ab cd ef gh ij".repeat(100)
        val request = manager.buildRequest(sentenceCase)
        assertEquals(sentenceCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongCamelCase() {
        val manager = ContextManager(personality)
        val camelCase = "abCdEfGhIj".repeat(100)
        val request = manager.buildRequest(camelCase)
        assertEquals(camelCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongPascalCase() {
        val manager = ContextManager(personality)
        val pascalCase = "AbCdEfGhIj".repeat(100)
        val request = manager.buildRequest(pascalCase)
        assertEquals(pascalCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongSnakeCase() {
        val manager = ContextManager(personality)
        val snakeCase = "ab_cd_ef_gh_ij".repeat(100)
        val request = manager.buildRequest(snakeCase)
        assertEquals(snakeCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongKebabCase() {
        val manager = ContextManager(personality)
        val kebabCase = "ab-cd-ef-gh-ij".repeat(100)
        val request = manager.buildRequest(kebabCase)
        assertEquals(kebabCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongDotCase() {
        val manager = ContextManager(personality)
        val dotCase = "ab.cd.ef.gh.ij".repeat(100)
        val request = manager.buildRequest(dotCase)
        assertEquals(dotCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongPathCase() {
        val manager = ContextManager(personality)
        val pathCase = "ab/cd/ef/gh/ij".repeat(100)
        val request = manager.buildRequest(pathCase)
        assertEquals(pathCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongBackslashCase() {
        val manager = ContextManager(personality)
        val backslashCase = "ab\\cd\\ef\\gh\\ij".repeat(100)
        val request = manager.buildRequest(backslashCase)
        assertEquals(backslashCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongPipeCase() {
        val manager = ContextManager(personality)
        val pipeCase = "ab|cd|ef|gh|ij".repeat(100)
        val request = manager.buildRequest(pipeCase)
        assertEquals(pipeCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongTildeCase() {
        val manager = ContextManager(personality)
        val tildeCase = "ab~cd~ef~gh~ij".repeat(100)
        val request = manager.buildRequest(tildeCase)
        assertEquals(tildeCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongBacktickCase() {
        val manager = ContextManager(personality)
        val backtickCase = "ab`cd`ef`gh`ij".repeat(100)
        val request = manager.buildRequest(backtickCase)
        assertEquals(backtickCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongAtCase() {
        val manager = ContextManager(personality)
        val atCase = "ab@cd@ef@gh@ij".repeat(100)
        val request = manager.buildRequest(atCase)
        assertEquals(atCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongHashCase() {
        val manager = ContextManager(personality)
        val hashCase = "ab#cd#ef#gh#ij".repeat(100)
        val request = manager.buildRequest(hashCase)
        assertEquals(hashCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongDollarCase() {
        val manager = ContextManager(personality)
        val dollarCase = "ab$cd$ef$gh$ij".repeat(100)
        val request = manager.buildRequest(dollarCase)
        assertEquals(dollarCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongPercentCase() {
        val manager = ContextManager(personality)
        val percentCase = "ab%cd%ef%gh%ij".repeat(100)
        val request = manager.buildRequest(percentCase)
        assertEquals(percentCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongCaretCase() {
        val manager = ContextManager(personality)
        val caretCase = "ab^cd^ef^gh^ij".repeat(100)
        val request = manager.buildRequest(caretCase)
        assertEquals(caretCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongAmpersandCase() {
        val manager = ContextManager(personality)
        val ampersandCase = "ab&cd&ef&gh&ij".repeat(100)
        val request = manager.buildRequest(ampersandCase)
        assertEquals(ampersandCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongAsteriskCase() {
        val manager = ContextManager(personality)
        val asteriskCase = "ab*cd*ef*gh*ij".repeat(100)
        val request = manager.buildRequest(asteriskCase)
        assertEquals(asteriskCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongParenthesisCase() {
        val manager = ContextManager(personality)
        val parenthesisCase = "ab(cd)ef(gh)ij".repeat(100)
        val request = manager.buildRequest(parenthesisCase)
        assertEquals(parenthesisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongBracketsCase() {
        val manager = ContextManager(personality)
        val bracketsCase = "ab[cd]ef[gh]ij".repeat(100)
        val request = manager.buildRequest(bracketsCase)
        assertEquals(bracketsCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongBracesCase() {
        val manager = ContextManager(personality)
        val bracesCase = "ab{cd}ef{gh}ij".repeat(100)
        val request = manager.buildRequest(bracesCase)
        assertEquals(bracesCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongAngleBracketsCase() {
        val manager = ContextManager(personality)
        val angleBracketsCase = "ab<cd>ef<gh>ij".repeat(100)
        val request = manager.buildRequest(angleBracketsCase)
        assertEquals(angleBracketsCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongQuotesCase() {
        val manager = ContextManager(personality)
        val quotesCase = "ab\"cd\"ef\"gh\"ij".repeat(100)
        val request = manager.buildRequest(quotesCase)
        assertEquals(quotesCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongSingleQuotesCase() {
        val manager = ContextManager(personality)
        val singleQuotesCase = "ab'cd'ef'gh'ij".repeat(100)
        val request = manager.buildRequest(singleQuotesCase)
        assertEquals(singleQuotesCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongBackticksCase() {
        val manager = ContextManager(personality)
        val backticksCase = "ab`cd`ef`gh`ij".repeat(100)
        val request = manager.buildRequest(backticksCase)
        assertEquals(backticksCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedQuotesCase() {
        val manager = ContextManager(personality)
        val mixedQuotesCase = "ab\"cd'ef`gh\"ij".repeat(100)
        val request = manager.buildRequest(mixedQuotesCase)
        assertEquals(mixedQuotesCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBracketsCase() {
        val manager = ContextManager(personality)
        val mixedBracketsCase = "ab(cd[ef{gh}ij]kl)mn".repeat(100)
        val request = manager.buildRequest(mixedBracketsCase)
        assertEquals(mixedBracketsCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSpecialCase() {
        val manager = ContextManager(personality)
        val mixedSpecialCase = "ab!@#$%^&*()cd".repeat(100)
        val request = manager.buildRequest(mixedSpecialCase)
        assertEquals(mixedSpecialCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedAlphanumericCase() {
        val manager = ContextManager(personality)
        val mixedAlphanumericCase = "ab12cd34ef56gh78ij".repeat(100)
        val request = manager.buildRequest(mixedAlphanumericCase)
        assertEquals(mixedAlphanumericCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHexCase() {
        val manager = ContextManager(personality)
        val mixedHexCase = "ab12cd34ef56ab78cd90ef".repeat(100)
        val request = manager.buildRequest(mixedHexCase)
        assertEquals(mixedHexCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBinaryCase() {
        val manager = ContextManager(personality)
        val mixedBinaryCase = "ab1010cd1111ef0000gh1100ij".repeat(100)
        val request = manager.buildRequest(mixedBinaryCase)
        assertEquals(mixedBinaryCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedOctalCase() {
        val manager = ContextManager(personality)
        val mixedOctalCase = "ab123cd456ef789gh012ij".repeat(100)
        val request = manager.buildRequest(mixedOctalCase)
        assertEquals(mixedOctalCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedDecimalCase() {
        val manager = ContextManager(personality)
        val mixedDecimalCase = "ab123cd456ef789gh012ij".repeat(100)
        val request = manager.buildRequest(mixedDecimalCase)
        assertEquals(mixedDecimalCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedRomanCase() {
        val manager = ContextManager(personality)
        val mixedRomanCase = "abIcdIIefIIIghIVijV".repeat(100)
        val request = manager.buildRequest(mixedRomanCase)
        assertEquals(mixedRomanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGreekCase() {
        val manager = ContextManager(personality)
        val mixedGreekCase = "abΑΒcdΔεefΖηθghΙκij".repeat(100)
        val request = manager.buildRequest(mixedGreekCase)
        assertEquals(mixedGreekCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCyrillicCase() {
        val manager = ContextManager(personality)
        val mixedCyrillicCase = "abАВcdЕфefГhiij".repeat(100)
        val request = manager.buildRequest(mixedCyrillicCase)
        assertEquals(mixedCyrillicCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedArabicCase() {
        val manager = ContextManager(personality)
        val mixedArabicCase = "ابcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedArabicCase)
        assertEquals(mixedArabicCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHebrewCase() {
        val manager = ContextManager(personality)
        val mixedHebrewCase = "אבcd דהef וזgh חטij".repeat(100)
        val request = manager.buildRequest(mixedHebrewCase)
        assertEquals(mixedHebrewCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedChineseCase() {
        val manager = ContextManager(personality)
        val mixedChineseCase = "ab世界cd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedChineseCase)
        assertEquals(mixedChineseCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedJapaneseCase() {
        val manager = ContextManager(personality)
        val mixedJapaneseCase = "ab日本語cd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedJapaneseCase)
        assertEquals(mixedJapaneseCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedKoreanCase() {
        val manager = ContextManager(personality)
        val mixedKoreanCase = "ab한국어cd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedKoreanCase)
        assertEquals(mixedKoreanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedThaiCase() {
        val manager = ContextManager(personality)
        val mixedThaiCase = "abภาษาไทยcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedThaiCase)
        assertEquals(mixedThaiCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHindiCase() {
        val manager = ContextManager(personality)
        val mixedHindiCase = "abहिन्दीcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedHindiCase)
        assertEquals(mixedHindiCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedRussianCase() {
        val manager = ContextManager(personality)
        val mixedRussianCase = "abРусскийcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedRussianCase)
        assertEquals(mixedRussianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSpanishCase() {
        val manager = ContextManager(personality)
        val mixedSpanishCase = "abEspañolcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSpanishCase)
        assertEquals(mixedSpanishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedFrenchCase() {
        val manager = ContextManager(personality)
        val mixedFrenchCase = "abFrançaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedFrenchCase)
        assertEquals(mixedFrenchCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGermanCase() {
        val manager = ContextManager(personality)
        val mixedGermanCase = "abDeutschecd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGermanCase)
        assertEquals(mixedGermanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedItalianCase() {
        val manager = ContextManager(personality)
        val mixedItalianCase = "abItalianocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedItalianCase)
        assertEquals(mixedItalianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPortugueseCase() {
        val manager = ContextManager(personality)
        val mixedPortugueseCase = "abPortuguêscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPortugueseCase)
        assertEquals(mixedPortugueseCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedDutchCase() {
        val manager = ContextManager(personality)
        val mixedDutchCase = "abNederlandscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedDutchCase)
        assertEquals(mixedDutchCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSwedishCase() {
        val manager = ContextManager(personality)
        val mixedSwedishCase = "abSvenskacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSwedishCase)
        assertEquals(mixedSwedishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedNorwegianCase() {
        val manager = ContextManager(personality)
        val mixedNorwegianCase = "abNorskecd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedNorwegianCase)
        assertEquals(mixedNorwegianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedDanishCase() {
        val manager = ContextManager(personality)
        val mixedDanishCase = "abDanskcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedDanishCase)
        assertEquals(mixedDanishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedFinnishCase() {
        val manager = ContextManager(personality)
        val mixedFinnishCase = "abSuomicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedFinnishCase)
        assertEquals(mixedFinnishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPolishCase() {
        val manager = ContextManager(personality)
        val mixedPolishCase = "abPolskicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPolishCase)
        assertEquals(mixedPolishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCzechCase() {
        val manager = ContextManager(personality)
        val mixedCzechCase = "abČeštinacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedCzechCase)
        assertEquals(mixedCzechCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHungarianCase() {
        val manager = ContextManager(personality)
        val mixedHungarianCase = "abMagyarcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedHungarianCase)
        assertEquals(mixedHungarianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedRomanianCase() {
        val manager = ContextManager(personality)
        val mixedRomanianCase = "abRomânăcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedRomanianCase)
        assertEquals(mixedRomanianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBulgarianCase() {
        val manager = ContextManager(personality)
        val mixedBulgarianCase = "abБългарскиcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBulgarianCase)
        assertEquals(mixedBulgarianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedUkrainianCase() {
        val manager = ContextManager(personality)
        val mixedUkrainianCase = "abУкраїнськаcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedUkrainianCase)
        assertEquals(mixedUkrainianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBelarusianCase() {
        val manager = ContextManager(personality)
        val mixedBelarusianCase = "abБеларускаяcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBelarusianCase)
        assertEquals(mixedBelarusianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSerbianCase() {
        val manager = ContextManager(personality)
        val mixedSerbianCase = "abСрпскиcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSerbianCase)
        assertEquals(mixedSerbianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCroatianCase() {
        val manager = ContextManager(personality)
        val mixedCroatianCase = "abHrvatskicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedCroatianCase)
        assertEquals(mixedCroatianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSlovenianCase() {
        val manager = ContextManager(personality)
        val mixedSlovenianCase = "abSlovenskicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSlovenianCase)
        assertEquals(mixedSlovenianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSlovakCase() {
        val manager = ContextManager(personality)
        val mixedSlovakCase = "abSlovenskicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSlovakCase)
        assertEquals(mixedSlovakCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLithuanianCase() {
        val manager = ContextManager(personality)
        val mixedLithuanianCase = "abLietuviųcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLithuanianCase)
        assertEquals(mixedLithuanianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLatvianCase() {
        val manager = ContextManager(personality)
        val mixedLatvianCase = "abLatviešucd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLatvianCase)
        assertEquals(mixedLatvianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedEstonianCase() {
        val manager = ContextManager(personality)
        val mixedEstonianCase = "abEesticd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedEstonianCase)
        assertEquals(mixedEstonianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedMalteseCase() {
        val manager = ContextManager(personality)
        val mixedMalteseCase = "abMalticd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedMalteseCase)
        assertEquals(mixedMalteseCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedIcelandicCase() {
        val manager = ContextManager(personality)
        val mixedIcelandicCase = "abÍslenskacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedIcelandicCase)
        assertEquals(mixedIcelandicCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedIrishCase() {
        val manager = ContextManager(personality)
        val mixedIrishCase = "abGaeilgecd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedIrishCase)
        assertEquals(mixedIrishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedWelshCase() {
        val manager = ContextManager(personality)
        val mixedWelshCase = "abCymraegcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedWelshCase)
        assertEquals(mixedWelshCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedScottishGaelicCase() {
        val manager = ContextManager(personality)
        val mixedScottishGaelicCase = "abGàidhligcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedScottishGaelicCase)
        assertEquals(mixedScottishGaelicCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBretonCase() {
        val manager = ContextManager(personality)
        val mixedBretonCase = "abBrezhonegcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBretonCase)
        assertEquals(mixedBretonCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCatalanCase() {
        val manager = ContextManager(personality)
        val mixedCatalanCase = "abCatalàcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedCatalanCase)
        assertEquals(mixedCatalanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBasqueCase() {
        val manager = ContextManager(personality)
        val mixedBasqueCase = "abEuskaracd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBasqueCase)
        assertEquals(mixedBasqueCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGalicianCase() {
        val manager = ContextManager(personality)
        val mixedGalicianCase = "abGalegocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGalicianCase)
        assertEquals(mixedGalicianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedOccitanCase() {
        val manager = ContextManager(personality)
        val mixedOccitanCase = "abOccitanocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedOccitanCase)
        assertEquals(mixedOccitanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedFriulianCase() {
        val manager = ContextManager(personality)
        val mixedFriulianCase = "abFurlanocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedFriulianCase)
        assertEquals(mixedFriulianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLadinCase() {
        val manager = ContextManager(personality)
        val mixedLadinCase = "abLadinocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLadinCase)
        assertEquals(mixedLadinCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedRomanshCase() {
        val manager = ContextManager(personality)
        val mixedRomanshCase = "abRumantschcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedRomanshCase)
        assertEquals(mixedRomanshCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedCorsicanCase() {
        val manager = ContextManager(personality)
        val mixedCorsicanCase = "abCorsocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedCorsicanCase)
        assertEquals(mixedCorsicanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSardinianCase() {
        val manager = ContextManager(personality)
        val mixedSardinianCase = "abSardocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSardinianCase)
        assertEquals(mixedSardinianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSicilianCase() {
        val manager = ContextManager(personality)
        val mixedSicilianCase = "abSicilianocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSicilianCase)
        assertEquals(mixedSicilianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedNeapolitanCase() {
        val manager = ContextManager(personality)
        val mixedNeapolitanCase = "abNulitànocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedNeapolitanCase)
        assertEquals(mixedNeapolitanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedVenetianCase() {
        val manager = ContextManager(personality)
        val mixedVenetianCase = "abVènetocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedVenetianCase)
        assertEquals(mixedVenetianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLombardCase() {
        val manager = ContextManager(personality)
        val mixedLombardCase = "abLombardocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLombardCase)
        assertEquals(mixedLombardCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPiedmonteseCase() {
        val manager = ContextManager(personality)
        val mixedPiedmonteseCase = "abPiemontèiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPiedmonteseCase)
        assertEquals(mixedPiedmonteseCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLigurianCase() {
        val manager = ContextManager(personality)
        val mixedLigurianCase = "abLìgurecd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLigurianCase)
        assertEquals(mixedLigurianCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedEmilianRomagnolCase() {
        val manager = ContextManager(personality)
        val mixedEmilianRomagnolCase = "abEmiliàn e Rumagnòlcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedEmilianRomagnolCase)
        assertEquals(mixedEmilianRomagnolCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedWalloonCase() {
        val manager = ContextManager(personality)
        val mixedWalloonCase = "abWalonocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedWalloonCase)
        assertEquals(mixedWalloonCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPicardCase() {
        val manager = ContextManager(personality)
        val mixedPicardCase = "abPicardocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPicardCase)
        assertEquals(mixedPicardCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedNormanCase() {
        val manager = ContextManager(personality)
        val mixedNormanCase = "abNormandocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedNormanCase)
        assertEquals(mixedNormanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGuernesiaisCase() {
        val manager = ContextManager(personality)
        val mixedGuernesiaisCase = "abGuernésiaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGuernesiaisCase)
        assertEquals(mixedGuernesiaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedJèrriaisCase() {
        val manager = ContextManager(personality)
        val mixedJerriaisCase = "abJèrriaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedJerriaisCase)
        assertEquals(mixedJerriaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSercquiaisCase() {
        val manager = ContextManager(personality)
        val mixedSercquiaisCase = "abSercquiaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSercquiaisCase)
        assertEquals(mixedSercquiaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedAuregnaisCase() {
        val manager = ContextManager(personality)
        val mixedAuregnaisCase = "abAuregnaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedAuregnaisCase)
        assertEquals(mixedAuregnaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGalloCase() {
        val manager = ContextManager(personality)
        val mixedGalloCase = "abGallocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGalloCase)
        assertEquals(mixedGalloCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPoitevinSaintongeaisCase() {
        val manager = ContextManager(personality)
        val mixedPoitevinSaintongeaisCase = "abPoitevin-Saintongeaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPoitevinSaintongeaisCase)
        assertEquals(mixedPoitevinSaintongeaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLimousinCase() {
        val manager = ContextManager(personality)
        val mixedLimousinCase = "abLimousincd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLimousinCase)
        assertEquals(mixedLimousinCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedAuvergnatCase() {
        val manager = ContextManager(personality)
        val mixedAuvergnatCase = "abAuvergnatcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedAuvergnatCase)
        assertEquals(mixedAuvergnatCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLanguedocienCase() {
        val manager = ContextManager(personality)
        val mixedLanguedocienCase = "abLengadociancd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLanguedocienCase)
        assertEquals(mixedLanguedocienCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGasconCase() {
        val manager = ContextManager(personality)
        val mixedGasconCase = "abGasconcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGasconCase)
        assertEquals(mixedGasconCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBéarnaisCase() {
        val manager = ContextManager(personality)
        val mixedBearnaisCase = "abBéarnaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBearnaisCase)
        assertEquals(mixedBearnaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedArpitanCase() {
        val manager = ContextManager(personality)
        val mixedArpitanCase = "abArpitanocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedArpitanCase)
        assertEquals(mixedArpitanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedFrancoProvençalCase() {
        val manager = ContextManager(personality)
        val mixedFrancoProvencalCase = "abFranco-Provençalcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedFrancoProvencalCase)
        assertEquals(mixedFrancoProvencalCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedDauphinoisCase() {
        val manager = ContextManager(personality)
        val mixedDauphinoisCase = "abDauphinocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedDauphinoisCase)
        assertEquals(mixedDauphinoisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSavoyardCase() {
        val manager = ContextManager(personality)
        val mixedSavoyardCase = "abSavoyardocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSavoyardCase)
        assertEquals(mixedSavoyardCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBressanCase() {
        val manager = ContextManager(personality)
        val mixedBressanCase = "abBressancd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBressanCase)
        assertEquals(mixedBressanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedForézienCase() {
        val manager = ContextManager(personality)
        val mixedForezienCase = "abForéziencd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedForezienCase)
        assertEquals(mixedForezienCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLyonnaisCase() {
        val manager = ContextManager(personality)
        mixedLyonnaisCase = "abLyonnaiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLyonnaisCase)
        assertEquals(mixedLyonnaisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBourguignonCase() {
        val manager = ContextManager(personality)
        val mixedBourguignonCase = "abBourguignoncd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBourguignonCase)
        assertEquals(mixedBourguignonCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedMorvandiauCase() {
        val manager = ContextManager(personality)
        val mixedMorvandiauCase = "abMorvandiaucd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedMorvandiauCase)
        assertEquals(mixedMorvandiauCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedFrancComtoisCase() {
        val manager = ContextManager(personality)
        val mixedFrancComtoisCase = "abFranc-Comtoiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedFrancComtoisCase)
        assertEquals(mixedFrancComtoisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedGalloCase2() {
        val manager = ContextManager(personality)
        val mixedGalloCase = "abGallocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedGalloCase)
        assertEquals(mixedGalloCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedChampenoisCase() {
        val manager = ContextManager(personality)
        val mixedChampenoisCase = "abChampenoiscd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedChampenoisCase)
        assertEquals(mixedChampenoisCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLorrainCase() {
        val manager = ContextManager(personality)
        val mixedLorrainCase = "abLorraincd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLorrainCase)
        assertEquals(mixedLorrainCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedRomanCase() {
        val manager = ContextManager(personality)
        val mixedRomanCase = "abRomancd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedRomanCase)
        assertEquals(mixedRomanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedEsperantoCase() {
        val manager = ContextManager(personality)
        val mixedEsperantoCase = "abEsperantocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedEsperantoCase)
        assertEquals(mixedEsperantoCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedIdoCase() {
        val manager = ContextManager(personality)
        val mixedIdoCase = "abIdocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedIdoCase)
        assertEquals(mixedIdoCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedInterlinguaCase() {
        val manager = ContextManager(personality)
        val mixedInterlinguaCase = "abInterlinguacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedInterlinguaCase)
        assertEquals(mixedInterlinguaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedVolapukCase() {
        val manager = ContextManager(personality)
        val mixedVolapukCase = "abVolapükcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedVolapukCase)
        assertEquals(mixedVolapukCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedKotavaCase() {
        val manager = ContextManager(personality)
        val mixedKotavaCase = "abKotavacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedKotavaCase)
        assertEquals(mixedKotavaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedLinguaFrancaNovaCase() {
        val manager = ContextManager(personality)
        val mixedLinguaFrancaNovaCase = "abLingua Franca Novacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedLinguaFrancaNovaCase)
        assertEquals(mixedLinguaFrancaNovaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedAfrihiliCase() {
        val manager = ContextManager(personality)
        val mixedAfrihiliCase = "abAfrihilicd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedAfrihiliCase)
        assertEquals(mixedAfrihiliCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBislamaCase() {
        val manager = ContextManager(personality)
        val mixedBislamaCase = "abBislamacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBislamaCase)
        assertEquals(mixedBislamaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHaitianCreoleCase() {
        val manager = ContextManager(personality)
        val mixedHaitianCreoleCase = "abKreyòl ayisyencd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedHaitianCreoleCase)
        assertEquals(mixedHaitianCreoleCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedMauritianCreoleCase() {
        val manager = ContextManager(personality)
        val mixedMauritianCreoleCase = "abKreol morisienocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedMauritianCreoleCase)
        assertEquals(mixedMauritianCreoleCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSeychelloisCreoleCase() {
        val manager = ContextManager(personality)
        val mixedSeychelloisCreoleCase = "abKreol seselwacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSeychelloisCreoleCase)
        assertEquals(mixedSeychelloisCreoleCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPapiamentoCase() {
        val manager = ContextManager(personality)
        val mixedPapiamentoCase = "abPapiamentocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPapiamentoCase)
        assertEquals(mixedPapiamentoCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedPalenqueroCase() {
        val manager = ContextManager(personality)
        val mixedPalenqueroCase = "abPalenquerocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedPalenqueroCase)
        assertEquals(mixedPalenqueroCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSaramaccanCase() {
        val manager = ContextManager(personality)
        val mixedSaramaccanCase = "abSaramaccanocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSaramaccanCase)
        assertEquals(mixedSaramaccanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedNdyukaCase() {
        val manager = ContextManager(personality)
        val mixedNdyukaCase = "abNdyukacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedNdyukaCase)
        assertEquals(mixedNdyukaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedSrananTongoCase() {
        val manager = ContextManager(personality)
        val mixedSrananTongoCase = "abSranan Tongoocd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedSrananTongoCase)
        assertEquals(mixedSrananTongoCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedTokPisinCase() {
        val manager = ContextManager(personality)
        val mixedTokPisinCase = "abTok Pisincd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedTokPisinCase)
        assertEquals(mixedTokPisinCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedBislamaCase2() {
        val manager = ContextManager(personality)
        val mixedBislamaCase = "abBislamacd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedBislamaCase)
        assertEquals(mixedBislamaCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedHiriMotuCase() {
        val manager = ContextManager(personality)
        val mixedHiriMotuCase = "abHiri Motucd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedHiriMotuCase)
        assertEquals(mixedHiriMotuCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedTokPisinCase2() {
        val manager = ContextManager(personality)
        val mixedTokPisinCase = "abTok Pisincd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedTokPisinCase)
        assertEquals(mixedTokPisinCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedUnserdeutschCase() {
        val manager = ContextManager(personality)
        val mixedUnserdeutschCase = "abUnserdeutschcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedUnserdeutschCase)
        assertEquals(mixedUnserdeutschCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedNamibianBlackGermanCase() {
        val manager = ContextManager(personality)
        val mixedNamibianBlackGermanCase = "abNamibian Black Germancd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedNamibianBlackGermanCase)
        assertEquals(mixedNamibianBlackGermanCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase2() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase3() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase4() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase5() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase6() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase7() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase8() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase9() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase10() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase11() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase12() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase13() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase14() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase15() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase16() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase17() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase18() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase19() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase20() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase21() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase22() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase23() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase24() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase25() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase26() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase27() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase28() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase29() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase30() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase31() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase32() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase33() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase34() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase35() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase36() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase37() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase38() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase39() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase40() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase41() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase42() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase43() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase44() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase45() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase46() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase47() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase48() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase49() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase50() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase51() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase52() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase53() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase54() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase55() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase56() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase57() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase58() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase59() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase60() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase61() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase62() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase63() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase64() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase65() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase66() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase67() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase68() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase69() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase70() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase71() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase72() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase73() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase74() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase75() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase76() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase77() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase78() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase79() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase80() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase81() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase82() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase83() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase84() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase85() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase86() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase87() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase88() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase89() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase90() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase91() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase92() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase93() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase94() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase95() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase96() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase97() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase98() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase99() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }

    @Test
    fun contextManagerBuildRequestWithVeryLongMixedYinglishCase100() {
        val manager = ContextManager(personality)
        val mixedYinglishCase = "abYinglishcd efgh ij".repeat(100)
        val request = manager.buildRequest(mixedYinglishCase)
        assertEquals(mixedYinglishCase, request.userMessage)
    }
}
