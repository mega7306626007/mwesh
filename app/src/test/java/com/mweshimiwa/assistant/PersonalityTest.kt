package com.mweshimiwa.assistant

import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality
import com.mweshimiwa.assistant.conversation.personality.PersonalityResponseSelector
import com.mweshimiwa.assistant.voice.speech.InputNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalityTest {

    private val personality = MweshimiwaPersonality()
    private val selector = PersonalityResponseSelector(personality)

    @Test
    fun systemPromptContainsIdentity() {
        val prompt = personality.buildSystemPrompt()
        assertTrue(prompt.contains("Mweshimiwa"))
        assertTrue(prompt.contains("Emmanuel"))
    }

    @Test
    fun systemPromptContainsTraits() {
        val prompt = personality.buildSystemPrompt()
        assertTrue(prompt.contains("helpful"))
        assertTrue(prompt.contains("concise"))
        assertTrue(prompt.contains("witty"))
    }

    @Test
    fun systemPromptContainsLanguages() {
        val prompt = personality.buildSystemPrompt()
        assertTrue(prompt.contains("English"))
        assertTrue(prompt.contains("Kiswahili"))
        assertTrue(prompt.contains("Sheng"))
    }

    @Test
    fun systemPromptContainsBehavioralGuidelines() {
        val prompt = personality.buildSystemPrompt()
        assertTrue(prompt.contains("offline"))
        assertTrue(prompt.contains("honestly"))
    }

    @Test
    fun greetingMentionsCreatorAndIdentity() {
        val greeting = personality.getGreeting()
        assertTrue(greeting.contains("Emmanuel"))
        assertTrue(greeting.contains("Mweshimiwa"))
    }

    @Test
    fun personalityExposesVersionAndLanguages() {
        assertEquals("1.0.0", personality.version)
        assertEquals(3, personality.languages.size)
        assertTrue(personality.languages.contains("English"))
    }

    @Test
    fun greetingSelectionByTimeOfDay() {
        assertTrue(selector.selectGreeting(6).contains("Good morning"))
        assertTrue(selector.selectGreeting(11).contains("Good morning"))
        assertTrue(selector.selectGreeting(13).contains("Good afternoon"))
        assertTrue(selector.selectGreeting(16).contains("Good afternoon"))
        assertTrue(selector.selectGreeting(18).contains("Good evening"))
        assertTrue(selector.selectGreeting(21).contains("Good evening"))
        assertTrue(selector.selectGreeting(23).startsWith("Hello"))
        assertTrue(selector.selectGreeting(2).startsWith("Hello"))
    }

    @Test
    fun acknowledgementByLanguage() {
        assertTrue(selector.selectAcknowledgement("KISWAHILI").isNotBlank())
        assertTrue(selector.selectAcknowledgement("SHENG").isNotBlank())
        assertTrue(selector.selectAcknowledgement("ENGLISH").contains("Sure"))
        assertTrue(selector.selectAcknowledgement("FRENCH").contains("Sure"))
    }

    @Test
    fun farewellByLanguage() {
        assertTrue(selector.selectFarewell("KISWAHILI").contains("Kwaheri"))
        assertTrue(selector.selectFarewell("SHENG").contains("Kwaheri"))
        assertTrue(selector.selectFarewell("ENGLISH").contains("Goodbye"))
        assertTrue(selector.selectFarewell("UNKNOWN").contains("Goodbye"))
    }

    @Test
    fun errorResponseByLanguage() {
        assertTrue(selector.selectErrorResponse("KISWAHILI").contains("Samahani"))
        assertTrue(selector.selectErrorResponse("SHENG").contains("Pole"))
        assertTrue(selector.selectErrorResponse("ENGLISH").contains("apologize"))
        assertTrue(selector.selectErrorResponse("SPANISH").contains("apologize"))
    }

    @Test
    fun combinedSelectionNeverBlank() {
        val response = selector.selectForLanguageAndTime("KISWAHILI", 9)
        assertTrue(response.isNotBlank())
        assertTrue(response.contains("Good morning"))
    }

    @Test
    fun languageDetectionSwahili() {
        assertEquals("KISWAHILI", InputNormalizer.detectLanguage("habari, unaweza kunisaidia"))
        assertEquals("KISWAHILI", InputNormalizer.detectLanguage("asante sana rafiki"))
    }

    @Test
    fun languageDetectionSheng() {
        assertEquals("SHENG", InputNormalizer.detectLanguage("mambo vipi, ni poa sana"))
    }

    @Test
    fun languageDetectionEnglish() {
        assertEquals("ENGLISH", InputNormalizer.detectLanguage("what is the time please"))
        assertEquals("ENGLISH", InputNormalizer.detectLanguage("hello there"))
    }

    @Test
    fun languageDetectionUnknown() {
        assertEquals("UNKNOWN", InputNormalizer.detectLanguage("xyz qrs"))
        assertEquals("UNKNOWN", InputNormalizer.detectLanguage(""))
    }

    @Test
    fun normalizerCleansInput() {
        assertEquals("Hello World", InputNormalizer.normalize("  Hello   World!  "))
        assertEquals("...test", InputNormalizer.normalize("...test..."))
    }
}
