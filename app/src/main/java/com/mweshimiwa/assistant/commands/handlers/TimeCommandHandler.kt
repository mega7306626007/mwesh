package com.mweshimiwa.assistant.commands.handlers

import com.mweshimiwa.assistant.commands.router.CommandHandler
import com.mweshimiwa.assistant.commands.router.CommandType

class TimeCommandHandler : CommandHandler {

    override val type = CommandType.TIME

    private val patterns = listOf(
        Regex("what time is it"),
        Regex("what's the time"),
        Regex("current time"),
        Regex("time now"),
        Regex("tell me the time")
    )

    override fun matches(normalizedInput: String): Boolean {
        return patterns.any { it.containsMatchIn(normalizedInput) }
    }

    override fun extractParameters(normalizedInput: String): Map<String, String> {
        return emptyMap()
    }
}
