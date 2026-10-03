package com.mweshimiwa.assistant.data.repositories

import com.mweshimiwa.assistant.data.database.MemoryDao
import com.mweshimiwa.assistant.data.database.MemoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryRepository(private val dao: MemoryDao) {

    suspend fun store(key: String, value: String, category: String = "general") {
        withContext(Dispatchers.IO) {
            dao.insert(MemoryEntity(key = key, value = value, category = category))
        }
    }

    suspend fun recall(key: String): String? {
        return withContext(Dispatchers.IO) {
            dao.getByKey(key)?.value
        }
    }

    suspend fun getByCategory(category: String): List<MemoryEntity> {
        return withContext(Dispatchers.IO) {
            dao.getByCategory(category)
        }
    }

    suspend fun getAll(): List<MemoryEntity> {
        return withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    suspend fun forget(key: String) {
        withContext(Dispatchers.IO) {
            dao.deleteByKey(key)
        }
    }
}
