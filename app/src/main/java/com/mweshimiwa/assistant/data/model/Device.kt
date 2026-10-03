package com.mweshimiwa.assistant.data.model

data class Device(
    val id: String,
    val name: String,
    val model: String = "",
    val manufacturer: String = "",
    val osVersion: String = "",
    val appVersion: String = "",
    val status: DeviceStatus = DeviceStatus.OFFLINE,
    val batteryLevel: Int = 0,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val registeredAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isBatteryLow(): Boolean {
        return batteryLevel <= 20
    }

    fun isBatteryCritical(): Boolean {
        return batteryLevel <= 5
    }

    fun isCharging(): Boolean {
        return status == DeviceStatus.CHARGING
    }

    fun isRecentlyActive(thresholdMs: Long = 300000L): Boolean {
        return System.currentTimeMillis() - lastSeen < thresholdMs
    }

    fun getFormattedLastSeen(): String {
        if (lastSeen == 0L) return "Never"
        val diff = System.currentTimeMillis() - lastSeen
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> "${diff / 604800000}w ago"
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "model" to model,
            "manufacturer" to manufacturer,
            "osVersion" to osVersion,
            "appVersion" to appVersion,
            "status" to status.name,
            "batteryLevel" to batteryLevel,
            "isOnline" to isOnline,
            "lastSeen" to lastSeen,
            "registeredAt" to registeredAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Device {
            return Device(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                model = map["model"] as? String ?: "",
                manufacturer = map["manufacturer"] as? String ?: "",
                osVersion = map["osVersion"] as? String ?: "",
                appVersion = map["appVersion"] as? String ?: "",
                status = try {
                    DeviceStatus.valueOf(map["status"] as? String ?: "OFFLINE")
                } catch (e: Exception) {
                    DeviceStatus.OFFLINE
                },
                batteryLevel = (map["batteryLevel"] as? Number)?.toInt() ?: 0,
                isOnline = map["isOnline"] as? Boolean ?: false,
                lastSeen = (map["lastSeen"] as? Number)?.toLong() ?: 0L,
                registeredAt = (map["registeredAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        fun create(
            name: String,
            model: String = "",
            manufacturer: String = "",
            osVersion: String = "",
            appVersion: String = ""
        ): Device {
            return Device(
                id = generateId(),
                name = name,
                model = model,
                manufacturer = manufacturer,
                osVersion = osVersion,
                appVersion = appVersion,
                registeredAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        private fun generateId(): String {
            return "device_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
        }
    }
}

enum class DeviceStatus {
    ONLINE,
    OFFLINE,
    ACTIVE,
    IDLE,
    CHARGING,
    DISCHARGING,
    ERROR,
    UNKNOWN
}

data class DeviceInfo(
    val deviceId: String,
    val name: String,
    val model: String = "",
    val manufacturer: String = "",
    val osVersion: String = "",
    val appVersion: String = "",
    val batteryLevel: Int = 0,
    val storageTotal: Long = 0L,
    val storageUsed: Long = 0L,
    val ramTotal: Long = 0L,
    val ramUsed: Long = 0L,
    val networkType: String = "",
    val ipAddress: String = "",
    val macAddress: String = "",
    val serialNumber: String = "",
    val imei: String = "",
    val phoneNumber: String = "",
    val carrier: String = "",
    val wifiSsid: String = "",
    val bluetoothEnabled: Boolean = false,
    val gpsEnabled: Boolean = false,
    val nfcEnabled: Boolean = false,
    val usbDebuggingEnabled: Boolean = false,
    val developerModeEnabled: Boolean = false,
    val rooted: Boolean = false,
    val encrypted: Boolean = false,
    val lockScreenEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val lastBackupTime: Long = 0L,
    val installedAppsCount: Int = 0,
    val systemAppsCount: Int = 0,
    val userAppsCount: Int = 0
) {
    val storageFree: Long
        get() = storageTotal - storageUsed

    val storageUsagePercentage: Int
        get() = if (storageTotal > 0) ((storageUsed * 100) / storageTotal).toInt() else 0

    val ramFree: Long
        get() = ramTotal - ramUsed

    val ramUsagePercentage: Int
        get() = if (ramTotal > 0) ((ramUsed * 100) / ramTotal).toInt() else 0

    fun isStorageAlmostFull(): Boolean {
        return storageUsagePercentage >= 90
    }

    fun isRamAlmostFull(): Boolean {
        return ramUsagePercentage >= 90
    }

    fun isSecure(): Boolean {
        return encrypted && lockScreenEnabled && !rooted
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "deviceId" to deviceId,
            "name" to name,
            "model" to model,
            "manufacturer" to manufacturer,
            "osVersion" to osVersion,
            "appVersion" to appVersion,
            "batteryLevel" to batteryLevel,
            "storageTotal" to storageTotal,
            "storageUsed" to storageUsed,
            "ramTotal" to ramTotal,
            "ramUsed" to ramUsed,
            "networkType" to networkType,
            "ipAddress" to ipAddress,
            "macAddress" to macAddress,
            "serialNumber" to serialNumber,
            "imei" to imei,
            "phoneNumber" to phoneNumber,
            "carrier" to carrier,
            "wifiSsid" to wifiSsid,
            "bluetoothEnabled" to bluetoothEnabled,
            "gpsEnabled" to gpsEnabled,
            "nfcEnabled" to nfcEnabled,
            "usbDebuggingEnabled" to usbDebuggingEnabled,
            "developerModeEnabled" to developerModeEnabled,
            "rooted" to rooted,
            "encrypted" to encrypted,
            "lockScreenEnabled" to lockScreenEnabled,
            "biometricEnabled" to biometricEnabled,
            "lastBackupTime" to lastBackupTime,
            "installedAppsCount" to installedAppsCount,
            "systemAppsCount" to systemAppsCount,
            "userAppsCount" to userAppsCount
        )
    }
}
