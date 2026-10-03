package com.mweshimiwa.assistant.commands.handlers

import com.mweshimiwa.assistant.commands.router.CommandHandler
import com.mweshimiwa.assistant.commands.router.CommandType

class TimerCommandHandler : CommandHandler {

    override val type = CommandType.TIMER

    private val numberWords = mapOf(
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "ten" to 10,
        "fifteen" to 15, "twenty" to 20, "thirty" to 30
    )

    private val patterns = listOf(
        Regex("set (?:a )?timer for (\\d+|one|two|three|four|five|six|seven|eight|nine|ten|fifteen|twenty|thirty) (minutes?|seconds?|hours?)"),
        Regex("timer for (\\d+|one|two|three|four|five|six|seven|eight|nine|ten|fifteen|twenty|thirty) (minutes?|seconds?|hours?)"),
        Regex("countdown (?:for )?(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|fifteen|twenty|thirty) (minutes?|seconds?|hours?)")
    )

    override fun matches(normalizedInput: String): Boolean {
        return patterns.any { it.containsMatchIn(normalizedInput) }
    }

    override fun extractParameters(normalizedInput: String): Map<String, String> {
        for (pattern in patterns) {
            val match = pattern.find(normalizedInput)
            if (match != null) {
                val value = match.groupValues[1]
                val unit = match.groupValues[2]
                val numericValue = value.toIntOrNull() ?: numberWords[value] ?: 0
                return mapOf(
                    "duration" to numericValue.toString(),
                    "unit" to unit
                )
            }
        }
        return emptyMap()
    }
}
