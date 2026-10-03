package com.mweshimiwa.assistant.ai.context

data class Memory(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val importance: Float = 0.5f,
    val category: String = "general",
    val keywords: List<String> = emptyList(),
    val accessCount: Int = 0,
    val lastAccessed: Long = 0L
)

data class RetrievalResult(
    val memory: Memory,
    val score: Float,
    val matchType: MatchType
)

enum class MatchType {
    EXACT,
    KEYWORD,
    FUZZY,
    SEMANTIC,
    RECENCY
}

data class RetrievalConfig(
    val maxResults: Int = 5,
    val minScore: Float = 0.1f,
    val recencyBoost: Float = 0.1f,
    val importanceWeight: Float = 0.3f,
    val keywordWeight: Float = 0.4f,
    val fuzzyWeight: Float = 0.3f,
    val useStemming: Boolean = true,
    val caseSensitive: Boolean = false
)

class MemoryRetriever(
    private val config: RetrievalConfig = RetrievalConfig()
) {
    private val memories = mutableListOf<Memory>()

    fun addMemory(memory: Memory) {
        memories.add(memory)
    }

    fun addMemories(newMemories: List<Memory>) {
        memories.addAll(newMemories)
    }

    fun removeMemory(id: String) {
        memories.removeAll { it.id == id }
    }

    fun clear() {
        memories.clear()
    }

    fun retrieve(query: String): List<RetrievalResult> {
        if (query.isBlank() || memories.isEmpty()) return emptyList()

        val queryKeywords = extractKeywords(query)
        val results = mutableListOf<RetrievalResult>()

        for (memory in memories) {
            val score = calculateScore(query, queryKeywords, memory)
            if (score >= config.minScore) {
                val matchType = determineMatchType(query, queryKeywords, memory)
                results.add(RetrievalResult(memory, score, matchType))
            }
        }

        return results
            .sortedByDescending { it.score }
            .take(config.maxResults)
    }

    fun retrieveTop(query: String, count: Int = 3): List<Memory> {
        return retrieve(query).take(count).map { it.memory }
    }

    fun searchByKeyword(keyword: String): List<Memory> {
        val lower = keyword.lowercase()
        return memories.filter { memory ->
            memory.keywords.any { it.lowercase().contains(lower) } ||
                    memory.content.lowercase().contains(lower)
        }
    }

    fun searchByCategory(category: String): List<Memory> {
        return memories.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getRecent(count: Int = 5): List<Memory> {
        return memories.sortedByDescending { it.timestamp }.take(count)
    }

    fun getImportant(count: Int = 5): List<Memory> {
        return memories.sortedByDescending { it.importance }.take(count)
    }

    fun getStats(): MemoryStats {
        return MemoryStats(
            totalMemories = memories.size,
            categories = memories.groupBy { it.category }.mapValues { it.value.size },
            averageImportance = if (memories.isEmpty()) 0f else memories.map { it.importance }.average().toFloat(),
            oldestTimestamp = memories.minOfOrNull { it.timestamp } ?: 0L,
            newestTimestamp = memories.maxOfOrNull { it.timestamp } ?: 0L
        )
    }

    private fun calculateScore(query: String, queryKeywords: List<String>, memory: Memory): Float {
        var score = 0f

        val contentLower = memory.content.lowercase()
        val queryLower = query.lowercase()

        if (contentLower == queryLower) {
            score += 1f
        } else if (contentLower.contains(queryLower)) {
            score += 0.8f
        }

        val keywordMatches = queryKeywords.count { qk ->
            memory.keywords.any { mk -> mk.lowercase().contains(qk) || qk.contains(mk.lowercase()) }
        }
        if (queryKeywords.isNotEmpty()) {
            score += (keywordMatches.toFloat() / queryKeywords.size) * config.keywordWeight
        }

        val fuzzyScore = fuzzyMatch(queryLower, contentLower)
        score += fuzzyScore * config.fuzzyWeight

        score += memory.importance * config.importanceWeight

        val recencyMs = System.currentTimeMillis() - memory.timestamp
        val recencyDays = recencyMs / (1000f * 60f * 60f * 24f)
        val recencyBoost = (config.recencyBoost / (1f + recencyDays)).coerceAtMost(config.recencyBoost)
        score += recencyBoost

        return score.coerceIn(0f, 1f)
    }

    private fun fuzzyMatch(query: String, content: String): Float {
        if (query.isEmpty() || content.isEmpty()) return 0f
        val queryWords = query.split(Regex("\\s+"))
        val contentWords = content.split(Regex("\\s+"))
        if (queryWords.isEmpty() || contentWords.isEmpty()) return 0f

        var matches = 0
        for (qw in queryWords) {
            for (cw in contentWords) {
                if (qw == cw) {
                    matches++
                    break
                } else if (qw.length > 3 && cw.length > 3) {
                    val similarity = levenshteinSimilarity(qw, cw)
                    if (similarity > 0.8f) {
                        matches++
                        break
                    }
                }
            }
        }
        return matches.toFloat() / queryWords.size
    }

    private fun levenshteinSimilarity(s1: String, s2: String): Float {
        val maxLen = maxOf(s1.length, s2.length)
        if (maxLen == 0) return 1f
        val distance = levenshteinDistance(s1, s2)
        return 1f - (distance.toFloat() / maxLen)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }

    private fun extractKeywords(text: String): List<String> {
        val stopWords = setOf(
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "may", "might", "shall", "can", "need", "dare", "ought",
            "to", "of", "in", "for", "on", "with", "at", "by", "from", "as",
            "into", "through", "during", "before", "after", "above", "below",
            "between", "out", "off", "over", "under", "again", "further", "then",
            "once", "here", "there", "when", "where", "why", "how", "all", "both",
            "each", "few", "more", "most", "other", "some", "such", "no", "nor",
            "not", "only", "own", "same", "so", "than", "too", "very", "just",
            "because", "but", "and", "or", "if", "while", "about", "up", "it",
            "its", "i", "me", "my", "we", "our", "you", "your", "he", "him",
            "his", "she", "her", "they", "them", "their", "this", "that", "these",
            "those", "what", "which", "who", "whom", "am", "s", "t", "don",
            "doesn't", "didn't", "won't", "wouldn't", "shouldn't", "couldn't",
            "isn't", "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't",
            "ni", "na", "ya", "wa", "la", "za", "kwa", "mwenye", "wenye"
        )
        return text.lowercase()
            .split(Regex("[^\\p{L}]+"))
            .filter { it.length > 2 }
            .filter { it !in stopWords }
            .distinct()
    }

    private fun determineMatchType(query: String, queryKeywords: List<String>, memory: Memory): MatchType {
        val queryLower = query.lowercase()
        val contentLower = memory.content.lowercase()
        if (contentLower == queryLower) return MatchType.EXACT
        if (queryKeywords.any { qk -> memory.keywords.any { it.lowercase().contains(qk) } }) return MatchType.KEYWORD
        if (fuzzyMatch(queryLower, contentLower) > 0.5f) return MatchType.FUZZY
        return MatchType.RECENCY
    }
}

data class MemoryStats(
    val totalMemories: Int,
    val categories: Map<String, Int>,
    val averageImportance: Float,
    val oldestTimestamp: Long,
    val newestTimestamp: Long
)
