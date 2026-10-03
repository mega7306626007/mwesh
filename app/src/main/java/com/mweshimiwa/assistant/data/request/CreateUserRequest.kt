package com.mweshimiwa.assistant.data.request

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String? = null,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null,
    val metadata: Map<String, Any> = emptyMap()
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && email.isNotBlank() && email.contains("@")
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "email" to email,
            "password" to (password ?: ""),
            "avatarUrl" to (avatarUrl ?: ""),
            "phoneNumber" to (phoneNumber ?: ""),
            "metadata" to metadata
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): CreateUserRequest {
            return CreateUserRequest(
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                password = map["password"] as? String,
                avatarUrl = map["avatarUrl"] as? String,
                phoneNumber = map["phoneNumber"] as? String,
                metadata = map["metadata"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun create(
            name: String,
            email: String,
            password: String? = null
        ): CreateUserRequest {
            return CreateUserRequest(
                name = name,
                email = email,
                password = password
            )
        }
    }
}

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null,
    val metadata: Map<String, Any> = emptyMap()
) {
    fun hasUpdates(): Boolean {
        return name != null || email != null || avatarUrl != null || phoneNumber != null || metadata.isNotEmpty()
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        name?.let { map["name"] = it }
        email?.let { map["email"] = it }
        avatarUrl?.let { map["avatarUrl"] = it }
        phoneNumber?.let { map["phoneNumber"] = it }
        if (metadata.isNotEmpty()) map["metadata"] = metadata
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UpdateUserRequest {
            return UpdateUserRequest(
                name = map["name"] as? String,
                email = map["email"] as? String,
                avatarUrl = map["avatarUrl"] as? String,
                phoneNumber = map["phoneNumber"] as? String,
                metadata = map["metadata"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun updateName(name: String): UpdateUserRequest {
            return UpdateUserRequest(name = name)
        }

        fun updateEmail(email: String): UpdateUserRequest {
            return UpdateUserRequest(email = email)
        }

        fun updateAvatar(avatarUrl: String): UpdateUserRequest {
            return UpdateUserRequest(avatarUrl = avatarUrl)
        }

        fun updatePhoneNumber(phoneNumber: String): UpdateUserRequest {
            return UpdateUserRequest(phoneNumber = phoneNumber)
        }
    }
}

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String? = null,
    val deviceName: String? = null,
    val fcmToken: String? = null
) {
    fun isValid(): Boolean {
        return email.isNotBlank() && password.isNotBlank() && email.contains("@")
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "email" to email,
            "password" to password,
            "deviceId" to (deviceId ?: ""),
            "deviceName" to (deviceName ?: ""),
            "fcmToken" to (fcmToken ?: "")
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): LoginRequest {
            return LoginRequest(
                email = map["email"] as? String ?: "",
                password = map["password"] as? String ?: "",
                deviceId = map["deviceId"] as? String,
                deviceName = map["deviceName"] as? String,
                fcmToken = map["fcmToken"] as? String
            )
        }

        fun create(email: String, password: String): LoginRequest {
            return LoginRequest(email = email, password = password)
        }
    }
}

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val deviceId: String? = null,
    val deviceName: String? = null,
    val fcmToken: String? = null
) {
    fun isValid(): Boolean {
        return email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && email.contains("@")
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "email" to email,
            "password" to password,
            "name" to name,
            "deviceId" to (deviceId ?: ""),
            "deviceName" to (deviceName ?: ""),
            "fcmToken" to (fcmToken ?: "")
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): RegisterRequest {
            return RegisterRequest(
                email = map["email"] as? String ?: "",
                password = map["password"] as? String ?: "",
                name = map["name"] as? String ?: "",
                deviceId = map["deviceId"] as? String,
                deviceName = map["deviceName"] as? String,
                fcmToken = map["fcmToken"] as? String
            )
        }

        fun create(email: String, password: String, name: String): RegisterRequest {
            return RegisterRequest(email = email, password = password, name = name)
        }
    }
}

data class RefreshTokenRequest(
    val refreshToken: String
) {
    fun isValid(): Boolean {
        return refreshToken.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf("refreshToken" to refreshToken)
    }

    companion object {
        fun fromMap(map: Map<String, Any>): RefreshTokenRequest {
            return RefreshTokenRequest(
                refreshToken = map["refreshToken"] as? String ?: ""
            )
        }

        fun create(refreshToken: String): RefreshTokenRequest {
            return RefreshTokenRequest(refreshToken = refreshToken)
        }
    }
}

data class UpdateSettingsRequest(
    val theme: String? = null,
    val language: String? = null,
    val notificationsEnabled: Boolean? = null,
    val darkModeEnabled: Boolean? = null,
    val autoSyncEnabled: Boolean? = null,
    val analyticsEnabled: Boolean? = null,
    val biometricEnabled: Boolean? = null,
    val fontSize: Int? = null,
    val accentColor: String? = null
) {
    fun hasUpdates(): Boolean {
        return theme != null || language != null || notificationsEnabled != null ||
                darkModeEnabled != null || autoSyncEnabled != null || analyticsEnabled != null ||
                biometricEnabled != null || fontSize != null || accentColor != null
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        theme?.let { map["theme"] = it }
        language?.let { map["language"] = it }
        notificationsEnabled?.let { map["notificationsEnabled"] = it }
        darkModeEnabled?.let { map["darkModeEnabled"] = it }
        autoSyncEnabled?.let { map["autoSyncEnabled"] = it }
        analyticsEnabled?.let { map["analyticsEnabled"] = it }
        biometricEnabled?.let { map["biometricEnabled"] = it }
        fontSize?.let { map["fontSize"] = it }
        accentColor?.let { map["accentColor"] = it }
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UpdateSettingsRequest {
            return UpdateSettingsRequest(
                theme = map["theme"] as? String,
                language = map["language"] as? String,
                notificationsEnabled = map["notificationsEnabled"] as? Boolean,
                darkModeEnabled = map["darkModeEnabled"] as? Boolean,
                autoSyncEnabled = map["autoSyncEnabled"] as? Boolean,
                analyticsEnabled = map["analyticsEnabled"] as? Boolean,
                biometricEnabled = map["biometricEnabled"] as? Boolean,
                fontSize = (map["fontSize"] as? Number)?.toInt(),
                accentColor = map["accentColor"] as? String
            )
        }

        fun updateTheme(theme: String): UpdateSettingsRequest {
            return UpdateSettingsRequest(theme = theme)
        }

        fun updateLanguage(language: String): UpdateSettingsRequest {
            return UpdateSettingsRequest(language = language)
        }

        fun updateFontSize(fontSize: Int): UpdateSettingsRequest {
            return UpdateSettingsRequest(fontSize = fontSize)
        }

        fun updateAccentColor(accentColor: String): UpdateSettingsRequest {
            return UpdateSettingsRequest(accentColor = accentColor)
        }
    }
}

data class RegisterDeviceRequest(
    val name: String,
    val model: String,
    val manufacturer: String,
    val osVersion: String,
    val appVersion: String,
    val fcmToken: String? = null,
    val deviceId: String? = null
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && model.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "model" to model,
            "manufacturer" to manufacturer,
            "osVersion" to osVersion,
            "appVersion" to appVersion,
            "fcmToken" to (fcmToken ?: ""),
            "deviceId" to (deviceId ?: "")
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): RegisterDeviceRequest {
            return RegisterDeviceRequest(
                name = map["name"] as? String ?: "",
                model = map["model"] as? String ?: "",
                manufacturer = map["manufacturer"] as? String ?: "",
                osVersion = map["osVersion"] as? String ?: "",
                appVersion = map["appVersion"] as? String ?: "",
                fcmToken = map["fcmToken"] as? String,
                deviceId = map["deviceId"] as? String
            )
        }

        fun create(
            name: String,
            model: String,
            manufacturer: String,
            osVersion: String,
            appVersion: String
        ): RegisterDeviceRequest {
            return RegisterDeviceRequest(
                name = name,
                model = model,
                manufacturer = manufacturer,
                osVersion = osVersion,
                appVersion = appVersion
            )
        }
    }
}

data class UpdateDeviceRequest(
    val name: String? = null,
    val osVersion: String? = null,
    val appVersion: String? = null
) {
    fun hasUpdates(): Boolean {
        return name != null || osVersion != null || appVersion != null
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        name?.let { map["name"] = it }
        osVersion?.let { map["osVersion"] = it }
        appVersion?.let { map["appVersion"] = it }
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UpdateDeviceRequest {
            return UpdateDeviceRequest(
                name = map["name"] as? String,
                osVersion = map["osVersion"] as? String,
                appVersion = map["appVersion"] as? String
            )
        }

        fun updateName(name: String): UpdateDeviceRequest {
            return UpdateDeviceRequest(name = name)
        }

        fun updateAppVersion(appVersion: String): UpdateDeviceRequest {
            return UpdateDeviceRequest(appVersion = appVersion)
        }
    }
}

data class CreateNotificationRequest(
    val title: String,
    val body: String,
    val type: String = "GENERAL",
    val priority: String = "DEFAULT",
    val actionUrl: String? = null,
    val imageUrl: String? = null,
    val iconUrl: String? = null,
    val sound: Boolean = true,
    val vibrate: Boolean = true,
    val lights: Boolean = true,
    val badge: Int = 1,
    val tag: String? = null,
    val channelId: String = "default",
    val groupId: String? = null,
    val isGroupSummary: Boolean = false,
    val progress: Int? = null,
    val progressMax: Int? = null,
    val progressIndeterminate: Boolean = false,
    val bigText: String? = null,
    val bigPictureUrl: String? = null,
    val inboxLines: List<String> = emptyList(),
    val expiresAt: Long? = null
) {
    fun isValid(): Boolean {
        return title.isNotBlank() && body.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "body" to body,
            "type" to type,
            "priority" to priority,
            "actionUrl" to (actionUrl ?: ""),
            "imageUrl" to (imageUrl ?: ""),
            "iconUrl" to (iconUrl ?: ""),
            "sound" to sound,
            "vibrate" to vibrate,
            "lights" to lights,
            "badge" to badge,
            "tag" to (tag ?: ""),
            "channelId" to channelId,
            "groupId" to (groupId ?: ""),
            "isGroupSummary" to isGroupSummary,
            "progress" to (progress ?: 0),
            "progressMax" to (progressMax ?: 0),
            "progressIndeterminate" to progressIndeterminate,
            "bigText" to (bigText ?: ""),
            "bigPictureUrl" to (bigPictureUrl ?: ""),
            "inboxLines" to inboxLines,
            "expiresAt" to (expiresAt ?: 0)
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): CreateNotificationRequest {
            return CreateNotificationRequest(
                title = map["title"] as? String ?: "",
                body = map["body"] as? String ?: "",
                type = map["type"] as? String ?: "GENERAL",
                priority = map["priority"] as? String ?: "DEFAULT",
                actionUrl = map["actionUrl"] as? String,
                imageUrl = map["imageUrl"] as? String,
                iconUrl = map["iconUrl"] as? String,
                sound = map["sound"] as? Boolean ?: true,
                vibrate = map["vibrate"] as? Boolean ?: true,
                lights = map["lights"] as? Boolean ?: true,
                badge = (map["badge"] as? Number)?.toInt() ?: 1,
                tag = map["tag"] as? String,
                channelId = map["channelId"] as? String ?: "default",
                groupId = map["groupId"] as? String,
                isGroupSummary = map["isGroupSummary"] as? Boolean ?: false,
                progress = (map["progress"] as? Number)?.toInt(),
                progressMax = (map["progressMax"] as? Number)?.toInt(),
                progressIndeterminate = map["progressIndeterminate"] as? Boolean ?: false,
                bigText = map["bigText"] as? String,
                bigPictureUrl = map["bigPictureUrl"] as? String,
                inboxLines = (map["inboxLines"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                expiresAt = (map["expiresAt"] as? Number)?.toLong()
            )
        }

        fun create(title: String, body: String): CreateNotificationRequest {
            return CreateNotificationRequest(title = title, body = body)
        }
    }
}

data class UpdateNotificationRequest(
    val title: String? = null,
    val body: String? = null,
    val isRead: Boolean? = null,
    val actionUrl: String? = null,
    val imageUrl: String? = null,
    val iconUrl: String? = null,
    val sound: Boolean? = null,
    val vibrate: Boolean? = null,
    val lights: Boolean? = null,
    val badge: Int? = null,
    val tag: String? = null,
    val channelId: String? = null,
    val groupId: String? = null,
    val isGroupSummary: Boolean? = null,
    val progress: Int? = null,
    val progressMax: Int? = null,
    val progressIndeterminate: Boolean? = null,
    val bigText: String? = null,
    val bigPictureUrl: String? = null,
    val inboxLines: List<String>? = null,
    val expiresAt: Long? = null
) {
    fun hasUpdates(): Boolean {
        return title != null || body != null || isRead != null || actionUrl != null ||
                imageUrl != null || iconUrl != null || sound != null || vibrate != null ||
                lights != null || badge != null || tag != null || channelId != null ||
                groupId != null || isGroupSummary != null || progress != null ||
                progressMax != null || progressIndeterminate != null || bigText != null ||
                bigPictureUrl != null || inboxLines != null || expiresAt != null
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        title?.let { map["title"] = it }
        body?.let { map["body"] = it }
        isRead?.let { map["isRead"] = it }
        actionUrl?.let { map["actionUrl"] = it }
        imageUrl?.let { map["imageUrl"] = it }
        iconUrl?.let { map["iconUrl"] = it }
        sound?.let { map["sound"] = it }
        vibrate?.let { map["vibrate"] = it }
        lights?.let { map["lights"] = it }
        badge?.let { map["badge"] = it }
        tag?.let { map["tag"] = it }
        channelId?.let { map["channelId"] = it }
        groupId?.let { map["groupId"] = it }
        isGroupSummary?.let { map["isGroupSummary"] = it }
        progress?.let { map["progress"] = it }
        progressMax?.let { map["progressMax"] = it }
        progressIndeterminate?.let { map["progressIndeterminate"] = it }
        bigText?.let { map["bigText"] = it }
        bigPictureUrl?.let { map["bigPictureUrl"] = it }
        inboxLines?.let { map["inboxLines"] = it }
        expiresAt?.let { map["expiresAt"] = it }
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UpdateNotificationRequest {
            return UpdateNotificationRequest(
                title = map["title"] as? String,
                body = map["body"] as? String,
                isRead = map["isRead"] as? Boolean,
                actionUrl = map["actionUrl"] as? String,
                imageUrl = map["imageUrl"] as? String,
                iconUrl = map["iconUrl"] as? String,
                sound = map["sound"] as? Boolean,
                vibrate = map["vibrate"] as? Boolean,
                lights = map["lights"] as? Boolean,
                badge = (map["badge"] as? Number)?.toInt(),
                tag = map["tag"] as? String,
                channelId = map["channelId"] as? String,
                groupId = map["groupId"] as? String,
                isGroupSummary = map["isGroupSummary"] as? Boolean,
                progress = (map["progress"] as? Number)?.toInt(),
                progressMax = (map["progressMax"] as? Number)?.toInt(),
                progressIndeterminate = map["progressIndeterminate"] as? Boolean,
                bigText = map["bigText"] as? String,
                bigPictureUrl = map["bigPictureUrl"] as? String,
                inboxLines = (map["inboxLines"] as? List<*>)?.mapNotNull { it as? String },
                expiresAt = (map["expiresAt"] as? Number)?.toLong()
            )
        }

        fun markAsRead(): UpdateNotificationRequest {
            return UpdateNotificationRequest(isRead = true)
        }

        fun markAsUnread(): UpdateNotificationRequest {
            return UpdateNotificationRequest(isRead = false)
        }
    }
}

data class CreateConversationRequest(
    val title: String,
    val assistantId: String = "",
    val initialMessage: String? = null
) {
    fun isValid(): Boolean {
        return title.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "assistantId" to assistantId,
            "initialMessage" to (initialMessage ?: "")
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): CreateConversationRequest {
            return CreateConversationRequest(
                title = map["title"] as? String ?: "",
                assistantId = map["assistantId"] as? String ?: "",
                initialMessage = map["initialMessage"] as? String
            )
        }

        fun create(title: String, assistantId: String = ""): CreateConversationRequest {
            return CreateConversationRequest(title = title, assistantId = assistantId)
        }
    }
}

