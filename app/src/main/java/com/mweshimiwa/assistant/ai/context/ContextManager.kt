package com.mweshimiwa.assistant.ai.context

import com.mweshimiwa.assistant.ai.model.ConversationRequest
import com.mweshimiwa.assistant.ai.model.GenerationConfig
import com.mweshimiwa.assistant.ai.model.Message
import com.mweshimiwa.assistant.conversation.personality.MweshimiwaPersonality

class ContextManager(
    private val personality: MweshimiwaPersonality,
    private val maxContextTokens: Int = 4096
) {
    private val recentMessages = mutableListOf<Message>()

    fun buildRequest(
        userMessage: String,
        generationConfig: GenerationConfig = GenerationConfig()
    ): ConversationRequest {
        val systemPrompt = personality.buildSystemPrompt()
        val contextMessages = buildContextMessages()

        return ConversationRequest(
            systemPrompt = systemPrompt,
            messages = contextMessages,
            userMessage = userMessage,
            generationConfig = generationConfig
        )
    }

    fun addMessage(role: String, content: String) {
        recentMessages.add(Message(role, content))
        trimToContextLimit()
    }

    fun clearHistory() {
        recentMessages.clear()
    }

    fun getRecentMessages(): List<Message> = recentMessages.toList()

    private fun buildContextMessages(): List<Message> {
        val availableTokens = maxContextTokens - estimateTokens(personality.buildSystemPrompt()) - 500
        val result = mutableListOf<Message>()
        var usedTokens = 0

        for (i in recentMessages.indices.reversed()) {
            val msg = recentMessages[i]
            val msgTokens = estimateTokens(msg.content)
            if (usedTokens + msgTokens > availableTokens) break
            result.add(0, msg)
            usedTokens += msgTokens
        }

        return result
    }

    private fun trimToContextLimit() {
        val systemTokens = estimateTokens(personality.buildSystemPrompt())
        val availableTokens = (maxContextTokens - systemTokens - 500).coerceAtLeast(100)
        var totalTokens = recentMessages.sumOf { estimateTokens(it.content) }

        while (recentMessages.isNotEmpty() && totalTokens > availableTokens) {
            val removed = recentMessages.removeAt(0)
            totalTokens -= estimateTokens(removed.content)
        }
    }

    private fun estimateTokens(text: String): Int {
        return text.split(Regex("\\s+")).size + text.length / 4
    }
}
