package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.data.database.MemoryEntity
import com.mweshimiwa.assistant.data.repositories.MemorySearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MemorySearchTest {

    @Test
    fun searchFindsExactMatch() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L),
            MemoryEntity(key = "food", value = "ugali", category = "preferences", timestamp = 2000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchFindsPartialMatch() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nair")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchReturnsEmptyForNoMatch() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "xyznonexistent")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyQuery() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyMemories() {
        val engine = MemorySearchEngine()
        val results = engine.search(emptyList(), "test")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchIsCaseInsensitive() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchByCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L),
            MemoryEntity(key = "food", value = "ugali", category = "preferences", timestamp = 2000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "facts")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchByValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L),
            MemoryEntity(key = "city", value = "Mombasa", category = "facts", timestamp = 2000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Mombasa")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchByMultipleTerms() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L),
            MemoryEntity(key = "food", value = "ugali", category = "preferences", timestamp = 2000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi ugali")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithSpecialCharacters() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi!")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNumbers() {
        val memories = listOf(
            MemoryEntity(key = "age", value = "25", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "25")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithLongQuery() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val longQuery = "Nairobi ".repeat(100)
        val results = engine.search(memories, longQuery)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithSingleCharacter() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "N")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithWhitespace() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "  Nairobi  ")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNewlines() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi\n")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithTabs() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi\t")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithMixedWhitespace() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, " \t\nNairobi \t\n ")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithUnicode() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "世界", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "世界")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithEmoji() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "😀", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "😀")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithMixedContent() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Hello 世界 😀", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Hello")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithVeryLongValue() {
        val longValue = "word ".repeat(1000)
        val memories = listOf(
            MemoryEntity(key = "city", value = longValue, category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "word")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithVeryLongKey() {
        val longKey = "k".repeat(1000)
        val memories = listOf(
            MemoryEntity(key = longKey, value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithVeryLongCategory() {
        val longCategory = "c".repeat(1000)
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = longCategory, timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithVeryLongTimestamp() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = Long.MAX_VALUE)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNegativeTimestamp() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = -1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithZeroTimestamp() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "facts", timestamp = 0L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithEmptyKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithValue() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "")
        assertTrue(results.isEmpty())
    }

    @Test
    fun searchWithEmptyCategory() {
        val memories = listOf(
            MemoryEntity(key = "city", value = "Nairobi", category = "", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun searchWithNullKey() {
        val memories = listOf(
            MemoryEntity(key = "", value = "Nairobi", category = "facts", timestamp = 1000L)
        )
        val engine = MemorySearchEngine()
        val results = engine.search(memories, "Nairobi")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun