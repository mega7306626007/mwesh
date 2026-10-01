package com.jarvis.assistant.data.model

data class Conversation(
    val id: String,
    val title: String,
    val assistantId: String = "",
    val userId: String = "",
    val messageCount: Int = 0,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastMessageAt: Long = 0L
) {
    fun isActive(): Boolean {
        return !isArchived
    }

    fun isPinnedAndActive(): Boolean {
        return isPinned && !isArchived
    }

    fun getFormattedDate(): String {
        val diff = System.currentTimeMillis() - createdAt
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> "${diff / 604800000}w ago"
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "assistantId" to assistantId,
            "userId" to userId,
            "messageCount" to messageCount,
            "isArchived" to isArchived,
            "isPinned" to isPinned,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "lastMessageAt" to lastMessageAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Conversation {
            return Conversation(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                assistantId = map["assistantId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                messageCount = (map["messageCount"] as? Number)?.toInt() ?: 0,
                isArchived = map["isArchived"] as? Boolean ?: false,
                isPinned = map["isPinned"] as? Boolean ?: false,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                lastMessageAt = (map["lastMessageAt"] as? Number)?.toLong() ?: 0L
            )
        }

        fun create(
            title: String,
            assistantId: String = "",
            userId: String = ""
        ): Conversation {
            return Conversation(
                id = generateId(),
                title = title,
                assistantId = assistantId,
                userId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "conv_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

data class Message(
    val id: String,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val tokensUsed: Int = 0,
    val isRead: Boolean = false,
    val isEdited: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isFromUser(): Boolean {
        return role == MessageRole.USER
    }

    fun isFromAssistant(): Boolean {
        return role == MessageRole.ASSISTANT
    }

    fun isFromSystem(): Boolean {
        return role == MessageRole.SYSTEM
    }

    fun getPreview(maxLength: Int = 100): String {
        return if (content.length > maxLength) {
            content.take(maxLength) + "..."
        } else {
            content
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "conversationId" to conversationId,
            "role" to role.name,
            "content" to content,
            "tokensUsed" to tokensUsed,
            "isRead" to isRead,
            "isEdited" to isEdited,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Message {
            return Message(
                id = map["id"] as? String ?: "",
                conversationId = map["conversationId"] as? String ?: "",
                role = try {
                    MessageRole.valueOf(map["role"] as? String ?: "USER")
                } catch (e: Exception) {
                    MessageRole.USER
                },
                content = map["content"] as? String ?: "",
                tokensUsed = (map["tokensUsed"] as? Number)?.toInt() ?: 0,
                isRead = map["isRead"] as? Boolean ?: false,
                isEdited = map["isEdited"] as? Boolean ?: false,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(
            conversationId: String,
            role: MessageRole,
            content: String
        ): Message {
            return Message(
                id = generateId(),
                conversationId = conversationId,
                role = role,
                content = content,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "msg_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
    TOOL,
    UNKNOWN
}
