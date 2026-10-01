package com.jarvis.assistant.commands.router

import com.jarvis.assistant.core.logging.JarvisLogger

class CommandRouter(
    private val handlers: List<CommandHandler>
) {
    fun route(input: String): CommandIntent? {
        val normalized = normalize(input)

        for (handler in handlers) {
            if (handler.matches(normalized)) {
                val params = handler.extractParameters(normalized)
                JarvisLogger.i("Command routed: ${handler.type} from '$input'")
                return CommandIntent(
                    command = handler.type,
                    confidence = 1.0f,
                    parameters = params,
                    rawInput = input
                )
            }
        }
        return null
    }

    private fun normalize(input: String): String {
        return input
            .trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s×+÷\\-'.]"), "")
            .replace(Regex("\\s+"), " ")
    }
}
