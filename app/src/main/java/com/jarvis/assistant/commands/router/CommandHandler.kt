package com.jarvis.assistant.commands.router

interface CommandHandler {
    val type: CommandType
    fun matches(normalizedInput: String): Boolean
    fun extractParameters(normalizedInput: String): Map<String, String>
}
