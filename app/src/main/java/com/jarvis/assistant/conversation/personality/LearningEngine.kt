package com.jarvis.assistant.conversation.personality

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class LearningConfig(
    val enabled: Boolean = true,
    val learningRate: Float = 0.1f,
    val decayFactor: Float = 0.99f,
    val minInteractions: Int = 3,
    val maxPreferences: Int = 100
)

data class Preference(
    val key: String,
    val value: String,
    val confidence: Float,
    val interactionCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class LearningResult(
    val preference: Preference,
    val isNew: Boolean,
    val confidenceChange: Float
)

data class UserProfile(
    val preferences: Map<String, Preference>,
    val totalInteractions: Int,
    val topPreferences: List<Preference>,
    val learningProgress: Float
)

class LearningEngine(
    private val config: LearningConfig = LearningConfig()
) {
    private val mutex = Mutex()
    private val preferences = mutableMapOf<String, Preference>()
    private var totalInteractions = 0

    suspend fun learn(key: String, value: String): LearningResult = mutex.withLock {
        totalInteractions++

        val existing = preferences[key]
        val result = if (existing != null) {
            val newConfidence = ((existing.confidence * existing.interactionCount + config.learningRate) /
                    (existing.interactionCount + 1)).coerceIn(0f, 1f)
            val updated = existing.copy(
                value = if (newConfidence > existing.confidence) value else existing.value,
                confidence = newConfidence,
                interactionCount = existing.interactionCount + 1,
                lastUpdated = System.currentTimeMillis()
            )
            preferences[key] = updated
            LearningResult(updated, false, newConfidence - existing.confidence)
        } else {
            val newPref = Preference(key, value, config.learningRate, 1)
            preferences[key] = newPref
            LearningResult(newPref, true, config.learningRate)
        }

        if (preferences.size > config.maxPreferences) {
            val oldest = preferences.minByOrNull { it.value.lastUpdated }
            oldest?.let { preferences.remove(it.key) }
        }

        result
    }

    suspend fun getPreference(key: String): Preference? = mutex.withLock {
        preferences[key]
    }

    suspend fun getAllPreferences(): Map<String, Preference> = mutex.withLock {
        preferences.toMap()
    }

    suspend fun getUserProfile(): UserProfile = mutex.withLock {
        val top = preferences.values
            .sortedByDescending { it.confidence * it.interactionCount }
            .take(10)

        val progress = (preferences.size.toFloat() / config.maxPreferences).coerceIn(0f, 1f)

        UserProfile(
            preferences = preferences.toMap(),
            totalInteractions = totalInteractions,
            topPreferences = top,
            learningProgress = progress
        )
    }

    suspend fun inferPreference(key: String, context: String): String? = mutex.withLock {
        val pref = preferences[key] ?: return@withLock null
        if (pref.confidence > 0.5f && pref.interactionCount >= config.minInteractions) {
            pref.value
        } else null
    }

    suspend fun forget(key: String) = mutex.withLock {
        preferences.remove(key)
    }

    suspend fun clearAll() = mutex.withLock {
        preferences.clear()
        totalInteractions = 0
    }

    suspend fun getPreferenceCount(): Int = mutex.withLock { preferences.size }

    suspend fun getInteractionCount(): Int = mutex.withLock { totalInteractions }

    fun getConfidenceLevel(confidence: Float): String = when {
        confidence >= 0.8f -> "high"
        confidence >= 0.5f -> "medium"
        confidence >= 0.2f -> "low"
        else -> "very_low"
    }
}
