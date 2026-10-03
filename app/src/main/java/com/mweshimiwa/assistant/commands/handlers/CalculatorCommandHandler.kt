package com.mweshimiwa.assistant.commands.handlers

import com.mweshimiwa.assistant.commands.router.CommandHandler
import com.mweshimiwa.assistant.commands.router.CommandType

class CalculatorCommandHandler : CommandHandler {

    override val type = CommandType.CALCULATOR

    private val patterns = listOf(
        Regex("calculate (.+)"),
        Regex("what is (.+)"),
        Regex("compute (.+)"),
        Regex("(\\d+(?:\\.\\d+)?)\\s*([+\\-×*/÷])\\s*(\\d+(?:\\.\\d+)?)")
    )

    override fun matches(normalizedInput: String): Boolean {
        return patterns.any { it.containsMatchIn(normalizedInput) }
    }

    override fun extractParameters(normalizedInput: String): Map<String, String> {
        val exprPattern = patterns[3]
        val match = exprPattern.find(normalizedInput)
        if (match != null) {
            return mapOf(
                "expression" to "${match.groupValues[1]} ${match.groupValues[2]} ${match.groupValues[3]}"
            )
        }

        for (i in 0..2) {
            val m = patterns[i].find(normalizedInput)
            if (m != null) {
                return mapOf("expression" to m.groupValues[1])
            }
        }
        return emptyMap()
    }
}
