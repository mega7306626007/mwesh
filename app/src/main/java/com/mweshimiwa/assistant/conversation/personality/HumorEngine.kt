package com.mweshimiwa.assistant.conversation.personality

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class HumorConfig(
    val humorLevel: HumorLevel = HumorLevel.SUBTLE,
    val useContext: Boolean = true,
    val avoidSensitiveTopics: Boolean = true,
    val maxJokeLength: Int = 200
)

data class Joke(
    val setup: String,
    val punchline: String,
    val category: String,
    val language: String
)

data class HumorResult(
    val joke: Joke?,
    val context: String,
    val appropriateness: Float,
    val delivery: String
)

class HumorEngine(
    private val config: HumorConfig = HumorConfig()
) {

    private val jokes = mapOf(
        "tech" to listOf(
            Joke("Why do programmers prefer dark mode?", "Because light attracts bugs!", "tech", "en"),
            Joke("Why did the developer go broke?", "Because he used up all his cache!", "tech", "en"),
            Joke("What's a computer's favorite snack?", "Microchips!", "tech", "en"),
            Joke("Why was the computer cold?", "It left its Windows open!", "tech", "en"),
            Joke("How many programmers does it take to change a light bulb?", "None, that's a hardware problem!", "tech", "en")
        ),
        "wordplay" to listOf(
            Joke("Why don't scientists trust atoms?", "Because they make up everything!", "wordplay", "en"),
            Joke("What do you call a fake noodle?", "An impasta!", "wordplay", "en"),
            Joke("Why did the scarecrow win an award?", "He was outstanding in his field!", "wordplay", "en"),
            Joke("What do you call a bear with no teeth?", "A gummy bear!", "wordplay", "en")
        ),
        "swahili" to listOf(
            Joke("Kwa nini program alikwenda hospitali?", "Kwa sababu alikuwa na virus!", "swahili", "sw"),
            Joke("Kuna aina ngapi ya watu?", "Kumi: wanaotambua binary na wanaotambua.", "swahili", "sw"),
            Joke("Mwalimu alisema: 'Kuna lugha 3 za program.' Mwanafunzi akauliza: 'Na Kiswahili?'", "...", "swahili", "sw")
        ),
        "sheng" to listOf(
            Joke("Programmer anapendelea dark mode kwa sababu?", "Bugs huenda light!", "sheng", "sheng"),
            Joke("Kuna aina ngapi ya watu?", "Kumi: wanaotambua binary na wanaotambua.", "sheng", "sheng")
        )
    )

    private val humorTriggers = listOf(
        "joke", "funny", "humor", "laugh", "make me laugh", "cheka", "utani", "poa"
    )

    suspend fun generateJoke(context: String = "", language: String = "en"): HumorResult = withContext(Dispatchers.Default) {
        if (config.humorLevel == HumorLevel.NONE) {
            return@withContext HumorResult(null, context, 0f, "Humor disabled")
        }

        val category = selectCategory(context, language)
        val availableJokes = jokes[category] ?: jokes["tech"]!!

        val joke = if (config.useContext && context.isNotBlank()) {
            findContextualJoke(context, availableJokes) ?: availableJokes.random()
        } else {
            availableJokes.random()
        }

        val appropriateness = assessAppropriateness(context, joke)
        val delivery = buildDelivery(joke, appropriateness)

        HumorResult(joke, context, appropriateness, delivery)
    }

    suspend fun shouldRespondWithHumor(message: String): Boolean = withContext(Dispatchers.Default) {
        val lower = message.lowercase()
        val hasTrigger = humorTriggers.any { lower.contains(it) }
        val hasQuestion = lower.contains("?")
        val isCasual = lower.split(Regex("\\s+")).size < 10

        hasTrigger || (hasQuestion && isCasual && config.humorLevel.ordinal >= HumorLevel.MODERATE.ordinal)
    }

    private fun selectCategory(context: String, language: String): String {
        return when {
            language == "sw" -> "swahili"
            language == "sheng" -> "sheng"
            context.contains("code") || context.contains("program") || context.contains("computer") -> "tech"
            else -> "wordplay"
        }
    }

    private fun findContextualJoke(context: String, jokes: List<Joke>): Joke? {
        val contextWords = context.lowercase().split(Regex("\\s+")).toSet()
        return jokes.maxByOrNull { joke ->
            val jokeWords = (joke.setup + " " + joke.punchline).lowercase().split(Regex("\\s+")).toSet()
            contextWords.intersect(jokeWords).size
        }
    }

    private fun assessAppropriateness(context: String, joke: Joke): Float {
        if (!config.avoidSensitiveTopics) return 1f

        val sensitiveTopics = listOf("death", "tragedy", "politics", "religion", "violence")
        val contextLower = context.lowercase()
        val jokeLower = (joke.setup + " " + joke.punchline).lowercase()

        val hasSensitive = sensitiveTopics.any {
            contextLower.contains(it) || jokeLower.contains(it)
        }

        return if (hasSensitive) 0.2f else 0.9f
    }

    private fun buildDelivery(joke: Joke, appropriateness: Float): String {
        return when {
            appropriateness > 0.7f -> "${joke.setup} ${joke.punchline}"
            appropriateness > 0.4f -> joke.setup
            else -> "I'll keep it professional."
        }
    }

    fun getJokeCount(): Int = jokes.values.sumOf { it.size }
}
