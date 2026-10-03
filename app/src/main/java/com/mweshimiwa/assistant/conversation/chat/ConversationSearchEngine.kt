package com.mweshimiwa.assistant.conversation.chat

import com.mweshimiwa.assistant.data.database.ConversationEntity

data class SearchResult(
    val message: ConversationEntity,
    val score: Float,
    val highlightedContent: String,
    val snippet: String
)

class ConversationSearchEngine(
    private val contextRadius: Int = 40,
    private val highlightPrefix: String = "**",
    private val highlightSuffix: String = "**"
) {
    fun search(messages: List<ConversationEntity>, query: String): List<SearchResult> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        val terms = q.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (terms.isEmpty()) return emptyList()
        return messages.mapNotNull { msg -> scoreMessage(msg, terms) }
            .sortedByDescending { it.score }
    }

    fun searchFirst(messages: List<ConversationEntity>, query: String): SearchResult? =
        search(messages, query).firstOrNull()

    private fun scoreMessage(message: ConversationEntity, terms: List<String>): SearchResult? {
        val content = message.content
        val lower = content.lowercase()
        var score = 0f
        for (term in terms) {
            var termScore = 0f
            var index = 0
            while (index < lower.length) {
                val found = lower.indexOf(term, index)
                if (found < 0) break
                val wordBoundary = found == 0 || !lower[found - 1].isLetterOrDigit()
                termScore += if (wordBoundary) 2f else 1f
                index = found + term.length
            }
            if (termScore == 0f) return null
            score += termScore
        }
        if (message.role.equals("user", ignoreCase = true)) score *= 1.2f
        return SearchResult(
            message = message,
            score = score,
            highlightedContent = highlight(content, terms),
            snippet = buildSnippet(content, terms.first())
        )
    }

    private fun highlight(content: String, terms: List<String>): String {
        var result = content
        val sortedTerms = terms.sortedByDescending { it.length }.distinct()
        for (term in sortedTerms) {
            val regex = Regex("(${Regex.escape(term)})", RegexOption.IGNORE_CASE)
            result = result.replace(regex, "$highlightPrefix$1$highlightSuffix")
        }
        return result
    }

    private fun buildSnippet(content: String, term: String): String {
        val lower = content.lowercase()
        val index = lower.indexOf(term)
        if (index < 0) {
            return if (content.length > contextRadius * 2) content.take(contextRadius * 2) + "..." else content
        }
        val start = (index - contextRadius).coerceAtLeast(0)
        val end = (index + term.length + contextRadius).coerceAtMost(content.length)
        val prefix = if (start > 0) "..." else ""
        val suffix = if (end < content.length) "..." else ""
        return prefix + content.substring(start, end) + suffix
    }
}