data class SendMessageRequest(
    val content: String,
    val role: String = "USER",
    val attachments: List<String> = emptyList()
) {
    fun isValid(): Boolean {
        return content.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "content" to content,
            "role" to role,
            "attachments" to attachments
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SendMessageRequest {
            return SendMessageRequest(
                content = map["content"] as? String ?: "",
                role = map["role"] as? String ?: "USER",
                attachments = (map["attachments"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }

        fun create(content: String): SendMessageRequest {
            return SendMessageRequest(content = content)
        }

        fun createWithAttachments(content: String, attachments: List<String>): SendMessageRequest {
            return SendMessageRequest(content = content, attachments = attachments)
        }
    }
}

data class ConfigureAssistantRequest(
    val name: String,
    val description: String = "",
    val capabilities: List<String> = emptyList(),
    val configuration: Map<String, Any> = emptyMap()
) {
    fun isValid(): Boolean {
        return name.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "description" to description,
            "capabilities" to capabilities,
            "configuration" to configuration
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): ConfigureAssistantRequest {
            return ConfigureAssistantRequest(
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                capabilities = (map["capabilities"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                configuration = map["configuration"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun create(name: String, description: String = ""): ConfigureAssistantRequest {
            return ConfigureAssistantRequest(name = name, description = description)
        }
    }
}

data class InteractWithAssistantRequest(
    val assistantId: String,
    val input: String,
    val conversationId: String? = null,
    val context: Map<String, Any> = emptyMap()
) {
    fun isValid(): Boolean {
        return assistantId.isNotBlank() && input.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "assistantId" to assistantId,
            "input" to input,
            "conversationId" to (conversationId ?: ""),
            "context" to context
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): InteractWithAssistantRequest {
            return InteractWithAssistantRequest(
                assistantId = map["assistantId"] as? String ?: "",
                input = map["input"] as? String ?: "",
                conversationId = map["conversationId"] as? String,
                context = map["context"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun create(assistantId: String, input: String): InteractWithAssistantRequest {
            return InteractWithAssistantRequest(assistantId = assistantId, input = input)
        }
    }
}

data class SearchRequest(
    val query: String,
    val page: Int = 1,
    val limit: Int = 20,
    val filters: List<com.mweshimiwa.assistant.data.model.SearchFilter> = emptyList(),
    val sortBy: String = "relevance",
    val sortOrder: String = "desc"
) {
    fun isValid(): Boolean {
        return query.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "query" to query,
            "page" to page,
            "limit" to limit,
            "filters" to filters.map { it.toMap() },
            "sortBy" to sortBy,
            "sortOrder" to sortOrder
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SearchRequest {
            return SearchRequest(
                query = map["query"] as? String ?: "",
                page = (map["page"] as? Number)?.toInt() ?: 1,
                limit = (map["limit"] as? Number)?.toInt() ?: 20,
                filters = (map["filters"] as? List<*>)?.mapNotNull {
                    com.mweshimiwa.assistant.data.model.SearchFilter.fromMap(it as Map<String, Any>)
                } ?: emptyList(),
                sortBy = map["sortBy"] as? String ?: "relevance",
                sortOrder = map["sortOrder"] as? String ?: "desc"
            )
        }

        fun create(query: String): SearchRequest {
            return SearchRequest(query = query)
        }

        fun createWithFilters(
            query: String,
            filters: List<com.mweshimiwa.assistant.data.model.SearchFilter>
        ): SearchRequest {
            return SearchRequest(query = query, filters = filters)
        }
    }
}

data class SyncRequest(
    val entityType: String,
    val lastSyncTime: Long? = null,
    val batchSize: Int = 100,
    val includeDeleted: Boolean = true,
    val conflictResolution: String = "server-wins"
) {
    fun isValid(): Boolean {
        return entityType.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "entityType" to entityType,
            "lastSyncTime" to (lastSyncTime ?: 0),
            "batchSize" to batchSize,
            "includeDeleted" to includeDeleted,
            "conflictResolution" to conflictResolution
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): SyncRequest {
            return SyncRequest(
                entityType = map["entityType"] as? String ?: "",
                lastSyncTime = (map["lastSyncTime"] as? Number)?.toLong(),
                batchSize = (map["batchSize"] as? Number)?.toInt() ?: 100,
                includeDeleted = map["includeDeleted"] as? Boolean ?: true,
                conflictResolution = map["conflictResolution"] as? String ?: "server-wins"
            )
        }

        fun create(entityType: String): SyncRequest {
            return SyncRequest(entityType = entityType)
        }
    }
}

data class ResolveConflictRequest(
    val itemId: String,
    val resolution: String,
    val localData: String? = null,
    val serverData: String? = null
) {
    fun isValid(): Boolean {
        return itemId.isNotBlank() && resolution.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "itemId" to itemId,
            "resolution" to resolution,
            "localData" to (localData ?: ""),
            "serverData" to (serverData ?: "")
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): ResolveConflictRequest {
            return ResolveConflictRequest(
                itemId = map["itemId"] as? String ?: "",
                resolution = map["resolution"] as? String ?: "",
                localData = map["localData"] as? String,
                serverData = map["serverData"] as? String
            )
        }

        fun resolveWithLocal(itemId: String): ResolveConflictRequest {
            return ResolveConflictRequest(itemId = itemId, resolution = "local")
        }

        fun resolveWithServer(itemId: String): ResolveConflictRequest {
            return ResolveConflictRequest(itemId = itemId, resolution = "server")
        }

        fun resolveWithMerge(itemId: String, mergedData: String): ResolveConflictRequest {
            return ResolveConflictRequest(itemId = itemId, resolution = "merge", localData = mergedData)
        }
    }
}

data class UploadFileRequest(
    val name: String,
    val path: String,
    val size: Long,
    val mimeType: String,
    val parentId: String? = null,
    val isFolder: Boolean = false,
    val isStarred: Boolean = false,
    val isShared: Boolean = false,
    val isHidden: Boolean = false,
    val isEncrypted: Boolean = false,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && path.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "path" to path,
            "size" to size,
            "mimeType" to mimeType,
            "parentId" to (parentId ?: ""),
            "isFolder" to isFolder,
            "isStarred" to isStarred,
            "isShared" to isShared,
            "isHidden" to isHidden,
            "isEncrypted" to isEncrypted,
            "tags" to tags,
            "metadata" to metadata
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UploadFileRequest {
            return UploadFileRequest(
                name = map["name"] as? String ?: "",
                path = map["path"] as? String ?: "",
                size = (map["size"] as? Number)?.toLong() ?: 0L,
                mimeType = map["mimeType"] as? String ?: "",
                parentId = map["parentId"] as? String,
                isFolder = map["isFolder"] as? Boolean ?: false,
                isStarred = map["isStarred"] as? Boolean ?: false,
                isShared = map["isShared"] as? Boolean ?: false,
                isHidden = map["isHidden"] as? Boolean ?: false,
                isEncrypted = map["isEncrypted"] as? Boolean ?: false,
                tags = (map["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                metadata = map["metadata"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun create(name: String, path: String, size: Long, mimeType: String): UploadFileRequest {
            return UploadFileRequest(name = name, path = path, size = size, mimeType = mimeType)
        }
    }
}

data class CreateFolderRequest(
    val name: String,
    val parentId: String? = null,
    val isHidden: Boolean = false,
    val isEncrypted: Boolean = false,
    val tags: List<String> = emptyList()
) {
    fun isValid(): Boolean {
        return name.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "parentId" to (parentId ?: ""),
            "isHidden" to isHidden,
            "isEncrypted" to isEncrypted,
            "tags" to tags
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): CreateFolderRequest {
            return CreateFolderRequest(
                name = map["name"] as? String ?: "",
                parentId = map["parentId"] as? String,
                isHidden = map["isHidden"] as? Boolean ?: false,
                isEncrypted = map["isEncrypted"] as? Boolean ?: false,
                tags = (map["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }

        fun create(name: String, parentId: String? = null): CreateFolderRequest {
            return CreateFolderRequest(name = name, parentId = parentId)
        }
    }
}

data class UploadMediaRequest(
    val name: String,
    val type: String,
    val url: String,
    val size: Long,
    val mimeType: String,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val isPrivate: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && url.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "url" to url,
            "size" to size,
            "mimeType" to mimeType,
            "description" to description,
            "tags" to tags,
            "isPrivate" to isPrivate,
            "metadata" to metadata
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UploadMediaRequest {
            return UploadMediaRequest(
                name = map["name"] as? String ?: "",
                type = map["type"] as? String ?: "",
                url = map["url"] as? String ?: "",
                size = (map["size"] as? Number)?.toLong() ?: 0L,
                mimeType = map["mimeType"] as? String ?: "",
                description = map["description"] as? String ?: "",
                tags = (map["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                isPrivate = map["isPrivate"] as? Boolean ?: false,
                metadata = map["metadata"] as? Map<String, Any> ?: emptyMap()
            )
        }

        fun create(name: String, type: String, url: String, size: Long, mimeType: String): UploadMediaRequest {
            return UploadMediaRequest(name = name, type = type, url = url, size = size, mimeType = mimeType)
        }
    }
}

data class DeleteMediaRequest(
    val mediaId: String,
    val permanent: Boolean = false
) {
    fun isValid(): Boolean {
        return mediaId.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "mediaId" to mediaId,
            "permanent" to permanent
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): DeleteMediaRequest {
            return DeleteMediaRequest(
                mediaId = map["mediaId"] as? String ?: "",
                permanent = map["permanent"] as? Boolean ?: false
            )
        }

        fun create(mediaId: String, permanent: Boolean = false): DeleteMediaRequest {
            return DeleteMediaRequest(mediaId = mediaId, permanent = permanent)
        }
    }
}

data class CreateWidgetRequest(
    val title: String,
    val type: String,
    val size: String = "MEDIUM",
    val description: String = "",
    val configuration: Map<String, Any> = emptyMap(),
    val data: String = "",
    val isActive: Boolean = true,
    val isVisible: Boolean = true,
    val position: Int = 0,
    val refreshInterval: Long = 300000L
) {
    fun isValid(): Boolean {
        return title.isNotBlank() && type.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "type" to type,
            "size" to size,
            "description" to description,
            "configuration" to configuration,
            "data" to data,
            "isActive" to isActive,
            "isVisible" to isVisible,
            "position" to position,
            "refreshInterval" to refreshInterval
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): CreateWidgetRequest {
            return CreateWidgetRequest(
                title = map["title"] as? String ?: "",
                type = map["type"] as? String ?: "",
                size = map["size"] as? String ?: "MEDIUM",
                description = map["description"] as? String ?: "",
                configuration = map["configuration"] as? Map<String, Any> ?: emptyMap(),
                data = map["data"] as? String ?: "",
                isActive = map["isActive"] as? Boolean ?: true,
                isVisible = map["isVisible"] as? Boolean ?: true,
                position = (map["position"] as? Number)?.toInt() ?: 0,
                refreshInterval = (map["refreshInterval"] as? Number)?.toLong() ?: 300000L
            )
        }

        fun create(title: String, type: String): CreateWidgetRequest {
            return CreateWidgetRequest(title = title, type = type)
        }
    }
}

data class UpdateWidgetRequest(
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,
    val size: String? = null,
    val configuration: Map<String, Any>? = null,
    val data: String? = null,
    val isActive: Boolean? = null,
    val isVisible: Boolean? = null,
    val position: Int? = null,
    val refreshInterval: Long? = null
) {
    fun hasUpdates(): Boolean {
        return title != null || description != null || type != null || size != null ||
                configuration != null || data != null || isActive != null || isVisible != null ||
                position != null || refreshInterval != null
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        title?.let { map["title"] = it }
        description?.let { map["description"] = it }
        type?.let { map["type"] = it }
        size?.let { map["size"] = it }
        configuration?.let { map["configuration"] = it }
        data?.let { map["data"] = it }
        isActive?.let { map["isActive"] = it }
        isVisible?.let { map["isVisible"] = it }
        position?.let { map["position"] = it }
        refreshInterval?.let { map["refreshInterval"] = it }
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UpdateWidgetRequest {
            return UpdateWidgetRequest(
                title = map["title"] as? String,
                description = map["description"] as? String,
                type = map["type"] as? String,
                size = map["size"] as? String,
                configuration = map["configuration"] as? Map<String, Any>,
                data = map["data"] as? String,
                isActive = map["isActive"] as? Boolean,
                isVisible = map["isVisible"] as? Boolean,
                position = (map["position"] as? Number)?.toInt(),
                refreshInterval = (map["refreshInterval"] as? Number)?.toLong()
            )
        }

        fun updateTitle(title: String): UpdateWidgetRequest {
            return UpdateWidgetRequest(title = title)
        }

        fun updateSize(size: String): UpdateWidgetRequest {
            return UpdateWidgetRequest(size = size)
        }

        fun updateActive(isActive: Boolean): UpdateWidgetRequest {
            return UpdateWidgetRequest(isActive = isActive)
        }
    }
}

data class TrackEventRequest(
    val eventName: String,
    val properties: Map<String, Any> = emptyMap(),
    val userId: String? = null,
    val sessionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun isValid(): Boolean {
        return eventName.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "eventName" to eventName,
            "properties" to properties,
            "userId" to (userId ?: ""),
            "sessionId" to (sessionId ?: ""),
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): TrackEventRequest {
            return TrackEventRequest(
                eventName = map["eventName"] as? String ?: "",
                properties = map["properties"] as? Map<String, Any> ?: emptyMap(),
                userId = map["userId"] as? String,
                sessionId = map["sessionId"] as? String,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(eventName: String, properties: Map<String, Any> = emptyMap()): TrackEventRequest {
            return TrackEventRequest(eventName = eventName, properties = properties)
        }
    }
}

data class TrackScreenViewRequest(
    val screenName: String,
    val screenClass: String? = null,
    val previousScreen: String? = null,
    val userId: String? = null,
    val sessionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun isValid(): Boolean {
        return screenName.isNotBlank()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "screenName" to screenName,
            "screenClass" to (screenClass ?: ""),
            "previousScreen" to (previousScreen ?: ""),
            "userId" to (userId ?: ""),
            "sessionId" to (sessionId ?: ""),
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): TrackScreenViewRequest {
            return TrackScreenViewRequest(
                screenName = map["screenName"] as? String ?: "",
                screenClass = map["screenClass"] as? String,
                previousScreen = map["previousScreen"] as? String,
                userId = map["userId"] as? String,
                sessionId = map["sessionId"] as? String,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(screenName: String): TrackScreenViewRequest {
            return TrackScreenViewRequest(screenName = screenName)
        }
    }
}
