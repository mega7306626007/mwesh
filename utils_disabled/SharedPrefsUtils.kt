package com.jarvis.assistant.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsUtils {

    private const val DEFAULT_PREFS_NAME = "jarvis_assistant_prefs"

    fun getPrefs(context: Context, name: String = DEFAULT_PREFS_NAME): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun putString(context: Context, key: String, value: String?, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null, name: String = DEFAULT_PREFS_NAME): String? {
        return getPrefs(context, name).getString(key, defaultValue)
    }

    fun putInt(context: Context, key: String, value: Int, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putInt(key, value).apply()
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0, name: String = DEFAULT_PREFS_NAME): Int {
        return getPrefs(context, name).getInt(key, defaultValue)
    }

    fun putLong(context: Context, key: String, value: Long, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putLong(key, value).apply()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = 0L, name: String = DEFAULT_PREFS_NAME): Long {
        return getPrefs(context, name).getLong(key, defaultValue)
    }

    fun putFloat(context: Context, key: String, value: Float, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putFloat(key, value).apply()
    }

    fun getFloat(context: Context, key: String, defaultValue: Float = 0f, name: String = DEFAULT_PREFS_NAME): Float {
        return getPrefs(context, name).getFloat(key, defaultValue)
    }

    fun putBoolean(context: Context, key: String, value: Boolean, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false, name: String = DEFAULT_PREFS_NAME): Boolean {
        return getPrefs(context, name).getBoolean(key, defaultValue)
    }

    fun putStringSet(context: Context, key: String, value: Set<String>, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putStringSet(key, value).apply()
    }

    fun getStringSet(context: Context, key, defaultValue: Set<String> = emptySet(), name: String = DEFAULT_PREFS_NAME): Set<String> {
        return getPrefs(context, name).getStringSet(key, defaultValue) ?: defaultValue
    }

    fun remove(context: Context, key: String, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().remove(key).apply()
    }

    fun clear(context: Context, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().clear().apply()
    }

    fun contains(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Boolean {
        return getPrefs(context, name).contains(key)
    }

    fun getAll(context: Context, name: String = DEFAULT_PREFS_NAME): Map<String, *> {
        return getPrefs(context, name).all
    }

    fun getAllKeys(context: Context, name: String = DEFAULT_PREFS_NAME): Set<String> {
        return getPrefs(context, name).all.keys
    }

    fun getAllValues(context: Context, name: String = DEFAULT_PREFS_NAME): Collection<Any?> {
        return getPrefs(context, name).all.values
    }

    fun getPrefsSize(context: Context, name: String = DEFAULT_PREFS_NAME): Int {
        return getPrefs(context, name).all.size
    }

    fun isEmpty(context: Context, name: String = DEFAULT_PREFS_NAME): Boolean {
        return getPrefs(context, name).all.isEmpty()
    }

    fun isNotEmpty(context: Context, name: String = DEFAULT_PREFS_NAME): Boolean {
        return !isEmpty(context, name)
    }

    fun registerListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun putStringSync(context: Context, key: String, value: String?, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putString(key, value).commit()
    }

    fun putIntSync(context: Context, key: String, value: Int, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putInt(key, value).commit()
    }

    fun putLongSync(context: Context, key: String, value: Long, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putLong(key, value).commit()
    }

    fun putFloatSync(context: Context, key: String, value: Float, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putFloat(key, value).commit()
    }

    fun putBooleanSync(context: Context, key: String, value: Boolean, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putBoolean(key, value).commit()
    }

    fun putStringSetSync(context: Context, key: String, value: Set<String>, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().putStringSet(key, value).commit()
    }

    fun removeSync(context: Context, key: String, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().remove(key).commit()
    }

    fun clearSync(context: Context, name: String = DEFAULT_PREFS_NAME) {
        getPrefs(context, name).edit().clear().commit()
    }

    fun putObject(context: Context, key: String, value: Any?, name: String = DEFAULT_PREFS_NAME) {
        when (value) {
            is String -> putString(context, key, value, name)
            is Int -> putInt(context, key, value, name)
            is Long -> putLong(context, key, value, name)
            is Float -> putFloat(context, key, value, name)
            is Boolean -> putBoolean(context, key, value, name)
            is Set<*> -> putStringSet(context, key, value.filterIsInstance<String>().toSet(), name)
            null -> remove(context, key, name)
        }
    }

    fun getObject(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Any? {
        val prefs = getPrefs(context, name)
        return prefs.all[key]
    }

    fun putObjectSync(context: Context, key: String, value: Any?, name: String = DEFAULT_PREFS_NAME) {
        when (value) {
            is String -> putStringSync(context, key, value, name)
            is Int -> putIntSync(context, key, value, name)
            is Long -> putLongSync(context, key, value, name)
            is Float -> putFloatSync(context, key, value, name)
            is Boolean -> putBooleanSync(context, key, value, name)
            is Set<*> -> putStringSetSync(context, key, value.filterIsInstance<String>().toSet(), name)
            null -> removeSync(context, key, name)
        }
    }

    fun incrementInt(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Int {
        val current = getInt(context, key, 0, name)
        val newValue = current + 1
        putInt(context, key, newValue, name)
        return newValue
    }

    fun decrementInt(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Int {
        val current = getInt(context, key, 0, name)
        val newValue = current - 1
        putInt(context, key, newValue, name)
        return newValue
    }

    fun incrementLong(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Long {
        val current = getLong(context, key, 0L, name)
        val newValue = current + 1L
        putLong(context, key, newValue, name)
        return newValue
    }

    fun decrementLong(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Long {
        val current = getLong(context, key, 0L, name)
        val newValue = current - 1L
        putLong(context, key, newValue, name)
        return newValue
    }

    fun toggleBoolean(context: Context, key: String, name: String = DEFAULT_PREFS_NAME): Boolean {
        val current = getBoolean(context, key, false, name)
        val newValue = !current
        putBoolean(context, key, newValue, name)
        return newValue
    }

    fun getStringOrDefault(context: Context, key: String, defaultValue: String, name: String = DEFAULT_PREFS_NAME): String {
        return getString(context, key, defaultValue, name) ?: defaultValue
    }

    fun getIntOrDefault(context: Context, key: String, defaultValue: Int, name: String = DEFAULT_PREFS_NAME): Int {
        return getInt(context, key, defaultValue, name)
    }

    fun getLongOrDefault(context: Context, key: String, defaultValue: Long, name: String = DEFAULT_PREFS_NAME): Long {
        return getLong(context, key, defaultValue, name)
    }

    fun getFloatOrDefault(context: Context, key: String, defaultValue: Float, name: String = DEFAULT_PREFS_NAME): Float {
        return getFloat(context, key, defaultValue, name)
    }

    fun getBooleanOrDefault(context: Context, key: String, defaultValue: Boolean, name: String = DEFAULT_PREFS_NAME): Boolean {
        return getBoolean(context, key, defaultValue, name)
    }

    fun getStringSetOrDefault(context: Context, key: String, defaultValue: Set<String>, name: String = DEFAULT_PREFS_NAME): Set<String> {
        return getStringSet(context, key, defaultValue, name)
    }

    fun putDouble(context: Context, key: String, value: Double, name: String = DEFAULT_PREFS_NAME) {
        putLong(context, key, java.lang.Double.doubleToRawLongBits(value), name)
    }

    fun getDouble(context: Context, key: String, defaultValue: Double = 0.0, name: String = DEFAULT_PREFS_NAME): Double {
        return java.lang.Double.longBitsToDouble(getLong(context, key, java.lang.Double.doubleToRawLongBits(defaultValue), name))
    }

    fun putDoubleSync(context: Context, key: String, value: Double, name: String = DEFAULT_PREFS_NAME) {
        putLongSync(context, key, java.lang.Double.doubleToRawLongBits(value), name)
    }

    fun getDoubleOrDefault(context: Context, key: String, defaultValue: Double, name: String = DEFAULT_PREFS_NAME): Double {
        return getDouble(context, key, defaultValue, name)
    }

    fun putStringList(context: Context, key: String, value: List<String>, name: String = DEFAULT_PREFS_NAME) {
        putStringSet(context, key, value.toSet(), name)
    }

    fun getStringList(context: Context, key: String, defaultValue: List<String> = emptyList(), name: String = DEFAULT_PREFS_NAME): List<String> {
        return getStringSet(context, key, defaultValue.toSet(), name).toList()
    }

    fun putIntList(context: Context, key: String, value: List<Int>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.joinToString(","), name)
    }

    fun getIntList(context: Context, key: String, defaultValue: List<Int> = emptyList(), name: String = DEFAULT_PREFS_NAME): List<Int> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun putLongList(context: Context, key: String, value: List<Long>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.joinToString(","), name)
    }

    fun getLongList(context: Context, key: String, defaultValue: List<Long> = emptyList(), name: String = DEFAULT_PREFS_NAME): List<Long> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(",").mapNotNull { it.toLongOrNull() }
    }

    fun putFloatList(context: Context, key: String, value: List<Float>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.joinToString(","), name)
    }

    fun getFloatList(context: Context, key: String, defaultValue: List<Float> = emptyList(), name: String = DEFAULT_PREFS_NAME): List<Float> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(",").mapNotNull { it.toFloatOrNull() }
    }

    fun putBooleanList(context: Context, key: String, value: List<Boolean>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.joinToString(","), name)
    }

    fun getBooleanList(context: Context, key: String, defaultValue: List<Boolean> = emptyList(), name: String = DEFAULT_PREFS_NAME): List<Boolean> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(",").map { it.toBooleanStrictOrNull() ?: false }
    }

    fun putIntMap(context: Context, key: String, value: Map<String, Int>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getIntMap(context: Context, key: String, defaultValue: Map<String, Int> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Int> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].toIntOrNull()
                if (v != null) k to v else null
            } else null
        }.toMap()
    }

    fun putStringMap(context: Context, key: String, value: Map<String, String>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getStringMap(context: Context, key: String, defaultValue: Map<String, String> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, String> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }

    fun putLongMap(context: Context, key: String, value: Map<String, Long>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getLongMap(context: Context, key: String, defaultValue: Map<String, Long> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Long> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].toLongOrNull()
                if (v != null) k to v else null
            } else null
        }.toMap()
    }

    fun putFloatMap(context: Context, key: String, value: Map<String, Float>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getFloatMap(context: Context, key: String, defaultValue: Map<String, Float> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Float> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].toFloatOrNull()
                if (v != null) k to v else null
            } else null
        }.toMap()
    }

    fun putBooleanMap(context: Context, key: String, value: Map<String, Boolean>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getBooleanMap(context: Context, key: String, defaultValue: Map<String, Boolean> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Boolean> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].toBooleanStrictOrNull()
                if (v != null) k to v else null
            } else null
        }.toMap()
    }

    fun putDoubleMap(context: Context, key: String, value: Map<String, Double>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value}" }, name)
    }

    fun getDoubleMap(context: Context, key: String, defaultValue: Map<String, Double> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Double> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].toDoubleOrNull()
                if (v != null) k to v else null
            } else null
        }.toMap()
    }

    fun putStringListMap(context: Context, key: String, value: Map<String, List<String>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getStringListMap(context: Context, key: String, defaultValue: Map<String, List<String>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<String>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",")
                k to v
            } else null
        }.toMap()
    }

    fun putIntListMap(context: Context, key: String, value: Map<String, List<Int>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getIntListMap(context: Context, key: String, defaultValue: Map<String, List<Int>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<Int>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { it.toIntOrNull() }
                k to v
            } else null
        }.toMap()
    }

    fun putLongListMap(context: Context, key: String, value: Map<String, List<Long>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getLongListMap(context: Context, key: String, defaultValue: Map<String, List<Long>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<Long>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { it.toLongOrNull() }
                k to v
            } else null
        }.toMap()
    }

    fun putFloatListMap(context: Context, key: String, value: Map<String, List<Float>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getFloatListMap(context: Context, key: String, defaultValue: Map<String, List<Float>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<Float>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { it.toFloatOrNull() }
                k to v
            } else null
        }.toMap()
    }

    fun putBooleanListMap(context: Context, key: String, value: Map<String, List<Boolean>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getBooleanListMap(context: Context, key: String, defaultValue: Map<String, List<Boolean>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<Boolean>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").map { it.toBooleanStrictOrNull() ?: false }
                k to v
            } else null
        }.toMap()
    }

    fun putDoubleListMap(context: Context, key: String, value: Map<String, List<Double>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.joinToString(",")}" }, name)
    }

    fun getDoubleListMap(context: Context, key: String, defaultValue: Map<String, List<Double>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, List<Double>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { it.toDoubleOrNull() }
                k to v
            } else null
        }.toMap()
    }

    fun putStringMapMap(context: Context, key: String, value: Map<String, Map<String, String>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getStringMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, String>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, String>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":", limit = 2)
                    if (pairParts.size == 2) pairParts[0] to pairParts[1] else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }

    fun putIntMapMap(context: Context, key: String, value: Map<String, Map<String, Int>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getIntMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, Int>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, Int>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":")
                    if (pairParts.size == 2) {
                        val mapKey = pairParts[0]
                        val mapValue = pairParts[1].toIntOrNull()
                        if (mapValue != null) mapKey to mapValue else null
                    } else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }

    fun putLongMapMap(context: Context, key: String, value: Map<String, Map<String, Long>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getLongMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, Long>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, Long>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":")
                    if (pairParts.size == 2) {
                        val mapKey = pairParts[0]
                        val mapValue = pairParts[1].toLongOrNull()
                        if (mapValue != null) mapKey to mapValue else null
                    } else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }

    fun putFloatMapMap(context: Context, key: String, value: Map<String, Map<String, Float>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getFloatMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, Float>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, Float>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":")
                    if (pairParts.size == 2) {
                        val mapKey = pairParts[0]
                        val mapValue = pairParts[1].toFloatOrNull()
                        if (mapValue != null) mapKey to mapValue else null
                    } else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }

    fun putBooleanMapMap(context: Context, key: String, value: Map<String, Map<String, Boolean>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getBooleanMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, Boolean>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, Boolean>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":")
                    if (pairParts.size == 2) {
                        val mapKey = pairParts[0]
                        val mapValue = pairParts[1].toBooleanStrictOrNull()
                        if (mapValue != null) mapKey to mapValue else null
                    } else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }

    fun putDoubleMapMap(context: Context, key: String, value: Map<String, Map<String, Double>>, name: String = DEFAULT_PREFS_NAME) {
        putString(context, key, value.entries.joinToString(";") { "${it.key}=${it.value.entries.joinToString(",") { "${it.key}:${it.value}" }}" }, name)
    }

    fun getDoubleMapMap(context: Context, key: String, defaultValue: Map<String, Map<String, Double>> = emptyMap(), name: String = DEFAULT_PREFS_NAME): Map<String, Map<String, Double>> {
        val str = getString(context, key, null, name) ?: return defaultValue
        return str.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val k = parts[0]
                val v = parts[1].split(",").mapNotNull { pair ->
                    val pairParts = pair.split(":")
                    if (pairParts.size == 2) {
                        val mapKey = pairParts[0]
                        val mapValue = pairParts[1].toDoubleOrNull()
                        if (mapValue != null) mapKey to mapValue else null
                    } else null
                }.toMap()
                k to v
            } else null
        }.toMap()
    }
}
