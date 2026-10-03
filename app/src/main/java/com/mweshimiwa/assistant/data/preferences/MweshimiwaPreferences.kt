package com.mweshimiwa.assistant.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mweshimiwa_prefs")

class MweshimiwaPreferences(private val context: Context) {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val MODEL_ID = stringPreferencesKey("model_id")
        val MODEL_VERSION = stringPreferencesKey("model_version")
        val CONTEXT_LENGTH = intPreferencesKey("context_length")
        val TEMPERATURE = floatPreferencesKey("temperature")
        val TOP_P = floatPreferencesKey("top_p")
        val MAX_TOKENS = intPreferencesKey("max_tokens")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_THEME] ?: true }
    val isVoiceEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.VOICE_ENABLED] ?: true }
    val modelId: Flow<String> = context.dataStore.data.map { it[Keys.MODEL_ID] ?: "mweshimiwa-custom-v1" }
    val modelVersion: Flow<String> = context.dataStore.data.map { it[Keys.MODEL_VERSION] ?: "1.0.0" }
    val contextLength: Flow<Int> = context.dataStore.data.map { it[Keys.CONTEXT_LENGTH] ?: 4096 }
    val temperature: Flow<Float> = context.dataStore.data.map { it[Keys.TEMPERATURE] ?: 0.7f }
    val topP: Flow<Float> = context.dataStore.data.map { it[Keys.TOP_P] ?: 0.9f }
    val maxTokens: Flow<Int> = context.dataStore.data.map { it[Keys.MAX_TOKENS] ?: 512 }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOICE_ENABLED] = enabled }
    }

    suspend fun setModelId(id: String) {
        context.dataStore.edit { it[Keys.MODEL_ID] = id }
    }

    suspend fun setModelVersion(version: String) {
        context.dataStore.edit { it[Keys.MODEL_VERSION] = version }
    }

    suspend fun setContextLength(length: Int) {
        context.dataStore.edit { it[Keys.CONTEXT_LENGTH] = length }
    }

    suspend fun setTemperature(temp: Float) {
        context.dataStore.edit { it[Keys.TEMPERATURE] = temp }
    }

    suspend fun setTopP(topP: Float) {
        context.dataStore.edit { it[Keys.TOP_P] = topP }
    }

    suspend fun setMaxTokens(tokens: Int) {
        context.dataStore.edit { it[Keys.MAX_TOKENS] = tokens }
    }
}
