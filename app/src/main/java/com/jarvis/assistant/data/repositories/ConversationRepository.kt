package com.jarvis.assistant.data.repositories

import com.jarvis.assistant.data.database.ConversationDao
import com.jarvis.assistant.data.database.ConversationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConversationRepository(private val dao: ConversationDao) {

    suspend fun saveMessage(role: String, content: String) {
        withContext(Dispatchers.IO) {
            dao.insert(ConversationEntity(role = role, content = content))
        }
    }

    suspend fun getRecentMessages(limit: Int = 50): List<ConversationEntity> {
        return withContext(Dispatchers.IO) {
            dao.getRecent(limit)
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            dao.clearAll()
        }
    }
}
