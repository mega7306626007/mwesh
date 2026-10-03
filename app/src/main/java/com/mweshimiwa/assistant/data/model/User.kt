package com.mweshimiwa.assistant.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getInitials(): String {
        return name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
    }

    fun getDisplayName(): String {
        return name.ifBlank { email.substringBefore("@") }
    }

    fun isRecentlyActive(thresholdMs: Long = 300000L): Boolean {
        return System.currentTimeMillis() - lastSeen < thresholdMs
    }

    fun getFormattedLastSeen(): String {
        if (lastSeen == 0L) return "Never"
        val diff = System.currentTimeMillis() - lastSeen
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> "${diff / 604800000}w ago"
        }
    }

    fun isValidEmail(): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun hasAvatar(): Boolean {
        return !avatarUrl.isNullOrBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "avatarUrl" to (avatarUrl ?: ""),
            "isOnline" to isOnline,
            "lastSeen" to lastSeen,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                avatarUrl = map["avatarUrl"] as? String,
                isOnline = map["isOnline"] as? Boolean ?: false,
                lastSeen = (map["lastSeen"] as? Number)?.toLong() ?: 0L,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun empty(): User {
            return User(
                id = "",
                name = "",
                email = ""
            )
        }

        fun create(
            name: String,
            email: String,
            avatarUrl: String? = null
        ): User {
            return User(
                id = generateId(),
                name = name,
                email = email,
                avatarUrl = avatarUrl,
                isOnline = false,
                lastSeen = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "user_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val bio: String = "",
    val location: String = "",
    val website: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val language: String = "en",
    val timezone: String = "UTC"
) {
    fun getFullProfile(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "avatarUrl" to (avatarUrl ?: ""),
            "bio" to bio,
            "location" to location,
            "website" to website,
            "phoneNumber" to phoneNumber,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender,
            "language" to language,
            "timezone" to timezone
        )
    }

    fun isProfileComplete(): Boolean {
        return displayName.isNotBlank() &&
                email.isNotBlank() &&
                bio.isNotBlank() &&
                location.isNotBlank()
    }

    fun getCompletionPercentage(): Int {
        var completed = 0
        val total = 8
        if (displayName.isNotBlank()) completed++
        if (email.isNotBlank()) completed++
        if (!avatarUrl.isNullOrBlank()) completed++
        if (bio.isNotBlank()) completed++
        if (location.isNotBlank()) completed++
        if (website.isNotBlank()) completed++
        if (phoneNumber.isNotBlank()) completed++
        if (dateOfBirth.isNotBlank()) completed++
        return (completed * 100) / total
    }
}

data class UserPreferences(
    val userId: String,
    val theme: String = "system",
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoSync: Boolean = true,
    val dataSaver: Boolean = false,
    val analyticsEnabled: Boolean = true,
    val crashReporting: Boolean = true
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "theme" to theme,
            "language" to language,
            "notificationsEnabled" to notificationsEnabled,
            "soundEnabled" to soundEnabled,
            "vibrationEnabled" to vibrationEnabled,
            "autoSync" to autoSync,
            "dataSaver" to dataSaver,
            "analyticsEnabled" to analyticsEnabled,
            "crashReporting" to crashReporting
        )
    }

    companion object {
        fun default(userId: String): UserPreferences {
            return UserPreferences(userId = userId)
        }

        fun fromMap(map: Map<String, Any>): UserPreferences {
            return UserPreferences(
                userId = map["userId"] as? String ?: "",
                theme = map["theme"] as? String ?: "system",
                language = map["language"] as? String ?: "en",
                notificationsEnabled = map["notificationsEnabled"] as? Boolean ?: true,
                soundEnabled = map["soundEnabled"] as? Boolean ?: true,
                vibrationEnabled = map["vibrationEnabled"] as? Boolean ?: true,
                autoSync = map["autoSync"] as? Boolean ?: true,
                dataSaver = map["dataSaver"] as? Boolean ?: false,
                analyticsEnabled = map["analyticsEnabled"] as? Boolean ?: true,
                crashReporting = map["crashReporting"] as? Boolean ?: true
            )
        }
    }
}
