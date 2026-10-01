package com.jarvis.assistant.ai.context

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ln
import kotlin.math.sqrt

data class SearchResult(
    val memory: Memory,
    val score: Float,
    val matchedTerms: List<String>
)

data class SearchConfig(
    val maxResults: Int = 10,
    val minScore: Float = 0.01f,
    val useIdf: Boolean = true,
    val boostRecent: Boolean = true,
    val boostFactor: Float = 0.1f
)

class SemanticSearch(
    private val config: SearchConfig = SearchConfig()
) {
    private val documents = mutableListOf<Memory>()
    private val documentFrequency = mutableMapOf<String, Int>()
    private var totalDocuments = 0

    fun index(memories: List<Memory>) {
        documents.clear()
        documentFrequency.clear()
        documents.addAll(memories)
        totalDocuments = memories.size

        for (doc in memories) {
            val terms = extractTerms(doc.content)
            for (term in terms.distinct()) {
                documentFrequency[term] = (documentFrequency[term] ?: 0) + 1
            }
        }
    }

    fun addDocument(memory: Memory) {
        documents.add(memory)
        totalDocuments++
        val terms = extractTerms(memory.content)
        for (term in terms.distinct()) {
            documentFrequency[term] = (documentFrequency[term] ?: 0) + 1
        }
    }

    fun removeDocument(memoryId: String) {
        documents.removeAll { it.id == memoryId }
        totalDocuments = documents.size
        rebuildIndex()
    }

    suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.Default) {
        if (documents.isEmpty() || query.isBlank()) return@withContext emptyList()

        val queryTerms = extractTerms(query)
        if (queryTerms.isEmpty()) return@withContext emptyList()

        val results = documents.map { doc ->
            val score = calculateTfIdf(queryTerms, doc)
            val matchedTerms = queryTerms.filter { term ->
                extractTerms(doc.content).contains(term)
            }
            SearchResult(doc, score, matchedTerms)
        }.filter { it.score >= config.minScore }
            .sortedByDescending { it.score }
            .take(config.maxResults)

        results
    }

    private fun calculateTfIdf(queryTerms: List<String>, doc: Memory): Float {
        val docTerms = extractTerms(doc.content)
        if (docTerms.isEmpty()) return 0f

        val docLength = docTerms.size.toFloat()
        var score = 0f

        for (queryTerm in queryTerms) {
            val tf = docTerms.count { it == queryTerm }.toFloat() / docLength
            val df = documentFrequency[queryTerm] ?: 0
            val idf = if (config.useIdf && df > 0) {
                ln((totalDocuments.toFloat() + 1) / (df.toFloat() + 1)) + 1
            } else 1f

            score += tf * idf
        }

        if (config.boostRecent) {
            val ageHours = (System.currentTimeMillis() - doc.timestamp).toFloat() / (1000f * 60f * 60f)
            val recencyBoost = config.boostFactor / (1f + ageHours / 24f)
            score += recencyBoost
        }

        return score
    }

    private fun extractTerms(text: String): List<String> {
        val stopWords = setOf(
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "may", "might", "shall", "can", "to", "of", "in", "for",
            "on", "with", "at", "by", "from", "as", "into", "through", "during",
            "before", "after", "above", "below", "between", "and", "but", "or",
            "if", "while", "about", "up", "it", "its", "i", "me", "my", "we",
            "our", "you", "your", "he", "him", "his", "she", "her", "they",
            "them", "their", "this", "that", "these", "those", "what", "which",
            "who", "whom", "am", "ni", "na", "ya", "wa", "la", "za", "kwa"
        )

        return text.lowercase()
            .split(Regex("[^\\p{L}]+"))
            .filter { it.length > 2 }
            .filter { it !in stopWords }
    }

    private fun rebuildIndex() {
        documentFrequency.clear()
        for (doc in documents) {
            val terms = extractTerms(doc.content)
            for (term in terms.distinct()) {
                documentFrequency[term] = (documentFrequency[term] ?: 0) + 1
            }
        }
    }

    fun getDocumentCount(): Int = documents.size
    fun getVocabularySize(): Int = documentFrequency.size

    fun getTopTerms(n: Int = 20): List<Pair<String, Int>> {
        return documentFrequency.entries
            .sortedByDescending { it.value }
            .take(n)
            .map { it.key to it.value }
    }
}
