package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.data.database.MemoryEntity
import com.mweshimiwa.assistant.data.repositories.MemorySearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryRetrievalTest {

    private val engine = MemorySearchEngine()

    private fun memory(key: String, value: String, category: String = "general", timestamp: Long = 0L) =
        MemoryEntity(key = key, value = value, category = category, timestamp = timestamp)

    @Test
    fun searchFindsByKeyTerm() {
        val memories = listOf(
            memory("user_name", "Emmanuel"),
            memory("user_city", "Nairobi")
        )
        val results = engine.search(memories, "user")
        assertEquals(2, results.size)
    }

    @Test
    fun searchFindsByValueTerm() {
        val memories = listOf(
            memory("favorite_food", "I love ugali and nyama choma"),
            memory("hobby", "playing football")
        )
        val results = engine.search(memories, "ugali")
        assertEquals(1, results.size)
        assertEquals("favorite_food", results[0].memory.key)
    }

    @Test
    fun multiTermQueryRequiresAllTerms() {
        val memories = listOf(
            memory("a", "alpha beta gamma"),
            memory("b", "alpha only")
        )
        val results = engine.search(memories, "alpha beta")
        assertEquals(1, results.size)
        assertEquals("a", results[0].memory.key)
    }

    @Test
    fun exactKeyMatchRanksHigher() {
        val memories = listOf(
            memory("city", "Nairobi is a city"),
            memory("user_city", "Nairobi")
        )
        val results = engine.search(memories, "city")
        assertEquals("city", results[0].memory.key)
        assertTrue(results[0].relevance > results[1].relevance)
    }

    @Test
    fun emptyQueryReturnsEmpty() {
        val memories = listOf(memory("a", "alpha"))
        assertTrue(engine.search(memories, "").isEmpty())
        assertTrue(engine.search(memories, "   ").isEmpty())
    }

    @Test
    fun noMatchReturnsEmpty() {
        val memories = listOf(memory("a", "alpha"), memory("b", "beta"))
        assertTrue(engine.search(memories, "zebra").isEmpty())
    }

    @Test
    fun searchRespectsLimit() {
        val memories = (1..20).map { memory("key$it", "common value $it") }
        val results = engine.search(memories, "common", limit = 5)
        assertEquals(5, results.size)
    }

    @Test
    fun resultsSortedByRelevance() {
        val memories = listOf(
            memory("weak", "mweshimiwa mentioned once"),
            memory("mweshimiwa_core", "mweshimiwa mweshimiwa mweshimiwa")
        )
        val results = engine.search(memories, "mweshimiwa")
        assertEquals("mweshimiwa_core", results[0].memory.key)
    }

    @Test
    fun consolidateKeepsLatestByKey() {
        val memories = listOf(
            memory("name", "old name", timestamp = 1000L),
            memory("name", "new name", timestamp = 2000L)
        )
        val consolidated = engine.consolidate(memories)
        assertEquals(1, consolidated.size)
        assertEquals("new name", consolidated[0].value)
    }

    @Test
    fun consolidateMergesDuplicateValues() {
        val memories = listOf(
            memory("a", "the quick brown fox", timestamp = 1000L),
            memory("b", "the quick brown fox", timestamp = 2000L)
        )
        val consolidated = engine.consolidate(memories)
        assertEquals(1, consolidated.size)
        assertEquals(2000L, consolidated[0].timestamp)
    }

    @Test
    fun consolidateKeepsDistinctMemories() {
        val memories = listOf(
            memory("a", "alpha content", timestamp = 1000L),
            memory("b", "beta content", timestamp = 2000L),
            memory("c", "gamma content", timestamp = 3000L)
        )
        val consolidated = engine.consolidate(memories)
        assertEquals(3, consolidated.size)
    }

    @Test
    fun consolidatePreservesInsertionOrder() {
        val memories = listOf(
            memory("z_key", "first"),
            memory("a_key", "second"),
            memory("m_key", "third")
        )
        val consolidated = engine.consolidate(memories)
        assertEquals("z_key", consolidated[0].key)
        assertEquals("a_key", consolidated[1].key)
        assertEquals("m_key", consolidated[2].key)
    }

    @Test
    fun consolidateEmptyList() {
        assertTrue(engine.consolidate(emptyList()).isEmpty())
    }

    @Test
    fun searchIsCaseInsensitive() {
        val memories = listOf(memory("a", "UPPERCASE Content"))
        assertEquals(1, engine.search(memories, "uppercase").size)
        assertEquals(1, engine.search(memories, "UPPERCASE").size)
    }
}
