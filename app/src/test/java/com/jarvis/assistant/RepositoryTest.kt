package com.jarvis.assistant

import com.jarvis.assistant.data.database.CommandUsageDao
import com.jarvis.assistant.data.database.CommandUsageEntity
import com.jarvis.assistant.data.database.ConversationDao
import com.jarvis.assistant.data.database.ConversationEntity
import com.jarvis.assistant.data.database.MemoryDao
import com.jarvis.assistant.data.database.MemoryEntity
import com.jarvis.assistant.data.repositories.CommandUsageRepository
import com.jarvis.assistant.data.repositories.ConversationRepository
import com.jarvis.assistant.data.repositories.MemoryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryTest {

    private class FakeMemoryDao : MemoryDao {
        private val memories = mutableListOf<MemoryEntity>()
        private var nextId = 1L

        override suspend fun insert(memory: MemoryEntity) {
            val existingIndex = memories.indexOfFirst { it.key == memory.key }
            if (existingIndex >= 0) {
                memories[existingIndex] = memory.copy(id = memories[existingIndex].id)
            } else {
                memories.add(memory.copy(id = nextId++))
            }
        }

        override suspend fun getByKey(key: String): MemoryEntity? =
            memories.firstOrNull { it.key == key }

        override suspend fun getByCategory(category: String): List<MemoryEntity> =
            memories.filter { it.category == category }

        override suspend fun getAll(): List<MemoryEntity> =
            memories.sortedByDescending { it.timestamp }

        override suspend fun deleteByKey(key: String) {
            memories.removeAll { it.key == key }
        }
    }

    private class FakeConversationDao : ConversationDao {
        private val messages = mutableListOf<ConversationEntity>()
        private var nextId = 1L

        override suspend fun insert(conversation: ConversationEntity) {
            messages.add(conversation.copy(id = nextId++))
        }

        override suspend fun getRecent(limit: Int): List<ConversationEntity> =
            messages.sortedByDescending { it.timestamp }.take(limit)

        override suspend fun clearAll() {
            messages.clear()
        }
    }

    private class FakeCommandUsageDao : CommandUsageDao {
        private val usages = mutableListOf<CommandUsageEntity>()
        private var nextId = 1L

        override suspend fun insert(usage: CommandUsageEntity): Long {
            val id = nextId++
            usages.add(usage.copy(id = id))
            return id
        }

        override suspend fun getByCommand(command: String): CommandUsageEntity? =
            usages.firstOrNull { it.command == command }

        override suspend fun getAll(): List<CommandUsageEntity> =
            usages.sortedByDescending { it.usageCount }

        override suspend fun getMostUsed(limit: Int): List<CommandUsageEntity> =
            usages.sortedByDescending { it.usageCount }.take(limit)

        override suspend fun incrementUsage(command: String, timestamp: Long) {
            val index = usages.indexOfFirst { it.command == command }
            if (index >= 0) {
                val existing = usages[index]
                usages[index] = existing.copy(
                    usageCount = existing.usageCount + 1,
                    lastUsed = timestamp
                )
            }
        }

        override suspend fun deleteByCommand(command: String) {
            usages.removeAll { it.command == command }
        }

        override suspend fun deleteAll() {
            usages.clear()
        }
    }

    @Test
    fun memoryStoreAndRecall() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        repository.store("name", "Emmanuel")
        assertEquals("Emmanuel", repository.recall("name"))
    }

    @Test
    fun memoryRecallUnknownKeyReturnsNull() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        assertNull(repository.recall("missing"))
    }

    @Test
    fun memoryStoreSameKeyReplaces() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        repository.store("name", "old")
        repository.store("name", "new")
        assertEquals("new", repository.recall("name"))
        assertEquals(1, repository.getAll().size)
    }

    @Test
    fun memoryGetByCategoryFilters() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        repository.store("a", "1", category = "facts")
        repository.store("b", "2", category = "preferences")
        repository.store("c", "3", category = "facts")
        assertEquals(2, repository.getByCategory("facts").size)
        assertEquals(1, repository.getByCategory("preferences").size)
    }

    @Test
    fun memoryGetAllReturnsAllEntries() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        repository.store("a", "1")
        repository.store("b", "2")
        repository.store("c", "3")
        assertEquals(3, repository.getAll().size)
    }

    @Test
    fun memoryGetAllSortedByTimestampDescending() = runBlocking {
        val dao = FakeMemoryDao()
        dao.insert(MemoryEntity(key = "old", value = "1", category = "g", timestamp = 1000L))
        dao.insert(MemoryEntity(key = "new", value = "2", category = "g", timestamp = 3000L))
        dao.insert(MemoryEntity(key = "mid", value = "3", category = "g", timestamp = 2000L))
        val all = dao.getAll()
        assertEquals("new", all[0].key)
        assertEquals("mid", all[1].key)
        assertEquals("old", all[2].key)
    }

    @Test
    fun memoryForgetRemovesEntry() = runBlocking {
        val repository = MemoryRepository(FakeMemoryDao())
        repository.store("temp", "value")
        assertNotNull(repository.recall("temp"))
        repository.forget("temp")
        assertNull(repository.recall("temp"))
    }

    @Test
    fun conversationSaveAndRetrieve() = runBlocking {
        val repository = ConversationRepository(FakeConversationDao())
        repository.saveMessage("user", "hello")
        val recent = repository.getRecentMessages()
        assertEquals(1, recent.size)
        assertEquals("hello", recent[0].content)
        assertEquals("user", recent[0].role)
    }

    @Test
    fun conversationRecentAppliesLimit() = runBlocking {
        val repository = ConversationRepository(FakeConversationDao())
        repeat(10) { repository.saveMessage("user", "message $it") }
        assertEquals(5, repository.getRecentMessages(5).size)
        assertEquals(10, repository.getRecentMessages().size)
    }

    @Test
    fun conversationRecentOrdersByTimestampDescending() = runBlocking {
        val dao = FakeConversationDao()
        dao.insert(ConversationEntity(role = "user", content = "first", timestamp = 1000L))
        dao.insert(ConversationEntity(role = "user", content = "third", timestamp = 3000L))
        dao.insert(ConversationEntity(role = "user", content = "second", timestamp = 2000L))
        val repository = ConversationRepository(dao)
        val recent = repository.getRecentMessages()
        assertEquals("third", recent[0].content)
        assertEquals("second", recent[1].content)
        assertEquals("first", recent[2].content)
    }

    @Test
    fun conversationClearHistoryEmptiesAll() = runBlocking {
        val repository = ConversationRepository(FakeConversationDao())
        repository.saveMessage("user", "hello")
        repository.saveMessage("assistant", "hi")
        repository.clearHistory()
        assertTrue(repository.getRecentMessages().isEmpty())
    }

    @Test
    fun commandUsageTrackCreatesNewEntry() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("timer")
        assertEquals(1, repository.getUsageCount("timer"))
    }

    @Test
    fun commandUsageTrackIncrementsExisting() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("calculator")
        repository.trackUsage("calculator")
        repository.trackUsage("calculator")
        assertEquals(3, repository.getUsageCount("calculator"))
    }

    @Test
    fun commandUsageUnknownCommandReturnsZero() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        assertEquals(0, repository.getUsageCount("unknown"))
        assertEquals(0L, repository.getLastUsed("unknown"))
    }

    @Test
    fun commandUsageMostUsedReturnsSorted() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repeat(5) { repository.trackUsage("rare") }
        repeat(20) { repository.trackUsage("common") }
        repeat(10) { repository.trackUsage("medium") }
        val mostUsed = repository.getMostUsedCommands(2)
        assertEquals("common", mostUsed[0].command)
        assertEquals("medium", mostUsed[1].command)
    }

    @Test
    fun commandUsageDeleteByCommand() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("deleteMe")
        repository.trackUsage("keepMe")
        repository.deleteByCommand("deleteMe")
        assertEquals(0, repository.getUsageCount("deleteMe"))
        assertTrue(repository.getUsageCount("keepMe") > 0)
    }

    @Test
    fun commandUsageDeleteAll() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("a")
        repository.trackUsage("b")
        repository.deleteAll()
        assertTrue(repository.getAllUsage().isEmpty())
    }

    @Test
    fun commandUsageGetAllReturnsEverything() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("a")
        repository.trackUsage("b")
        repository.trackUsage("a")
        assertEquals(2, repository.getAllUsage().size)
    }

    @Test
    fun commandUsageLastUsedTimestampSet() = runBlocking {
        val repository = CommandUsageRepository(FakeCommandUsageDao())
        repository.trackUsage("timer")
        assertTrue(repository.getLastUsed("timer") > 0L)
    }
}
