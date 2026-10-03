package com.mweshimiwa.assistant.voice.speech

object InputNormalizer {

    fun normalize(input: String): String {
        return input
            .trim()
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[.!?]+$"), "")
            .replace(Regex("^[,;:\\s]+"), "")
            .trim()
    }

    fun detectLanguage(input: String): String {
        val lower = input.lowercase()

        val swahiliIndicators = listOf(
            "habari", "asante", "tafadhali", "ndiyo", "hapana", "ninahitaji",
            "unaweza", "nini", "wapi", "lini", "ngani", "jina", "mwambao"
        )

        val shengIndicators = listOf(
            "mambo", "vipi", "poa", "sasa", "niaje", "mbwe", "mnoma",
            "buda", "kali", "fala", "dame", "kijana"
        )

        val englishIndicators = listOf(
            "the", "is", "are", "what", "how", "can", "please", "would",
            "could", "should", "hello", "hey", "time", "help"
        )

        val swahiliCount = swahiliIndicators.count { lower.contains(it) }
        val shengCount = shengIndicators.count { lower.contains(it) }
        val englishCount = englishIndicators.count { lower.contains(it) }

        return when {
            swahiliCount > englishCount && swahiliCount > shengCount -> "KISWAHILI"
            shengCount > englishCount && shengCount > swahiliCount -> "SHENG"
            englishCount > 0 -> "ENGLISH"
            else -> "UNKNOWN"
        }
    }
}
