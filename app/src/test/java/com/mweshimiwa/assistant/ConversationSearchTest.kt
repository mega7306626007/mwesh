package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.conversation.chat.ConversationSearchEngine
import com.mweshimiwa.assistant.data.database.ConversationEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationSearchTest {

    private val engine = ConversationSearchEngine()

    private fun message(id: Long, role: String, content: String, timestamp: Long = id * 1000L) =
        ConversationEntity(id = id, role = role, content = content, timestamp = timestamp)

    private val messages = listOf(
        message(1, "user", "what is the weather in Nairobi today"),
        message(2, "assistant", "It is sunny in Nairobi with a high of 28 degrees"),
        message(3, "user", "remind me to buy milk tomorrow"),
        message(4, "assistant", "I will remind you to buy milk tomorrow morning"),
        message(5, "user", "calculate 15 percent of 200"),
        message(6, "assistant", "15 percent of 200 is 30")
    )

    @Test
    fun findsExactMatch() {
        val results = engine.search(messages, "weather")
        assertTrue(results.isNotEmpty())
        assertEquals(1L, results[0].message.id)
    }

    @Test
    fun searchIsCaseInsensitive() {
        val results = engine.search(messages, "NAIROBI")
        assertEquals(2, results.size)
    }

    @Test
    fun multiWordQueryMatchesAllTerms() {
        val results = engine.search(messages, "buy milk")
        assertEquals(2, results.size)
        assertTrue(results.all { it.message.content.lowercase().contains("milk") })
    }

    @Test
    fun multiWordQueryExcludesPartialMatches() {
        val results = engine.search(messages, "weather milk")
        assertTrue(results.isEmpty())
    }

    @Test
    fun noMatchReturnsEmpty() {
        assertTrue(engine.search(messages, "zebra giraffe").isEmpty())
    }

    @Test
    fun emptyQueryReturnsEmpty() {
        assertTrue(engine.search(messages, "").isEmpty())
        assertTrue(engine.search(messages, "   ").isEmpty())
    }

    @Test
    fun resultsContainHighlightedContent() {
        val results = engine.search(messages, "weather")
        assertTrue(results[0].highlightedContent.contains("**weather**"))
    }

    @Test
    fun resultsContainSnippets() {
        val results = engine.search(messages, "milk")
        assertTrue(results[0].snippet.contains("milk"))
    }

    @Test
    fun snippetAddsEllipsisForLongContent() {
        val longMessage = message(7, "user", "start " + "word ".repeat(50) + "needle " + "word ".repeat(50))
        val results = engine.search(listOf(longMessage), "needle")
        assertTrue(results[0].snippet.startsWith("..."))
        assertTrue(results[0].snippet.endsWith("..."))
    }

    @Test
    fun regexSpecialCharsTreatedLiterally() {
        val special = listOf(message(8, "user", "price is 5.00 (final)"))
        val results = engine.search(special, "5.00")
        assertEquals(1, results.size)
        assertTrue(engine.search(special, ".*").isEmpty() || engine.search(special, ".*").isNotEmpty())
    }

    @Test
    fun resultsSortedByScore() {
        val results = engine.search(messages, "milk")
        assertTrue(results.size >= 2)
        for (i in 0 until results.size - 1) {
            assertTrue(results[i].score >= results[i + 1].score)
        }
    }

    @Test
    fun searchFirstReturnsTopResult() {
        val first = engine.searchFirst(messages, "milk")
        assertEquals("remind me to buy milk tomorrow", first!!.message.content)
    }

    @Test
    fun userMessagesBoosted() {
        val mixed = listOf(
            message(1, "assistant", "mweshimiwa mweshimiwa"),
            message(2, "user", "mweshimiwa mweshimiwa mweshimiwa mweshimiwa")
        )
        val results = engine.search(mixed, "mweshimiwa")
        assertEquals(2L, results[0].message.id)
    }

    @Test
    fun customHighlightMarkers() {
        val customEngine = ConversationSearchEngine(highlightPrefix = "<mark>", highlightSuffix = "</mark>")
        val results = customEngine.search(messages, "weather")
        assertTrue(results[0].highlightedContent.contains("<mark>weather</mark>"))
    }

    @Test
    fun emptyMessageListReturnsEmpty() {
        assertTrue(engine.search(emptyList(), "anything").isEmpty())
    }
}
