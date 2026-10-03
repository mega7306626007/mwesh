package com.mweshimiwa.assistant.data.model

data class Assistant(
    val id: String,
    val name: String,
    val description: String = "",
    val avatarUrl: String? = null,
    val isActive: Boolean = true,
    val capabilities: List<AssistantCapability> = emptyList(),
    val configuration: AssistantConfiguration = AssistantConfiguration(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun hasCapability(capability: AssistantCapability): Boolean {
        return capabilities.contains(capability)
    }

    fun getCapabilityNames(): List<String> {
        return capabilities.map { it.name }
    }

    fun isAvailable(): Boolean {
        return isActive
    }

    fun getSummary(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "isActive" to isActive,
            "capabilities" to getCapabilityNames()
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "avatarUrl" to (avatarUrl ?: ""),
            "isActive" to isActive,
            "capabilities" to capabilities.map { it.name },
            "configuration" to configuration.toMap(),
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Assistant {
            return Assistant(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                avatarUrl = map["avatarUrl"] as? String,
                isActive = map["isActive"] as? Boolean ?: true,
                capabilities = (map["capabilities"] as? List<*>)?.mapNotNull {
                    try { AssistantCapability.valueOf(it as String) } catch (e: Exception) { null }
                } ?: emptyList(),
                configuration = AssistantConfiguration.fromMap(
                    map["configuration"] as? Map<String, Any> ?: emptyMap()
                ),
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(
            name: String,
            description: String = "",
            capabilities: List<AssistantCapability> = emptyList()
        ): Assistant {
            return Assistant(
                id = generateId(),
                name = name,
                description = description,
                capabilities = capabilities,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "assistant_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

enum class AssistantCapability {
    TEXT,
    IMAGE,
    AUDIO,
    VIDEO,
    CODE,
    SEARCH,
    CALENDAR,
    EMAIL,
    MAPS,
    WEATHER,
    UNKNOWN
}

data class AssistantConfiguration(
    val model: String = "default",
    val temperature: Double = 0.7,
    val maxTokens: Int = 2048,
    val topP: Double = 1.0,
    val frequencyPenalty: Double = 0.0,
    val presencePenalty: Double = 0.0,
    val systemPrompt: String = "",
    val stopSequences: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "model" to model,
            "temperature" to temperature,
            "maxTokens" to maxTokens,
            "topP" to topP,
            "frequencyPenalty" to frequencyPenalty,
            "presencePenalty" to presencePenalty,
            "systemPrompt" to systemPrompt,
            "stopSequences" to stopSequences
        )
    }

    fun isValid(): Boolean {
        return model.isNotBlank() &&
                temperature in 0.0..2.0 &&
                maxTokens > 0 &&
                topP in 0.0..1.0
    }

    companion object {
        fun fromMap(map: Map<String, Any>): AssistantConfiguration {
            return AssistantConfiguration(
                model = map["model"] as? String ?: "default",
                temperature = (map["temperature"] as? Number)?.toDouble() ?: 0.7,
                maxTokens = (map["maxTokens"] as? Number)?.toInt() ?: 2048,
                topP = (map["topP"] as? Number)?.toDouble() ?: 1.0,
                frequencyPenalty = (map["frequencyPenalty"] as? Number)?.toDouble() ?: 0.0,
                presencePenalty = (map["presencePenalty"] as? Number)?.toDouble() ?: 0.0,
                systemPrompt = map["systemPrompt"] as? String ?: "",
                stopSequences = (map["stopSequences"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }

        fun default(): AssistantConfiguration {
            return AssistantConfiguration()
        }
    }
}

data class InteractionRecord(
    val id: String,
    val assistantId: String,
    val conversationId: String? = null,
    val input: String = "",
    val output: String = "",
    val tokensUsed: Int = 0,
    val processingTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "assistantId" to assistantId,
            "conversationId" to (conversationId ?: ""),
            "input" to input,
            "output" to output,
            "tokensUsed" to tokensUsed,
            "processingTime" to processingTime,
            "createdAt" to createdAt
        )
    }

    fun getProcessingTimeSeconds(): Double {
        return processingTime / 1000.0
    }

    fun isLongRunning(thresholdMs: Long = 5000L): Boolean {
        return processingTime > thresholdMs
    }
}
