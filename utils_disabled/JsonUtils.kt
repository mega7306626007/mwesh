package com.mweshimiwa.assistant.utils

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {

    fun parseObject(json: String): JSONObject? {
        return try {
            JSONObject(json)
        } catch (e: Exception) {
            null
        }
    }

    fun parseArray(json: String): JSONArray? {
        return try {
            JSONArray(json)
        } catch (e: Exception) {
            null
        }
    }

    fun toJson(map: Map<String, Any?>): String {
        return JSONObject(map).toString()
    }

    fun toJson(list: List<Any?>): String {
        return JSONArray(list).toString()
    }

    fun fromJson(json: String): Map<String, Any?>? {
        return try {
            val obj = JSONObject(json)
            jsonToMap(obj)
        } catch (e: Exception) {
            null
        }
    }

    fun fromJsonArray(json: String): List<Any?>? {
        return try {
            val arr = JSONArray(json)
            jsonToList(arr)
        } catch (e: Exception) {
            null
        }
    }

    private fun jsonToMap(obj: JSONObject): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = obj.opt(key)
        }
        return map
    }

    private fun jsonToList(arr: JSONArray): List<Any?> {
        val list = mutableListOf<Any?>()
        for (i in 0 until arr.length()) {
            list.add(arr.opt(i))
        }
        return list
    }

    fun getString(obj: JSONObject, key: String, defaultValue: String? = null): String? {
        return obj.optString(key, defaultValue)
    }

    fun getInt(obj: JSONObject, key: String, defaultValue: Int = 0): Int {
        return obj.optInt(key, defaultValue)
    }

    fun getLong(obj: JSONObject, key: String, defaultValue: Long = 0L): Long {
        return obj.optLong(key, defaultValue)
    }

    fun getDouble(obj: JSONObject, key: String, defaultValue: Double = 0.0): Double {
        return obj.optDouble(key, defaultValue)
    }

    fun getBoolean(obj: JSONObject, key: String, defaultValue: Boolean = false): Boolean {
        return obj.optBoolean(key, defaultValue)
    }

    fun getJSONObject(obj: JSONObject, key: String): JSONObject? {
        return obj.optJSONObject(key)
    }

    fun getJSONArray(obj: JSONObject, key: String): JSONArray? {
        return obj.optJSONArray(key)
    }

    fun hasKey(obj: JSONObject, key: String): Boolean {
        return obj.has(key)
    }

    fun isNull(obj: JSONObject, key: String): Boolean {
        return obj.isNull(key)
    }

    fun removeKey(obj: JSONObject, key: String): JSONObject {
        obj.remove(key)
        return obj
    }

    fun putValue(obj: JSONObject, key: String, value: Any?): JSONObject {
        obj.put(key, value)
        return obj
    }

    fun mergeObjects(base: JSONObject, overlay: JSONObject): JSONObject {
        val result = JSONObject(base.toString())
        val keys = overlay.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result.put(key, overlay.opt(key))
        }
        return result
    }

    fun mergeObjectsDeep(base: JSONObject, overlay: JSONObject): JSONObject {
        val result = JSONObject(base.toString())
        val keys = overlay.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val overlayValue = overlay.opt(key)
            val baseValue = result.opt(key)
            if (baseValue is JSONObject && overlayValue is JSONObject) {
                result.put(key, mergeObjectsDeep(baseValue, overlayValue))
            } else {
                result.put(key, overlayValue)
            }
        }
        return result
    }

    fun flattenJson(obj: JSONObject, separator: String = ".", prefix: String = ""): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            val fullKey = if (prefix.isEmpty()) key else "$prefix$separator$key"
            when (value) {
                is JSONObject -> {
                    result.putAll(flattenJson(value, separator, fullKey))
                }
                is JSONArray -> {
                    result[fullKey] = value
                }
                else -> {
                    result[fullKey] = value
                }
            }
        }
        return result
    }

    fun unflattenJson(map: Map<String, Any?>, separator: String = "."): JSONObject {
        val result = JSONObject()
        for ((key, value) in map) {
            val parts = key.split(separator)
            var current = result
            for (i in 0 until parts.size - 1) {
                val part = parts[i]
                if (!current.has(part)) {
                    current.put(part, JSONObject())
                }
                current = current.getJSONObject(part)
            }
            current.put(parts.last(), value)
        }
        return result
    }

    fun filterJson(obj: JSONObject, predicate: (String, Any?) -> Boolean): JSONObject {
        val result = JSONObject()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            if (predicate(key, value)) {
                result.put(key, value)
            }
        }
        return result
    }

    fun transformJson(obj: JSONObject, transform: (String, Any?) -> Pair<String, Any?>): JSONObject {
        val result = JSONObject()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            val (newKey, newValue) = transform(key, value)
            result.put(newKey, newValue)
        }
        return result
    }

    fun mapJson(obj: JSONObject, transform: (Any?) -> Any?): JSONObject {
        val result = JSONObject()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            result.put(key, transform(value))
        }
        return result
    }

    fun mapJsonArray(arr: JSONArray, transform: (Any?) -> Any?): JSONArray {
        val result = JSONArray()
        for (i in 0 until arr.length()) {
            result.put(transform(arr.opt(i)))
        }
        return result
    }

    fun reduceJson(obj: JSONObject, initial: Any?, operation: (Any?, String, Any?) -> Any?): Any? {
        var accumulator = initial
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            accumulator = operation(accumulator, key, obj.opt(key))
        }
        return accumulator
    }

    fun reduceJsonArray(arr: JSONArray, initial: Any?, operation: (Any?, Int, Any?) -> Any?): Any? {
        var accumulator = initial
        for (i in 0 until arr.length()) {
            accumulator = operation(accumulator, i, arr.opt(i))
        }
        return accumulator
    }

    fun findInJson(obj: JSONObject, predicate: (String, Any?) -> Boolean): Pair<String, Any?>? {
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            if (predicate(key, value)) {
                return Pair(key, value)
            }
        }
        return null
    }

    fun findInJsonArray(arr: JSONArray, predicate: (Int, Any?) -> Boolean): Pair<Int, Any?>? {
        for (i in 0 until arr.length()) {
            if (predicate(i, arr.opt(i))) {
                return Pair(i, arr.opt(i))
            }
        }
        return null
    }

    fun findDeep(obj: Any?, predicate: (Any?) -> Boolean): Any? {
        if (predicate(obj)) return obj
        when (obj) {
            is JSONObject -> {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val result = findDeep(obj.opt(keys.next()), predicate)
                    if (result != null) return result
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    val result = findDeep(obj.opt(i), predicate)
                    if (result != null) return result
                }
            }
        }
        return null
    }

    fun findDeepKey(obj: Any?, key: String): Any? {
        when (obj) {
            is JSONObject -> {
                if (obj.has(key)) return obj.opt(key)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val result = findDeepKey(obj.opt(keys.next()), key)
                    if (result != null) return result
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    val result = findDeepKey(obj.opt(i), key)
                    if (result != null) return result
                }
            }
        }
        return null
    }

    fun findDeepAll(obj: Any?, predicate: (Any?) -> Boolean): List<Any?> {
        val results = mutableListOf<Any?>()
        when (obj) {
            is JSONObject -> {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val value = obj.opt(keys.next())
                    if (predicate(value)) results.add(value)
                    results.addAll(findDeepAll(value, predicate))
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    val value = obj.opt(i)
                    if (predicate(value)) results.add(value)
                    results.addAll(findDeepAll(value, predicate))
                }
            }
        }
        return results
    }

    fun findDeepAllKeys(obj: Any?, key: String): List<Any?> {
        val results = mutableListOf<Any?>()
        when (obj) {
            is JSONObject -> {
                if (obj.has(key)) results.add(obj.opt(key))
                val keys = obj.keys()
                while (keys.hasNext()) {
                    results.addAll(findDeepAllKeys(obj.opt(keys.next()), key))
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    results.addAll(findDeepAllKeys(obj.opt(i), key))
                }
            }
        }
        return results
    }

    fun countKeys(obj: JSONObject): Int {
        var count = 0
        val keys = obj.keys()
        while (keys.hasNext()) {
            keys.next()
            count++
        }
        return count
    }

    fun countArrayElements(arr: JSONArray): Int {
        return arr.length()
    }

    fun countDeep(obj: Any?): Int {
        var count = 0
        when (obj) {
            is JSONObject -> {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    keys.next()
                    count++
                    count += countDeep(obj.opt(keys.next()))
                }
            }
            is JSONArray -> {
                for (i in 0 until obj.length()) {
                    count++
                    count += countDeep(obj.opt(i))
                }
            }
        }
        return count
    }

    fun getDepth(obj: Any?): Int {
        return when (obj) {
            is JSONObject -> {
                var maxDepth = 0
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val depth = getDepth(obj.opt(keys.next()))
                    if (depth > maxDepth) maxDepth = depth
                }
                maxDepth + 1
            }
            is JSONArray -> {
                var maxDepth = 0
                for (i in 0 until obj.length()) {
                    val depth = getDepth(obj.opt(i))
                    if (depth > maxDepth) maxDepth = depth
                }
                maxDepth + 1
            }
            else -> 0
        }
    }

    fun getKeys(obj: JSONObject): List<String> {
        val keys = mutableListOf<String>()
        val iterator = obj.keys()
        while (iterator.hasNext()) {
            keys.add(iterator.next())
        }
        return keys
    }

    fun getValues(obj: JSONObject): List<Any?> {
        val values = mutableListOf<Any?>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            values.add(obj.opt(keys.next()))
        }
        return values
    }

    fun getEntries(obj: JSONObject): List<Pair<String, Any?>> {
        val entries = mutableListOf<Pair<String, Any?>>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            entries.add(Pair(key, obj.opt(key)))
        }
        return entries
    }

    fun invertJson(obj: JSONObject): JSONObject {
        val result = JSONObject()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            if (value is String) {
                result.put(value, key)
            }
        }
        return result
    }

    fun sortJson(obj: JSONObject): JSONObject {
        val result = JSONObject()
        val keys = getKeys(obj).sorted()
        for (key in keys) {
            result.put(key, obj.opt(key))
        }
        return result
    }

    fun sortJsonArray(arr: JSONArray, comparator: Comparator<Any?>): JSONArray {
        val list = jsonToList(arr)
        list.sortedWith(comparator)
        return JSONArray(list)
    }

    fun reverseJsonArray(arr: JSONArray): JSONArray {
        val result = JSONArray()
        for (i in arr.length() - 1 downTo 0) {
            result.put(arr.opt(i))
        }
        return result
    }

    fun sliceJsonArray(arr: JSONArray, start: Int, end: Int): JSONArray {
        val result = JSONArray()
        val safeStart = start.coerceIn(0, arr.length())
        val safeEnd = end.coerceIn(safeStart, arr.length())
        for (i in safeStart until safeEnd) {
            result.put(arr.opt(i))
        }
        return result
    }

    fun uniqueJsonArray(arr: JSONArray): JSONArray {
        val result = JSONArray()
        val seen = mutableSetOf<String>()
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            val key = value.toString()
            if (!seen.contains(key)) {
                result.put(value)
                seen.add(key)
            }
        }
        return result
    }

    fun intersectJsonArrays(arr1: JSONArray, arr2: JSONArray): JSONArray {
        val result = JSONArray()
        val set2 = mutableSetOf<String>()
        for (i in 0 until arr2.length()) {
            set2.add(arr2.opt(i).toString())
        }
        for (i in 0 until arr1.length()) {
            val value = arr1.opt(i)
            if (set2.contains(value.toString())) {
                result.put(value)
            }
        }
        return result
    }

    fun unionJsonArrays(arr1: JSONArray, arr2: JSONArray): JSONArray {
        val result = JSONArray()
        val seen = mutableSetOf<String>()
        for (i in 0 until arr1.length()) {
            val value = arr1.opt(i)
            val key = value.toString()
            if (!seen.contains(key)) {
                result.put(value)
                seen.add(key)
            }
        }
        for (i in 0 until arr2.length()) {
            val value = arr2.opt(i)
            val key = value.toString()
            if (!seen.contains(key)) {
                result.put(value)
                seen.add(key)
            }
        }
        return result
    }

    fun differenceJsonArrays(arr1: JSONArray, arr2: JSONArray): JSONArray {
        val result = JSONArray()
        val set2 = mutableSetOf<String>()
        for (i in 0 until arr2.length()) {
            set2.add(arr2.opt(i).toString())
        }
        for (i in 0 until arr1.length()) {
            val value = arr1.opt(i)
            if (!set2.contains(value.toString())) {
                result.put(value)
            }
        }
        return result
    }

    fun symmetricDifferenceJsonArrays(arr1: JSONArray, arr2: JSONArray): JSONArray {
        val result = JSONArray()
        val set1 = mutableSetOf<String>()
        val set2 = mutableSetOf<String>()
        for (i in 0 until arr1.length()) {
            set1.add(arr1.opt(i).toString())
        }
        for (i in 0 until arr2.length()) {
            set2.add(arr2.opt(i).toString())
        }
        for (i in 0 until arr1.length()) {
            val value = arr1.opt(i)
            if (!set2.contains(value.toString())) {
                result.put(value)
            }
        }
        for (i in 0 until arr2.length()) {
            val value = arr2.opt(i)
            if (!set1.contains(value.toString())) {
                result.put(value)
            }
        }
        return result
    }

    fun chunkJsonArray(arr: JSONArray, size: Int): List<JSONArray> {
        val chunks = mutableListOf<JSONArray>()
        var i = 0
        while (i < arr.length()) {
            val chunk = JSONArray()
            val end = minOf(i + size, arr.length())
            for (j in i until end) {
                chunk.put(arr.opt(j))
            }
            chunks.add(chunk)
            i = end
        }
        return chunks
    }

    fun zipJsonArrays(arr1: JSONArray, arr2: JSONArray): JSONArray {
        val result = JSONArray()
        val length = minOf(arr1.length(), arr2.length())
        for (i in 0 until length) {
            val pair = JSONArray()
            pair.put(arr1.opt(i))
            pair.put(arr2.opt(i))
            result.put(pair)
        }
        return result
    }

    fun unzipJsonArray(arr: JSONArray): Pair<JSONArray, JSONArray> {
        val arr1 = JSONArray()
        val arr2 = JSONArray()
        for (i in 0 until arr.length()) {
            val pair = arr.optJSONArray(i) ?: continue
            arr1.put(pair.opt(0))
            arr2.put(pair.opt(1))
        }
        return Pair(arr1, arr2)
    }

    fun groupByJsonArray(arr: JSONArray, keySelector: (Any?) -> String): Map<String, JSONArray> {
        val groups = mutableMapOf<String, JSONArray>()
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            val key = keySelector(value)
            if (!groups.containsKey(key)) {
                groups[key] = JSONArray()
            }
            groups[key]!!.put(value)
        }
        return groups
    }

    fun partitionJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Pair<JSONArray, JSONArray> {
        val matching = JSONArray()
        val nonMatching = JSONArray()
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            if (predicate(value)) {
                matching.put(value)
            } else {
                nonMatching.put(value)
            }
        }
        return Pair(matching, nonMatching)
    }

    fun splitJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Pair<JSONArray, JSONArray> {
        return partitionJsonArray(arr, predicate)
    }

    fun takeJsonArray(arr: JSONArray, count: Int): JSONArray {
        val result = JSONArray()
        val safeCount = count.coerceIn(0, arr.length())
        for (i in 0 until safeCount) {
            result.put(arr.opt(i))
        }
        return result
    }

    fun skipJsonArray(arr: JSONArray, count: Int): JSONArray {
        val result = JSONArray()
        val safeCount = count.coerceIn(0, arr.length())
        for (i in safeCount until arr.length()) {
            result.put(arr.opt(i))
        }
        return result
    }

    fun dropWhileJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): JSONArray {
        var dropCount = 0
        for (i in 0 until arr.length()) {
            if (predicate(arr.opt(i))) {
                dropCount++
            } else {
                break
            }
        }
        return skipJsonArray(arr, dropCount)
    }

    fun takeWhileJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): JSONArray {
        var takeCount = 0
        for (i in 0 until arr.length()) {
            if (predicate(arr.opt(i))) {
                takeCount++
            } else {
                break
            }
        }
        return takeJsonArray(arr, takeCount)
    }

    fun distinctByJsonArray(arr: JSONArray, keySelector: (Any?) -> String): JSONArray {
        val result = JSONArray()
        val seen = mutableSetOf<String>()
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            val key = keySelector(value)
            if (!seen.contains(key)) {
                result.put(value)
                seen.add(key)
            }
        }
        return result
    }

    fun orderByJsonArray(arr: JSONArray, keySelector: (Any?) -> Comparable<*>): JSONArray {
        val list = jsonToList(arr)
        val sorted = list.sortedBy { keySelector(it) }
        return JSONArray(sorted)
    }

    fun orderByDescendingJsonArray(arr: JSONArray, keySelector: (Any?) -> Comparable<*>): JSONArray {
        val list = jsonToList(arr)
        val sorted = list.sortedByDescending { keySelector(it) }
        return JSONArray(sorted)
    }

    fun thenByJsonArray(arr: JSONArray, vararg keySelectors: (Any?) -> Comparable<*>): JSONArray {
        val list = jsonToList(arr)
        val sorted = list.sortedWith(compareBy(*keySelectors))
        return JSONArray(sorted)
    }

    fun thenByDescendingJsonArray(arr: JSONArray, vararg keySelectors: (Any?) -> Comparable<*>): JSONArray {
        val list = jsonToList(arr)
        val sorted = list.sortedWith(compareByDescending(*keySelectors))
        return JSONArray(sorted)
    }

    fun minByJsonArray(arr: JSONArray, keySelector: (Any?) -> Comparable<*>): Any? {
        var min: Any? = null
        var minKey: Comparable<*>? = null
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            val key = keySelector(value)
            if (minKey == null || key < minKey) {
                min = value
                minKey = key
            }
        }
        return min
    }

    fun maxByJsonArray(arr: JSONArray, keySelector: (Any?) -> Comparable<*>): Any? {
        var max: Any? = null
        var maxKey: Comparable<*>? = null
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            val key = keySelector(value)
            if (maxKey == null || key > maxKey) {
                max = value
                maxKey = key
            }
        }
        return max
    }

    fun sumByJsonArray(arr: JSONArray, valueSelector: (Any?) -> Double): Double {
        var sum = 0.0
        for (i in 0 until arr.length()) {
            sum += valueSelector(arr.opt(i))
        }
        return sum
    }

    fun averageByJsonArray(arr: JSONArray, valueSelector: (Any?) -> Double): Double {
        if (arr.length() == 0) return 0.0
        return sumByJsonArray(arr, valueSelector) / arr.length()
    }

    fun countByJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Int {
        var count = 0
        for (i in 0 until arr.length()) {
            if (predicate(arr.opt(i))) count++
        }
        return count
    }

    fun allJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Boolean {
        for (i in 0 until arr.length()) {
            if (!predicate(arr.opt(i))) return false
        }
        return true
    }

    fun anyJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Boolean {
        for (i in 0 until arr.length()) {
            if (predicate(arr.opt(i))) return true
        }
        return false
    }

    fun noneJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Boolean {
        return !anyJsonArray(arr, predicate)
    }

    fun firstJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Any? {
        for (i in 0 until arr.length()) {
            if (predicate(arr.opt(i))) return arr.opt(i)
        }
        return null
    }

    fun firstOrNullJsonArray(arr: JSONArray): Any? {
        return if (arr.length() > 0) arr.opt(0) else null
    }

    fun lastJsonArray(arr: JSONArray, predicate: (Any?) -> Boolean): Any? {
        for (i in arr.length() - 1 downTo 0) {
            if (predicate(arr.opt(i))) return arr.opt(i)
        }
        return null
    }

    fun lastOrNullJsonArray(arr: JSONArray): Any? {
        return if (arr.length() > 0) arr.opt(arr.length() - 1) else null
    }

    fun elementAtJsonArray(arr: JSONArray, index: Int): Any? {
        return if (index in 0 until arr.length()) arr.opt(index) else null
    }

    fun elementAtOrNullJsonArray(arr: JSONArray, index: Int): Any? {
        return elementAtJsonArray(arr, index)
    }

    fun indexOfJsonArray(arr: JSONArray, value: Any?): Int {
        for (i in 0 until arr.length()) {
            if (arr.opt(i) == value) return i
        }
        return -1
    }

    fun lastIndexOfJsonArray(arr: JSONArray, value: Any?): Int {
        for (i in arr.length() - 1 downTo 0) {
            if (arr.opt(i) == value) return i
        }
        return -1
    }

    fun containsJsonArray(arr: JSONArray, value: Any?): Boolean {
        return indexOfJsonArray(arr, value) >= 0
    }

    fun isEmptyJsonArray(arr: JSONArray): Boolean {
        return arr.length() == 0
    }

    fun isNotEmptyJsonArray(arr: JSONArray): Boolean {
        return arr.length() > 0
    }

    fun isNullOrEmptyJsonArray(arr: JSONArray?): Boolean {
        return arr == null || arr.length() == 0
    }

    fun isNotNullOrEmptyJsonArray(arr: JSONArray?): Boolean {
        return arr != null && arr.length() > 0
    }

    fun orEmptyJsonArray(arr: JSONArray?): JSONArray {
        return arr ?: JSONArray()
    }

    fun ifEmptyJsonArray(arr: JSONArray, default: JSONArray): JSONArray {
        return if (arr.length() == 0) default else arr
    }

    fun ifBlankJsonArray(arr: JSONArray, default: JSONArray): JSONArray {
        return ifEmptyJsonArray(arr, default)
    }

    fun coalesceJsonArray(vararg arrays: JSONArray?): JSONArray? {
        for (arr in arrays) {
            if (arr != null && arr.length() > 0) return arr
        }
        return null
    }

    fun defaultIfEmptyJsonArray(arr: JSONArray, default: JSONArray): JSONArray {
        return ifEmptyJsonArray(arr, default)
    }

    fun defaultIfBlankJsonArray(arr: JSONArray, default: JSONArray): JSONArray {
        return ifBlankJsonArray(arr, default)
    }

    fun takeUnlessJsonArray(arr: JSONArray, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (predicate(arr)) null else arr
    }

    fun takeIfJsonArray(arr: JSONArray, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (predicate(arr)) arr else null
    }

    fun alsoJsonArray(arr: JSONArray, block: (JSONArray) -> Unit): JSONArray {
        block(arr)
        return arr
    }

    fun applyJsonArray(arr: JSONArray, block: (JSONArray) -> Unit): JSONArray {
        block(arr)
        return arr
    }

    fun letJsonArray(arr: JSONArray, block: (JSONArray) -> Unit) {
        block(arr)
    }

    fun runJsonArray(arr: JSONArray, block: (JSONArray) -> Unit) {
        block(arr)
    }

    fun withJsonArray(arr: JSONArray, block: (JSONArray) -> Unit): JSONArray {
        block(arr)
        return arr
    }

    fun useJsonArray(arr: JSONArray, block: (JSONArray) -> Unit) {
        block(arr)
    }

    fun useJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull2(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull2(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull2(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull3(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull3(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull3(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull4(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull4(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull4(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull5(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull5(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull5(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull6(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull6(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull6(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull7(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull7(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull7(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull8(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull8(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull8(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull9(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull9(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull9(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull10(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull10(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull10(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull11(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull11(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull11(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull12(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull12(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull12(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull13(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull13(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull13(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull14(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull14(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull14(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull15(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull15(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull15(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull16(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull16(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull16(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull17(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull17(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull17(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull18(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull18(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull18(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull19(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull19(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull19(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull20(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull20(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull20(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull21(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull21(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull21(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull22(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull22(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull22(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull23(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull23(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull23(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull24(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull24(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull24(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull25(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull25(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull25(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull26(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull26(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull26(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull27(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull27(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull27(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull28(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull28(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull28(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull29(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull29(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull29(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull30(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull30(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull30(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull31(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull31(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull31(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull32(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull32(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull32(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull33(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull33(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull33(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull34(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull34(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull34(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull35(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull35(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull35(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull36(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull36(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull36(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull37(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull37(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull37(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull38(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull38(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull38(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull39(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull39(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull39(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull40(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull40(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull40(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull41(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull41(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull41(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull42(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull42(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull42(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull43(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull43(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull43(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull44(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull44(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull44(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull45(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull45(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull45(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull46(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull46(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull46(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull47(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull47(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull47(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull48(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull48(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull48(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull49(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull49(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull49(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull50(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull50(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull50(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull51(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull51(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull51(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull52(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull52(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull52(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull53(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull53(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull53(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull54(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull54(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull54(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull55(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull55(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull55(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull56(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull56(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull56(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull57(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull57(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull57(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull58(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull58(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull58(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull59(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull59(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull59(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull60(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull60(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull60(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull61(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull61(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull61(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull62(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull62(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull62(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull63(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull63(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull63(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull64(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull64(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull64(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull65(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull65(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull65(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull66(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull66(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull66(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull67(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull67(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull67(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull68(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull68(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull68(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull69(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull69(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull69(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull70(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull70(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull70(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull71(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull71(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull71(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull72(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull72(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull72(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull73(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull73(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull73(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull74(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull74(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull74(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull75(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull75(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull75(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull76(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull76(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull76(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull77(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull77(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull77(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull78(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull78(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull78(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull79(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull79(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull79(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull80(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull80(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull80(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull81(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull81(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull81(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull82(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull82(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull82(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull83(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull83(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull83(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull84(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull84(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull84(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull85(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull85(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun runJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun withJsonArrayOrNull85(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun useJsonArrayOrNull86(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null) block(arr)
    }

    fun takeIfJsonArrayOrNull86(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && predicate(arr)) arr else null
    }

    fun takeUnlessJsonArrayOrNull86(arr: JSONArray?, predicate: (JSONArray) -> Boolean): JSONArray? {
        return if (arr != null && !predicate(arr)) arr else null
    }

    fun alsoJsonArrayOrNull86(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun applyJsonArrayOrNull86(arr: JSONArray?, block: (JSONArray) -> Unit): JSONArray? {
        if (arr != null) block(arr)
        return arr
    }

    fun letJsonArrayOrNull86(arr: JSONArray?, block: (JSONArray) -> Unit) {
        if (arr != null