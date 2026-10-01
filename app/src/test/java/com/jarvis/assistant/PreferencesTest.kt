package com.jarvis.assistant

import com.jarvis.assistant.data.preferences.PreferencesState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesTest {

    @Test
    fun defaultValues() {
        val prefs = PreferencesState()
        assertTrue(prefs.darkTheme)
        assertTrue(prefs.voiceEnabled)
        assertEquals("jarvis-custom-v1", prefs.modelId)
        assertEquals("1.0.0", prefs.modelVersion)
        assertEquals(4096, prefs.contextLength)
        assertEquals(0.7f, prefs.temperature, 0.0001f)
        assertEquals(0.9f, prefs.topP, 0.0001f)
        assertEquals(512, prefs.maxTokens)
    }

    @Test
    fun customValuesPreserved() {
        val prefs = PreferencesState(
            darkTheme = false,
            voiceEnabled = false,
            modelId = "custom-model",
            modelVersion = "2.1.0",
            contextLength = 8192,
            temperature = 1.2f,
            topP = 0.5f,
            maxTokens = 1024
        )
        assertFalse(prefs.darkTheme)
        assertFalse(prefs.voiceEnabled)
        assertEquals("custom-model", prefs.modelId)
        assertEquals(8192, prefs.contextLength)
        assertEquals(1.2f, prefs.temperature, 0.0001f)
        assertEquals(0.5f, prefs.topP, 0.0001f)
        assertEquals(1024, prefs.maxTokens)
    }

    @Test
    fun clampingBoundsValues() {
        val clamped = PreferencesState().withClamping(
            contextLength = 100,
            temperature = 5f,
            topP = 3f,
            maxTokens = 1
        )
        assertEquals(PreferencesState.MIN_CONTEXT_LENGTH, clamped.contextLength)
        assertEquals(2f, clamped.temperature, 0.0001f)
        assertEquals(1f, clamped.topP, 0.0001f)
        assertEquals(PreferencesState.MIN_MAX_TOKENS, clamped.maxTokens)
    }

    @Test
    fun clampingKeepsValidValues() {
        val clamped = PreferencesState().withClamping(
            contextLength = 2048,
            temperature = 1.0f,
            topP = 0.8f,
            maxTokens = 256
        )
        assertEquals(2048, clamped.contextLength)
        assertEquals(1.0f, clamped.temperature, 0.0001f)
        assertEquals(0.8f, clamped.topP, 0.0001f)
        assertEquals(256, clamped.maxTokens)
    }

    @Test
    fun outOfRangeConstructionThrows() {
        var thrown = false
        try {
            PreferencesState(contextLength = 100)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)

        thrown = false
        try {
            PreferencesState(temperature = -1f)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)

        thrown = false
        try {
            PreferencesState(topP = 1.5f)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)

        thrown = false
        try {
            PreferencesState(maxTokens = 10)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)
    }

    @Test
    fun jsonRoundTrip() {
        val prefs = PreferencesState(
            darkTheme = false,
            voiceEnabled = true,
            modelId = "test-model",
            modelVersion = "3.2.1",
            contextLength = 2048,
            temperature = 1.5f,
            topP = 0.3f,
            maxTokens = 128
        )
        val json = prefs.toJson()
        val restored = PreferencesState.fromJson(json)
        assertEquals(prefs, restored)
    }

    @Test
    fun jsonRoundTripDefaults() {
        val json = PreferencesState().toJson()
        val restored = PreferencesState.fromJson(json)!!
        assertEquals(PreferencesState(), restored)
    }

    @Test
    fun fromJsonWithInvalidJsonReturnsNull() {
        assertNull(PreferencesState.fromJson("not json"))
        assertNull(PreferencesState.fromJson("{invalid}"))
        assertNull(PreferencesState.fromJson(""))
    }

    @Test
    fun fromJsonWithPartialJsonUsesDefaults() {
        val restored = PreferencesState.fromJson("{\"darkTheme\":false}")!!
        assertFalse(restored.darkTheme)
        assertEquals(4096, restored.contextLength)
        assertEquals(0.7f, restored.temperature, 0.0001f)
    }

    @Test
    fun fromJsonWithOutOfRangeValuesReturnsNull() {
        assertNull(PreferencesState.fromJson("{\"contextLength\":100}"))
        assertNull(PreferencesState.fromJson("{\"temperature\":9}"))
    }

    @Test
    fun jsonContainsAllFields() {
        val json = PreferencesState().toJson()
        assertTrue(json.contains("\"darkTheme\""))
        assertTrue(json.contains("\"voiceEnabled\""))
        assertTrue(json.contains("\"modelId\""))
        assertTrue(json.contains("\"modelVersion\""))
        assertTrue(json.contains("\"contextLength\""))
        assertTrue(json.contains("\"temperature\""))
        assertTrue(json.contains("\"topP\""))
        assertTrue(json.contains("\"maxTokens\""))
    }

    @Test
    fun jsonEscapesSpecialCharacters() {
        val prefs = PreferencesState(modelId = "model\"with\\quotes")
        val json = prefs.toJson()
        val restored = PreferencesState.fromJson(json)
        assertNotEquals(null, restored)
        assertEquals("model\"with\\quotes", restored!!.modelId)
    }
}
