package com.mweshimiwa.assistant.data.model

data class SearchQuery(
    val id: String,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val resultCount: Int = 0
) {
    fun isRecent(thresholdMs: Long = 86400000L): Boolean {
        return System.currentTimeMillis() - timestamp < thresholdMs
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "query" to query,
            "timestamp" to timestamp,
            "resultCount" to resultCount
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SearchQuery {
            return SearchQuery(
                id = map["id"] as? String ?: "",
                query = map["query"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                resultCount = (map["resultCount"] as? Number)?.toInt() ?: 0
            )
        }

        fun create(query: String): SearchQuery {
            return SearchQuery(
                id = generateId(),
                query = query,
                timestamp = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "query_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

data class SearchResult(
    val id: String,
    val title: String,
    val description: String = "",
    val url: String = "",
    val type: String = "",
    val score: Double = 0.0,
    val thumbnailUrl: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isHighRelevance(): Boolean {
        return score >= 0.8
    }

    fun isMediumRelevance(): Boolean {
        return score in 0.5..0.8
    }

    fun isLowRelevance(): Boolean {
        return score < 0.5
    }

    fun hasThumbnail(): Boolean {
        return !thumbnailUrl.isNullOrBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "url" to url,
            "type" to type,
            "score" to score,
            "thumbnailUrl" to (thumbnailUrl ?: ""),
            "metadata" to metadata,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SearchResult {
            return SearchResult(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                url = map["url"] as? String ?: "",
                type = map["type"] as? String ?: "",
                score = (map["score"] as? Number)?.toDouble() ?: 0.0,
                thumbnailUrl = map["thumbnailUrl"] as? String,
                metadata = map["metadata"] as? Map<String, Any> ?: emptyMap(),
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(
            title: String,
            description: String = "",
            url: String = "",
            type: String = ""
        ): SearchResult {
            return SearchResult(
                id = generateId(),
                title = title,
                description = description,
                url = url,
                type = type,
                createdAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "result_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

data class SearchFilter(
    val id: String,
    val name: String,
    val value: String,
    val type: String = "text",
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "value" to value,
            "type" to type,
            "isActive" to isActive
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SearchFilter {
            return SearchFilter(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                value = map["value"] as? String ?: "",
                type = map["type"] as? String ?: "text",
                isActive = map["isActive"] as? Boolean ?: true
            )
        }

        fun create(
            name: String,
            value: String,
            type: String = "text"
        ): SearchFilter {
            return SearchFilter(
                id = generateId(),
                name = name,
                value = value,
                type = type
            )
        }

        private fun generateId(): String {
            return "filter_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}
