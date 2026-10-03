package com.mweshimiwa.assistant.utils

import java.security.MessageDigest
import java.util.Locale
import java.util.regex.Pattern

object StringUtils {

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private val PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    )

    private val URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        Pattern.CASE_INSENSITIVE
    )

    fun capitalizeFirst(str: String): String {
        if (str.isEmpty()) return str
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }

    fun capitalizeWords(str: String): String {
        if (str.isEmpty()) return str
        return str.split(" ").joinToString(" ") { word ->
            if (word.isEmpty()) word
            else word.substring(0, 1).uppercase(Locale.getDefault()) + word.substring(1).lowercase(Locale.getDefault())
        }
    }

    fun reverse(str: String): String {
        return str.reversed()
    }

    fun isPalindrome(str: String): Boolean {
        val cleaned = str.lowercase(Locale.getDefault()).replace(Regex("[^a-z0-9]"), "")
        return cleaned == cleaned.reversed()
    }

    fun truncate(str: String, maxLength: Int, suffix: String = "..."): String {
        return if (str.length <= maxLength) str
        else str.substring(0, maxLength - suffix.length) + suffix
    }

    fun removeWhitespace(str: String): String {
        return str.replace("\\s".toRegex(), "")
    }

    fun removeSpecialCharacters(str: String): String {
        return str.replace("[^a-zA-Z0-9 ]".toRegex(), "")
    }

    fun countOccurrences(str: String, substring: String): Int {
        if (substring.isEmpty()) return 0
        var count = 0
        var index = 0
        while (true) {
            index = str.indexOf(substring, index)
            if (index == -1) break
            count++
            index += substring.length
        }
        return count
    }

    fun countWords(str: String): Int {
        val trimmed = str.trim()
        if (trimmed.isEmpty()) return 0
        return trimmed.split("\\s+".toRegex()).size
    }

    fun isBlank(str: String?): Boolean {
        return str == null || str.trim().isEmpty()
    }

    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }

    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    fun defaultIfBlank(str: String?, default: String): String {
        return if (isBlank(str)) default else str!!
    }

    fun defaultIfEmpty(str: String?, default: String): String {
        return if (isEmpty(str)) default else str!!
    }

    fun abbreviate(str: String, maxWidth: Int): String {
        if (str.length <= maxWidth) return str
        if (maxWidth < 4) throw IllegalArgumentException("Minimum abbreviation width is 4")
        return str.substring(0, maxWidth - 3) + "..."
    }

    fun wrap(str: String, width: Int): List<String> {
        if (str.isEmpty()) return listOf("")
        val words = str.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        for (word in words) {
            if (currentLine.length + word.length + 1 > width) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString().trim())
                    currentLine = StringBuilder()
                }
                if (word.length > width) {
                    var i = 0
                    while (i < word.length) {
                        val end = minOf(i + width, word.length)
                        lines.add(word.substring(i, end))
                        i = end
                    }
                } else {
                    currentLine.append(word).append(" ")
                }
            } else {
                currentLine.append(word).append(" ")
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString().trim())
        }
        return lines
    }

    fun toCamelCase(str: String): String {
        val words = str.split("_", "-", " ")
        if (words.isEmpty()) return str
        return words[0].lowercase(Locale.getDefault()) +
                words.drop(1).joinToString("") { capitalizeFirst(it.lowercase(Locale.getDefault())) }
    }

    fun toSnakeCase(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
            .replace(Regex("[\\s-]+"), "_")
            .lowercase(Locale.getDefault())
    }

    fun toKebabCase(str: String): String {
        return toSnakeCase(str).replace("_", "-")
    }

    fun toPascalCase(str: String): String {
        val camel = toCamelCase(str)
        return if (camel.isEmpty()) camel
        else camel.substring(0, 1).uppercase(Locale.getDefault()) + camel.substring(1)
    }

    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[^0-9+]"), "")
        return PHONE_PATTERN.matcher(cleaned).matches()
    }

    fun isValidUrl(url: String): Boolean {
        return URL_PATTERN.matcher(url).matches()
    }

    fun extractEmails(str: String): List<String> {
        val matcher = EMAIL_PATTERN.matcher(str)
        val emails = mutableListOf<String>()
        while (matcher.find()) {
            emails.add(matcher.group())
        }
        return emails
    }

    fun extractUrls(str: String): List<String> {
        val matcher = URL_PATTERN.matcher(str)
        val urls = mutableListOf<String>()
        while (matcher.find()) {
            urls.add(matcher.group())
        }
        return urls
    }

    fun extractNumbers(str: String): List<String> {
        val pattern = Pattern.compile("-?\\d+(?:\\.\\d+)?")
        val matcher = pattern.matcher(str)
        val numbers = mutableListOf<String>()
        while (matcher.find()) {
            numbers.add(matcher.group())
        }
        return numbers
    }

    fun stripHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .trim()
    }

    fun normalizeWhitespace(str: String): String {
        return str.replace(Regex("\\s+"), " ").trim()
    }

    fun padStart(str: String, length: Int, padChar: Char = ' '): String {
        return str.padStart(length, padChar)
    }

    fun padEnd(str: String, length: Int, padChar: Char = ' '): String {
        return str.padEnd(length, padChar)
    }

    fun center(str: String, length: Int, padChar: Char = ' '): String {
        if (str.length >= length) return str
        val totalPad = length - str.length
        val leftPad = totalPad / 2
        val rightPad = totalPad - leftPad
        return padChar.toString().repeat(leftPad) + str + padChar.toString().repeat(rightPad)
    }

    fun repeat(str: String, count: Int): String {
        return str.repeat(count)
    }

    fun shuffle(str: String): String {
        return str.toCharArray().toList().shuffled().joinToString("")
    }

    fun mostFrequentChar(str: String): Char? {
        if (str.isEmpty()) return null
        val frequency = str.groupingBy { it }.eachCount()
        return frequency.maxByOrNull { it.value }?.key
    }

    fun leastFrequentChar(str: String): Char? {
        if (str.isEmpty()) return null
        val frequency = str.groupingBy { it }.eachCount()
        return frequency.minByOrNull { it.value }?.key
    }

    fun characterFrequency(str: String): Map<Char, Int> {
        return str.groupingBy { it }.eachCount()
    }

    fun levenshteinDistance(s1: String, s2: String): Int {
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

    fun similarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0
        val maxLen = maxOf(s1.length, s2.length)
        val distance = levenshteinDistance(s1, s2)
        return 1.0 - (distance.toDouble() / maxLen)
    }

    fun md5(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(str.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun sha256(str: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(str.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun generateRandomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun generateRandomAlphanumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun generateRandomNumeric(length: Int): String {
        val chars = "0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun generateRandomHex(length: Int): String {
        val chars = "0123456789ABCDEF"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun toTitleCase(str: String): String {
        val smallWords = setOf("a", "an", "and", "as", "at", "but", "by", "for", "in", "nor", "of", "on", "or", "so", "the", "to", "up", "yet")
        val words = str.split(" ")
        return words.mapIndexed { index, word ->
            if (index == 0 || index == words.size - 1 || word.lowercase(Locale.getDefault()) !in smallWords) {
                capitalizeFirst(word.lowercase(Locale.getDefault()))
            } else {
                word.lowercase(Locale.getDefault())
            }
        }.joinToString(" ")
    }

    fun initials(str: String): String {
        return str.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it[0].uppercase(Locale.getDefault()) }
            .joinToString("")
    }

    fun slugify(str: String): String {
        return str.lowercase(Locale.getDefault())
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("[\\s-]+"), "-")
            .trim('-')
    }

    fun containsIgnoreCase(str: String, search: String): Boolean {
        return str.lowercase(Locale.getDefault()).contains(search.lowercase(Locale.getDefault()))
    }

    fun startsWithIgnoreCase(str: String, prefix: String): Boolean {
        return str.lowercase(Locale.getDefault()).startsWith(prefix.lowercase(Locale.getDefault()))
    }

    fun endsWithIgnoreCase(str: String, suffix: String): Boolean {
        return str.lowercase(Locale.getDefault()).endsWith(suffix.lowercase(Locale.getDefault()))
    }

    fun replaceIgnoreCase(str: String, old: String, new: String): String {
        return str.replace(Regex(Pattern.quote(old), RegexOption.IGNORE_CASE), new)
    }

    fun splitCamelCase(str: String): List<String> {
        return str.split(Regex("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))
    }

    fun isNumeric(str: String): Boolean {
        return str.matches(Regex("-?\\d+(?:\\.\\d+)?"))
    }

    fun isInteger(str: String): Boolean {
        return str.matches(Regex("-?\\d+"))
    }

    fun isAlpha(str: String): Boolean {
        return str.matches(Regex("[a-zA-Z]+"))
    }

    fun isAlphanumeric(str: String): Boolean {
        return str.matches(Regex("[a-zA-Z0-9]+"))
    }

    fun isHexColor(str: String): Boolean {
        return str.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))
    }

    fun isIPv4(str: String): Boolean {
        val parts = str.split(".")
        if (parts.size != 4) return false
        return parts.all { it.toIntOrNull()?.let { n -> n in 0..255 } == true }
    }

    fun isIPv6(str: String): Boolean {
        return str.matches(Regex("^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$"))
    }

    fun isUUID(str: String): Boolean {
        return str.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
    }

    fun isJson(str: String): Boolean {
        val trimmed = str.trim()
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"))
    }

    fun isXml(str: String): Boolean {
        val trimmed = str.trim()
        return trimmed.startsWith("<") && trimmed.endsWith(">")
    }

    fun isBase64(str: String): Boolean {
        return str.matches(Regex("^[A-Za-z0-9+/]*={0,2}$")) && str.length % 4 == 0
    }

    fun wordWrap(text: String, lineWidth: Int): String {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        for (word in words) {
            if (currentLine.length + word.length + 1 > lineWidth) {
                lines.add(currentLine.toString().trimEnd())
                currentLine = StringBuilder()
            }
            currentLine.append("$word ")
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString().trimEnd())
        }
        return lines.joinToString("\n")
    }

    fun diff(s1: String, s2: String): List<String> {
        val result = mutableListOf<String>()
        val maxLen = maxOf(s1.length, s2.length)
        for (i in 0 until maxLen) {
            val c1 = s1.getOrNull(i)
            val c2 = s2.getOrNull(i)
            when {
                c1 == null -> result.add("+ $c2")
                c2 == null -> result.add("- $c1")
                c1 != c2 -> result.add("~ $c1 -> $c2")
            }
        }
        return result
    }

    fun longestCommonSubstring(s1: String, s2: String): String {
        if (s1.isEmpty() || s2.isEmpty()) return ""
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        var maxLen = 0
        var endPos = 0
        for (i in 1..m) {
            for (j in 1..n) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                    if (dp[i][j] > maxLen) {
                        maxLen = dp[i][j]
                        endPos = i
                    }
                }
            }
        }
        return s1.substring(endPos - maxLen, endPos)
    }

    fun hammingDistance(s1: String, s2: String): Int {
        if (s1.length != s2.length) throw IllegalArgumentException("Strings must be of equal length")
        return s1.zip(s2).count { (a, b) -> a != b }
    }

    fun soundex(str: String): String {
        if (str.isEmpty()) return ""
        val upper = str.uppercase(Locale.getDefault())
        val first = upper[0]
        val codes = mutableListOf<Char>()
        var prevCode = getSoundexCode(first)
        if (prevCode != '0') codes.add(prevCode)
        for (i in 1 until upper.length) {
            val code = getSoundexCode(upper[i])
            if (code != '0' && code != prevCode) {
                codes.add(code)
            }
            if (upper[i] !in "AEIOUY") {
                prevCode = code
            }
        }
        while (codes.size < 4) codes.add('0')
        return "$first${codes.take(3).joinToString("")}"
    }

    private fun getSoundexCode(c: Char): Char {
        return when (c) {
            'B', 'F', 'P', 'V' -> '1'
            'C', 'G', 'J', 'K', 'Q', 'S', 'X', 'Z' -> '2'
            'D', 'T' -> '3'
            'L' -> '4'
            'M', 'N' -> '5'
            'R' -> '6'
            else -> '0'
        }
    }

    fun metaphone(str: String): String {
        if (str.isEmpty()) return ""
        val upper = str.uppercase(Locale.getDefault())
        val result = StringBuilder()
        var i = 0
        while (i < upper.length) {
            when {
                upper.startsWith("GN", i) || upper.startsWith("KN", i) ||
                        upper.startsWith("PN", i) || upper.startsWith("WR", i) -> {
                    if (i == 0) result.append(upper[i + 1])
                    i += 2
                }
                upper.startsWith("WH", i) -> {
                    result.append("W")
                    i += 2
                }
                upper[i] == 'X' -> {
                    if (i == 0) result.append("S") else result.append("KS")
                    i++
                }
                upper[i] == 'C' -> {
                    if (i + 1 < upper.length && upper[i + 1] == 'E' ||
                        i + 1 < upper.length && upper[i + 1] == 'I' ||
                        i + 1 < upper.length && upper[i + 1] == 'Y') {
                        result.append("S")
                    } else if (i + 1 < upper.length && upper[i + 1] == 'H') {
                        result.append("X")
                        i++
                    } else {
                        result.append("K")
                    }
                    i++
                }
                upper[i] == 'G' -> {
                    if (i + 1 < upper.length && upper[i + 1] == 'H') {
                        i += 2
                    } else if (i + 1 < upper.length && upper[i + 1] == 'N') {
                        i += 2
                    } else {
                        result.append("K")
                        i++
                    }
                }
                upper[i] == 'P' -> {
                    if (i + 1 < upper.length && upper[i + 1] == 'H') {
                        result.append("F")
                        i++
                    } else {
                        result.append("P")
                        i++
                    }
                }
                upper[i] == 'T' -> {
                    if (i + 1 < upper.length && upper[i + 1] == 'H') {
                        result.append("0")
                        i++
                    } else if (i + 1 < upper.length && upper[i + 1] == 'C' &&
                        i + 2 < upper.length && upper[i + 2] == 'H') {
                        result.append("X")
                        i += 2
                    } else {
                        result.append("T")
                        i++
                    }
                }
                upper[i] == 'A' || upper[i] == 'E' || upper[i] == 'I' ||
                        upper[i] == 'O' || upper[i] == 'U' -> {
                    if (i == 0) result.append(upper[i])
                    i++
                }
                else -> {
                    result.append(upper[i])
                    i++
                }
            }
        }
        return result.toString()
    }

    fun ngramSimilarity(s1: String, s2: String, n: Int = 2): Double {
        if (s1.length < n || s2.length < n) return if (s1 == s2) 1.0 else 0.0
        val ngrams1 = (0..s1.length - n).map { s1.substring(it, it + n) }.toSet()
        val ngrams2 = (0..s2.length - n).map { s2.substring(it, it + n) }.toSet()
        val intersection = ngrams1.intersect(ngrams2).size
        val union = ngrams1.union(ngrams2).size
        return if (union == 0) 0.0 else intersection.toDouble() / union
    }

    fun jaroWinklerSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0
        val matchDistance = maxOf(s1.length, s2.length) / 2 - 1
        val s1Matches = BooleanArray(s1.length)
        val s2Matches = BooleanArray(s2.length)
        var matches = 0
        var transpositions = 0
        for (i in s1.indices) {
            val start = maxOf(0, i - matchDistance)
            val end = minOf(i + matchDistance + 1, s2.length)
            for (j in start until end) {
                if (s2Matches[j] || s1[i] != s2[j]) continue
                s1Matches[i] = true
                s2Matches[j] = true
                matches++
                break
            }
        }
        if (matches == 0) return 0.0
        var k = 0
        for (i in s1.indices) {
            if (!s1Matches[i]) continue
            while (!s2Matches[k]) k++
            if (s1[i] != s2[k]) transpositions++
            k++
        }
        val jaro = (matches.toDouble() / s1.length +
                matches.toDouble() / s2.length +
                (matches - transpositions / 2.0) / matches) / 3.0
        var prefix = 0
        for (i in 0 until minOf(4, minOf(s1.length, s2.length))) {
            if (s1[i] == s2[i]) prefix++ else break
        }
        return jaro + prefix * 0.1 * (1 - jaro)
    }

    fun tokenize(str: String): List<String> {
        return str.split(Regex("[\\s,;.!?]+")).filter { it.isNotEmpty() }
    }

    fun sentencize(str: String): List<String> {
        return str.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotEmpty() }
    }

    fun paragraphize(str: String): List<String> {
        return str.split(Regex("\\n\\s*\\n")).filter { it.isNotEmpty() }
    }

    fun readingTime(text: String, wordsPerMinute: Int = 200): Int {
        val wordCount = countWords(text)
        return (wordCount + wordsPerMinute - 1) / wordsPerMinute
    }

    fun characterCount(str: String): Int = str.length

    fun characterCountWithoutSpaces(str: String): Int = str.count { !it.isWhitespace() }

    fun lineCount(str: String): Int = str.lines().size

    fun byteLength(str: String): Int = str.toByteArray(Charsets.UTF_8).size

    fun toByteArray(str: String): ByteArray = str.toByteArray(Charsets.UTF_8)

    fun fromByteArray(bytes: ByteArray): String = String(bytes, Charsets.UTF_8)

    fun toHex(str: String): String {
        return str.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }
    }

    fun fromHex(hex: String): String {
        return hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray().let { String(it, Charsets.UTF_8) }
    }

    fun toBinary(str: String): String {
        return str.toByteArray(Charsets.UTF_8).joinToString(" ") { "%8s".format(Integer.toBinaryString(it.toInt() and 0xFF)).replace(' ', '0') }
    }

    fun fromBinary(binary: String): String {
        return binary.split(" ").map { it.toInt(2).toByte() }.toByteArray().let { String(it, Charsets.UTF_8) }
    }

    fun toAsciiCodes(str: String): List<Int> = str.map { it.code }

    fun fromAsciiCodes(codes: List<Int>): String = codes.map { it.toChar() }.joinToString("")

    fun rot13(str: String): String {
        return str.map { c ->
            when {
                c in 'a'..'z' -> ((c - 'a' + 13) % 26 + 'a'.code).toChar()
                c in 'A'..'Z' -> ((c - 'A' + 13) % 26 + 'A'.code).toChar()
                else -> c
            }
        }.joinToString("")
    }

    fun caesarCipher(str: String, shift: Int): String {
        return str.map { c ->
            when {
                c in 'a'..'z' -> ((c - 'a' + shift + 26) % 26 + 'a'.code).toChar()
                c in 'A'..'Z' -> ((c - 'A' + shift + 26) % 26 + 'A'.code).toChar()
                else -> c
            }
        }.joinToString("")
    }

    fun vigenereEncrypt(str: String, key: String): String {
        if (key.isEmpty()) return str
        val upperKey = key.uppercase(Locale.getDefault())
        var keyIndex = 0
        return str.map { c ->
            when {
                c in 'a'..'z' -> {
                    val shift = upperKey[keyIndex % upperKey.length] - 'A'
                    keyIndex++
                    ((c - 'a' + shift) % 26 + 'a'.code).toChar()
                }
                c in 'A'..'Z' -> {
                    val shift = upperKey[keyIndex % upperKey.length] - 'A'
                    keyIndex++
                    ((c - 'A' + shift) % 26 + 'A'.code).toChar()
                }
                else -> c
            }
        }.joinToString("")
    }

    fun vigenereDecrypt(str: String, key: String): String {
        if (key.isEmpty()) return str
        val upperKey = key.uppercase(Locale.getDefault())
        var keyIndex = 0
        return str.map { c ->
            when {
                c in 'a'..'z' -> {
                    val shift = upperKey[keyIndex % upperKey.length] - 'A'
                    keyIndex++
                    ((c - 'a' - shift + 26) % 26 + 'a'.code).toChar()
                }
                c in 'A'..'Z' -> {
                    val shift = upperKey[keyIndex % upperKey.length] - 'A'
                    keyIndex++
                    ((c - 'A' - shift + 26) % 26 + 'A'.code).toChar()
                }
                else -> c
            }
        }.joinToString("")
    }

    fun xorEncrypt(str: String, key: String): String {
        if (key.isEmpty()) return str
        return str.mapIndexed { i, c -> (c.code xor key[i % key.length].code).toChar() }.joinToString("")
    }

    fun xorDecrypt(str: String, key: String): String = xorEncrypt(str, key)

    fun frequencyAnalysis(str: String): Map<Char, Double> {
        if (str.isEmpty()) return emptyMap()
        val counts = str.filter { it.isLetter() }.groupingBy { it.lowercaseChar() }.eachCount()
        val total = counts.values.sum().toDouble()
        return counts.mapValues { it.value / total }
    }

    fun indexOfNth(str: String, char: Char, n: Int): Int {
        var count = 0
        for (i in str.indices) {
            if (str[i] == char) {
                count++
                if (count == n) return i
            }
        }
        return -1
    }

    fun lastIndexOfNth(str: String, char: Char, n: Int): Int {
        var count = 0
        for (i in str.length - 1 downTo 0) {
            if (str[i] == char) {
                count++
                if (count == n) return i
            }
        }
        return -1
    }

    fun insertAt(str: String, index: Int, insertion: String): String {
        if (index < 0 || index > str.length) throw IndexOutOfBoundsException("Index: $index, Length: ${str.length}")
        return str.substring(0, index) + insertion + str.substring(index)
    }

    fun deleteRange(str: String, start: Int, end: Int): String {
        if (start < 0 || end > str.length || start > end) throw IndexOutOfBoundsException("Start: $start, End: $end, Length: ${str.length}")
        return str.substring(0, start) + str.substring(end)
    }

    fun replaceRange(str: String, start: Int, end: Int, replacement: String): String {
        if (start < 0 || end > str.length || start > end) throw IndexOutOfBoundsException("Start: $start, End: $end, Length: ${str.length}")
        return str.substring(0, start) + replacement + str.substring(end)
    }

    fun swapCase(str: String): String {
        return str.map { c ->
            when {
                c.isUpperCase() -> c.lowercaseChar()
                c.isLowerCase() -> c.uppercaseChar()
                else -> c
            }
        }.joinToString("")
    }

    fun isUpperCase(str: String): Boolean = str == str.uppercase(Locale.getDefault()) && str != str.lowercase(Locale.getDefault())

    fun isLowerCase(str: String): Boolean = str == str.lowercase(Locale.getDefault()) && str != str.uppercase(Locale.getDefault())

    fun toUpperCase(str: String): String = str.uppercase(Locale.getDefault())

    fun toLowerCase(str: String): String = str.lowercase(Locale.getDefault())

    fun compare(s1: String, s2: String): Int = s1.compareTo(s2)

    fun compareIgnoreCase(s1: String, s2: String): Int = s1.compareTo(s2, ignoreCase = true)

    fun join(elements: Collection<String>, separator: String = ", "): String = elements.joinToString(separator)

    fun join(vararg elements: String, separator: String = ", "): String = elements.joinToString(separator)

    fun split(str: String, delimiter: String): List<String> = str.split(delimiter)

    fun splitByMultipleDelimiters(str: String, vararg delimiters: String): List<String> {
        if (delimiters.isEmpty()) return listOf(str)
        val pattern = delimiters.joinToString("|") { Pattern.quote(it) }
        return str.split(Regex(pattern))
    }

    fun chomp(str: String): String = str.removeSuffix("\n").removeSuffix("\r")

    fun chop(str: String): String = if (str.length >= 2) str.substring(0, str.length - 1) else ""

    fun multiply(str: String, count: Int): String = str.repeat(count)

    fun partition(str: String, separator: String): Triple<String, String, String>? {
        val index = str.indexOf(separator)
        if (index == -1) return null
        return Triple(
            str.substring(0, index),
            separator,
            str.substring(index + separator.length)
        )
    }

    fun rpartition(str: String, separator: String): Triple<String, String, String>? {
        val index = str.lastIndexOf(separator)
        if (index == -1) return null
        return Triple(
            str.substring(0, index),
            separator,
            str.substring(index + separator.length)
        )
    }

    fun lines(str: String): List<String> = str.lines()

    fun unlines(lines: List<String>): String = lines.joinToString("\n")

    fun words(str: String): List<String> = str.split(Regex("\\s+")).filter { it.isNotEmpty() }

    fun unwords(words: List<String>): String = words.joinToString(" ")

    fun chars(str: String): List<Char> = str.toList()

    fun unchars(chars: List<Char>): String = chars.joinToString("")

    fun graphemes(str: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < str.length) {
            val codePoint = str.codePointAt(i)
            val charCount = Character.charCount(codePoint)
            result.add(str.substring(i, i + charCount))
            i += charCount
        }
        return result
    }

    fun codePoints(str: String): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0
        while (i < str.length) {
            val codePoint = str.codePointAt(i)
            result.add(codePoint)
            i += Character.charCount(codePoint)
        }
        return result
    }

    fun fromCodePoints(codePoints: List<Int>): String {
        return String(codePoints.toIntArray(), 0, codePoints.size)
    }

    fun isEmoji(str: String): Boolean {
        if (str.isEmpty()) return false
        val codePoint = str.codePointAt(0)
        return (codePoint in 0x1F600..0x1F64F) ||
                (codePoint in 0x1F300..0x1F5FF) ||
                (codePoint in 0x1F680..0x1F6FF) ||
                (codePoint in 0x1F1E0..0x1F1FF) ||
                (codePoint in 0x2600..0x26FF) ||
                (codePoint in 0x2700..0x27BF) ||
                (codePoint in 0xFE00..0xFE0F) ||
                (codePoint in 0x1F900..0x1F9FF) ||
                (codePoint in 0x1FA00..0x1FA6F) ||
                (codePoint in 0x1FA70..0x1FAFF)
    }

    fun extractEmojis(str: String): List<String> {
        return graphemes(str).filter { isEmoji(it) }
    }

    fun removeEmojis(str: String): String {
        return graphemes(str).filter { !isEmoji(it) }.joinToString("")
    }

    fun isRTL(str: String): Boolean {
        if (str.isEmpty()) return false
        val codePoint = str.codePointAt(0)
        return (codePoint in 0x0590..0x05FF) ||
                (codePoint in 0x0600..0x06FF) ||
                (codePoint in 0x0700..0x074F) ||
                (codePoint in 0x0780..0x07BF) ||
                (codePoint in 0x07C0..0x07FF) ||
                (codePoint in 0x0800..0x083F) ||
                (codePoint in 0xFB50..0xFDFF) ||
                (codePoint in 0xFE70..0xFEFF)
    }

    fun detectScript(str: String): String {
        if (str.isEmpty()) return "Unknown"
        val codePoint = str.codePointAt(0)
        return when {
            codePoint in 0x0041..0x005A || codePoint in 0x0061..0x007A -> "Latin"
            codePoint in 0x0370..0x03FF -> "Greek"
            codePoint in 0x0400..0x04FF -> "Cyrillic"
            codePoint in 0x0590..0x05FF -> "Hebrew"
            codePoint in 0x0600..0x06FF -> "Arabic"
            codePoint in 0x0900..0x097F -> "Devanagari"
            codePoint in 0x3040..0x309F -> "Hiragana"
            codePoint in 0x30A0..0x30FF -> "Katakana"
            codePoint in 0x4E00..0x9FFF -> "CJK"
            codePoint in 0xAC00..0xD7AF -> "Hangul"
            codePoint in 0x0E00..0x0E7F -> "Thai"
            codePoint in 0x0530..0x058F -> "Armenian"
            codePoint in 0x10A0..0x10FF -> "Georgian"
            else -> "Unknown"
        }
    }

    fun transliterate(str: String): String {
        val map = mapOf(
            'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
            'е' to "e", 'ё' to "yo", 'ж' to "zh", 'з' to "z", 'и' to "i",
            'й' to "y", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n",
            'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
            'у' to "u", 'ф' to "f", 'х' to "kh", 'ц' to "ts", 'ч' to "ch",
            'ш' to "sh", 'щ' to "shch", 'ъ' to "", 'ы' to "y", 'ь' to "",
            'э' to "e", 'ю' to "yu", 'я' to "ya"
        )
        return str.map { c -> map[c.lowercaseChar()] ?: c.toString() }.joinToString("")
    }

    fun toMorseCode(str: String): String {
        val morseMap = mapOf(
            'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
            'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
            'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
            'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
            'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
            'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---",
            '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
            '7' to "--...", '8' to "---..", '9' to "----."
        )
        return str.uppercase(Locale.getDefault()).map { c -> morseMap[c] ?: "" }.filter { it.isNotEmpty() }.joinToString(" ")
    }

    fun fromMorseCode(morse: String): String {
        val morseMap = mapOf(
            ".-" to 'A', "-..." to 'B', "-.-." to 'C', "-.." to 'D', "." to 'E',
            "..-." to 'F', "--." to 'G', "...." to 'H', ".." to 'I', ".---" to 'J',
            "-.-" to 'K', ".-.." to 'L', "--" to 'M', "-." to 'N', "---" to 'O',
            ".--." to 'P', "--.-" to 'Q', ".-." to 'R', "..." to 'S', "-" to 'T',
            "..-" to 'U', "...-" to 'V', ".--" to 'W', "-..-" to 'X', "-.--" to 'Y',
            "--.." to 'Z', "-----" to '0', ".----" to '1', "..---" to '2',
            "...--" to '3', "....-" to '4', "....." to '5', "-...." to '6',
            "--..." to '7', "---.." to '8', "----." to '9'
        )
        return morse.split("  ").map { word ->
            word.split(" ").map { code -> morseMap[code] ?: '?' }.joinToString("")
        }.joinToString(" ")
    }

    fun toPigLatin(str: String): String {
        val vowels = "aeiouAEIOU"
        return str.split(" ").map { word ->
            if (word.isEmpty()) word
            else if (word[0] in vowels) word + "way"
            else {
                val firstVowelIndex = word.indexOfFirst { it in vowels }
                if (firstVowelIndex == -1) word + "ay"
                else word.substring(firstVowelIndex) + word.substring(0, firstVowelIndex) + "ay"
            }
        }.joinToString(" ")
    }

    fun toLeetSpeak(str: String): String {
        val leetMap = mapOf(
            'a' to "4", 'b' to "8", 'e' to "3", 'g' to "6", 'i' to "1",
            'l' to "1", 'o' to "0", 's' to "5", 't' to "7", 'z' to "2",
            'A' to "4", 'B' to "8", 'E' to "3", 'G' to "6", 'I' to "1",
            'L' to "1", 'O' to "0", 'S' to "5", 'T' to "7", 'Z' to "2"
        )
        return str.map { c -> leetMap[c] ?: c.toString() }.joinToString("")
    }

    fun fromLeetSpeak(str: String): String {
        val leetMap = mapOf(
            '4' to "a", '8' to "b", '3' to "e", '6' to "g", '1' to "i",
            '0' to "o", '5' to "s", '7' to "t", '2' to "z"
        )
        return str.map { c -> leetMap[c] ?: c.toString() }.joinToString("")
    }

    fun toNATOAlphabet(str: String): String {
        val natoMap = mapOf(
            'A' to "Alpha", 'B' to "Bravo", 'C' to "Charlie", 'D' to "Delta",
            'E' to "Echo", 'F' to "Foxtrot", 'G' to "Golf", 'H' to "Hotel",
            'I' to "India", 'J' to "Juliet", 'K' to "Kilo", 'L' to "Lima",
            'M' to "Mike", 'N' to "November", 'O' to "Oscar", 'P' to "Papa",
            'Q' to "Quebec", 'R' to "Romeo", 'S' to "Sierra", 'T' to "Tango",
            'U' to "Uniform", 'V' to "Victor", 'W' to "Whiskey", 'X' to "X-ray",
            'Y' to "Yankee", 'Z' to "Zulu", '0' to "Zero", '1' to "One",
            '2' to "Two", '3' to "Three", '4' to "Four", '5' to "Five",
            '6' to "Six", '7' to "Seven", '8' to "Eight", '9' to "Nine"
        )
        return str.uppercase(Locale.getDefault()).map { c -> natoMap[c] ?: c.toString() }.joinToString(" ")
    }

    fun toRomanNumeral(number: Int): String {
        if (number <= 0 || number > 3999) return number.toString()
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val result = StringBuilder()
        var num = number
        for (i in values.indices) {
            while (num >= values[i]) {
                result.append(symbols[i])
                num -= values[i]
            }
        }
        return result.toString()
    }

    fun fromRomanNumeral(roman: String): Int {
        val map = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
        val upper = roman.uppercase(Locale.getDefault())
        var result = 0
        var prev = 0
        for (i in upper.length - 1 downTo 0) {
            val current = map[upper[i]] ?: 0
            if (current < prev) result -= current else result += current
            prev = current
        }
        return result
    }

    fun toWords(number: Long): String {
        if (number == 0L) return "zero"
        val ones = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
            "eighteen", "nineteen")
        val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
        val scales = arrayOf("", "thousand", "million", "billion", "trillion")
        val parts = mutableListOf<String>()
        var num = number
        var scaleIndex = 0
        while (num > 0) {
            val chunk = (num % 1000).toInt()
            if (chunk > 0) {
                val chunkWords = StringBuilder()
                if (chunk >= 100) {
                    chunkWords.append(ones[chunk / 100]).append(" hundred")
                    if (chunk % 100 > 0) chunkWords.append(" ")
                }
                val remainder = chunk % 100
                if (remainder > 0) {
                    if (remainder < 20) {
                        chunkWords.append(ones[remainder])
                    } else {
                        chunkWords.append(tens[remainder / 10])
                        if (remainder % 10 > 0) chunkWords.append("-").append(ones[remainder % 10])
                    }
                }
                if (scaleIndex > 0) chunkWords.append(" ").append(scales[scaleIndex])
                parts.add(0, chunkWords.toString())
            }
            num /= 1000
            scaleIndex++
        }
        return parts.joinToString(" ")
    }

    fun fromWords(words: String): Long {
        val wordMap = mapOf(
            "zero" to 0L, "one" to 1L, "two" to 2L, "three" to 3L, "four" to 4L,
            "five" to 5L, "six" to 6L, "seven" to 7L, "eight" to 8L, "nine" to 9L,
            "ten" to 10L, "eleven" to 11L, "twelve" to 12L, "thirteen" to 13L,
            "fourteen" to 14L, "fifteen" to 15L, "sixteen" to 16L, "seventeen" to 17L,
            "eighteen" to 18L, "nineteen" to 19L, "twenty" to 20L, "thirty" to 30L,
            "forty" to 40L, "fifty" to 50L, "sixty" to 60L, "seventy" to 70L,
            "eighty" to 80L, "ninety" to 90L, "hundred" to 100L, "thousand" to 1000L,
            "million" to 1000000L, "billion" to 1000000000L
        )
        val tokens = words.lowercase(Locale.getDefault()).split(Regex("[\\s-]+"))
        var result = 0L
        var current = 0L
        for (token in tokens) {
            val value = wordMap[token] ?: continue
            when {
                value == 100L -> current *= 100
                value >= 1000L -> {
                    current *= value
                    result += current
                    current = 0
                }
                else -> current += value
            }
        }
        return result + current
    }

    fun toBase36(number: Long): String = number.toString(36)

    fun fromBase36(str: String): Long = str.toLong(36)

    fun toBase62(number: Long): String {
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        if (number == 0L) return "0"
        val sb = StringBuilder()
        var num = number
        while (num > 0) {
            sb.append(chars[(num % 62).toInt()])
            num /= 62
        }
        return sb.reverse().toString()
    }

    fun fromBase62(str: String): Long {
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var result = 0L
        for (c in str) {
            result = result * 62 + chars.indexOf(c)
        }
        return result
    }

    fun toCustomBase(number: Long, base: Int, digits: String): String {
        if (number == 0L) return digits[0].toString()
        val sb = StringBuilder()
        var num = number
        while (num > 0) {
            sb.append(digits[(num % base).toInt()])
            num /= base
        }
        return sb.reverse().toString()
    }

    fun fromCustomBase(str: String, base: Int, digits: String): Long {
        var result = 0L
        for (c in str) {
            result = result * base + digits.indexOf(c)
        }
        return result
    }

    fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB", "PB")
        var value = bytes.toDouble()
        var unitIndex = -1
        do {
            value /= 1024
            unitIndex++
        } while (value >= 1024 && unitIndex < units.size - 1)
        return "%.2f %s".format(Locale.US, value, units[unitIndex])
    }

    fun parseBytes(str: String): Long {
        val upper = str.uppercase(Locale.getDefault()).trim()
        val number = upper.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: return 0L
        return when {
            upper.endsWith("KB") || upper.endsWith("K") -> (number * 1024).toLong()
            upper.endsWith("MB") || upper.endsWith("M") -> (number * 1024 * 1024).toLong()
            upper.endsWith("GB") || upper.endsWith("G") -> (number * 1024 * 1024 * 1024).toLong()
            upper.endsWith("TB") || upper.endsWith("T") -> (number * 1024 * 1024 * 1024 * 1024).toLong()
            upper.endsWith("PB") || upper.endsWith("P") -> (number * 1024 * 1024 * 1024 * 1024 * 1024).toLong()
            else -> number.toLong()
        }
    }

    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return when {
            days > 0 -> "${days}d ${hours % 24}h ${minutes % 60}m ${seconds % 60}s"
            hours > 0 -> "${hours}h ${minutes % 60}m ${seconds % 60}s"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }

    fun parseDuration(str: String): Long {
        val regex = Regex("(\\d+)([dhms])")
        var totalMillis = 0L
        for (match in regex.findAll(str)) {
            val value = match.groupValues[1].toLong()
            val unit = match.groupValues[2]
            totalMillis += when (unit) {
                "d" -> value * 24 * 60 * 60 * 1000
                "h" -> value * 60 * 60 * 1000
                "m" -> value * 60 * 1000
                "s" -> value * 1000
                else -> 0
            }
        }
        return totalMillis
    }

    fun formatFileSize(bytes: Long): String = formatBytes(bytes)

    fun formatSpeed(bytesPerSecond: Long): String = "${formatBytes(bytesPerSecond)}/s"

    fun formatPercentage(value: Double, decimals: Int = 1): String {
        return "%.${decimals}f%%".format(Locale.US, value)
    }

    fun formatCurrency(amount: Double, currencyCode: String = "USD"): String {
        return String.format(Locale.US, "%.2f %s", amount, currencyCode)
    }

    fun formatScientific(number: Double, decimals: Int = 2): String {
        return "%.${decimals}e".format(Locale.US, number)
    }

    fun formatOrdinal(number: Int): String {
        val suffixes = arrayOf("th", "st", "nd", "rd")
        val mod100 = number % 100
        val suffix = if (mod100 in 11..13) "th" else suffixes.getOrElse(number % 10) { "th" }
        return "$number$suffix"
    }

    fun formatRoman(number: Int): String = toRomanNumeral(number)

    fun formatBinary(number: Int): String = Integer.toBinaryString(number)

    fun formatOctal(number: Int): String = Integer.toOctalString(number)

    fun formatHex(number: Int): String = Integer.toHexString(number).uppercase(Locale.getDefault())

    fun formatWithCommas(number: Long): String = "%,d".format(Locale.US, number)

    fun formatWithCommas(number: Double): String = "%,.2f".format(Locale.US, number)

    fun formatLeadingZeros(number: Int, width: Int): String = "%0${width}d".format(Locale.US, number)

    fun formatFixedWidth(number: Double, width: Int, decimals: Int): String {
        return "%${width}.${decimals}f".format(Locale.US, number)
    }

    fun formatSign(number: Double): String = if (number >= 0) "+$number" else "$number"

    fun formatPlusMinus(number: Double): String = if (number >= 0) "±$number" else "∓$number"

    fun formatRange(min: Double, max: Double, separator: String = " - "): String = "$min$separator$max"

    fun formatList(items: List<String>, conjunction: String = "and"): String {
        return when (items.size) {
            0 -> ""
            1 -> items[0]
            2 -> "${items[0]} $conjunction ${items[1]}"
            else -> "${items.dropLast(1).joinToString(", ")}, $conjunction ${items.last()}"
        }
    }

    fun formatTemplate(template: String, vararg args: Pair<String, String>): String {
        var result = template
        for ((key, value) in args) {
            result = result.replace("{$key}", value)
        }
        return result
    }

    fun formatTemplate(template: String, args: Map<String, String>): String {
        var result = template
        for ((key, value) in args) {
            result = result.replace("{$key}", value)
        }
        return result
    }

    fun interpolate(str: String, resolver: (String) -> String?): String {
        val regex = Regex("\\$\\{([^}]+)}")
        return regex.replace(str) { match ->
            resolver(match.groupValues[1]) ?: match.value
        }
    }

    fun mustache(str: String, context: Map<String, Any>): String {
        val regex = Regex("\\{\\{([^}]+)}}")
        return regex.replace(str) { match ->
            val key = match.groupValues[1].trim()
            context[key]?.toString() ?: ""
        }
    }

    fun csvEscape(str: String): String {
        return if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            "\"" + str.replace("\"", "\"\"") + "\""
        } else str
    }

    fun tsvEscape(str: String): String {
        return str.replace("\t", " ").replace("\n", " ")
    }

    fun jsonEscape(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\u000C", "\\f")
    }

    fun xmlEscape(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    fun htmlEscape(str: String): String = xmlEscape(str)

    fun urlEncode(str: String): String = java.net.URLEncoder.encode(str, "UTF-8")

    fun urlDecode(str: String): String = java.net.URLDecoder.decode(str, "UTF-8")

    fun htmlUnescape(str: String): String {
        return str.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
    }

    fun stripTags(str: String): String = str.replace(Regex("<[^>]*>"), "")

    fun extractTagContent(str: String, tag: String): List<String> {
        val regex = Regex("<$tag[^>]*>(.*?)</$tag>", RegexOption.DOT_MATCHES_ALL)
        return regex.findAll(str).map { it.groupValues[1] }.toList()
    }

    fun extractTagAttribute(str: String, tag: String, attribute: String): List<String> {
        val regex = Regex("<$tag[^>]*$attribute=\"([^\"]*)\"[^>]*>", RegexOption.IGNORE_CASE)
        return regex.findAll(str).map { it.groupValues[1] }.toList()
    }

    fun toXml(tag: String, content: String): String = "<$tag>$content</$tag>"

    fun toXml(tag: String, attributes: Map<String, String>, content: String): String {
        val attrs = attributes.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        return "<$tag $attrs>$content</$tag>"
    }

    fun toJson(key: String, value: String): String = "\"$key\":\"${jsonEscape(value)}\""

    fun toJson(key: String, value: Number): String = "\"$key\":$value"

    fun toJson(key: String, value: Boolean): String = "\"$key\":$value"

    fun toJson(key: String, value: List<*>): String {
        return "\"$key\":[${value.joinToString(",") { it?.toString() ?: "null" }}]"
    }

    fun toJson(key: String, value: Map<String, *>): String {
        val entries = value.joinToString(",") { "${jsonEscape(it.key)}:${it.value}" }
        return "\"$key\":{$entries}"
    }

    fun buildJson(vararg pairs: Pair<String, Any?>): String {
        val entries = pairs.joinToString(",") { (key, value) ->
            when (value) {
                null -> "\"$key\":null"
                is String -> "\"$key\":\"${jsonEscape(value)}\""
                is Number -> "\"$key\":$value"
                is Boolean -> "\"$key\":$value"
                is List<*> -> "\"$key\":[${value.joinToString(",") { it?.toString() ?: "null" }}]"
                is Map<*, *> -> {
                    val mapEntries = value.joinToString(",") { (k, v) -> "\"$k\":${v ?: "null"}" }
                    "\"$key\":{$mapEntries}"
                }
                else -> "\"$key\":\"${jsonEscape(value.toString())}\""
            }
        }
        return "{$entries}"
    }

    fun prettyPrintJson(json: String, indent: Int = 2): String {
        val sb = StringBuilder()
        var level = 0
        var inString = false
        var escape = false
        for (c in json) {
            when {
                escape -> {
                    sb.append(c)
                    escape = false
                }
                c == '\\' -> {
                    sb.append(c)
                    escape = true
                }
                c == '"' -> {
                    sb.append(c)
                    inString = !inString
                }
                !inString && (c == '{' || c == '[') -> {
                    sb.append(c)
                    level++
                    sb.append("\n" + "  ".repeat(level))
                }
                !inString && (c == '}' || c == ']') -> {
                    level--
                    sb.append("\n" + "  ".repeat(level))
                    sb.append(c)
                }
                !inString && c == ',' -> {
                    sb.append(c)
                    sb.append("\n" + "  ".repeat(level))
                }
                !inString && c == ':' -> {
                    sb.append(c)
                    sb.append(" ")
                }
                !inString && c.isWhitespace() -> {}
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    fun minifyJson(json: String): String {
        return json.replace(Regex("\\s+"), "")
    }

    fun isValidJson(str: String): Boolean {
        val trimmed = str.trim()
        if (trimmed.isEmpty()) return false
        return try {
            (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                    (trimmed.startsWith("[") && trimmed.endsWith("]"))
        } catch (e: Exception) {
            false
        }
    }

    fun extractJsonField(json: String, field: String): String? {
        val regex = Regex("\"$field\"\\s*:\\s*\"([^\"]*)\"")
        return regex.find(json)?.groupValues?.get(1)
    }

    fun extractJsonNumber(json: String, field: String): Number? {
        val regex = Regex("\"$field\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)")
        return regex.find(json)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    fun extractJsonBoolean(json: String, field: String): Boolean? {
        val regex = Regex("\"$field\"\\s*:\\s*(true|false)")
        return regex.find(json)?.groupValues?.get(1)?.toBooleanStrictOrNull()
    }

    fun mergeJson(base: String, overlay: String): String {
        val baseFields = Regex("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|\\d+|true|false|null|\\[[^]]*]|\\{[^}]*})")
            .findAll(base).associate { it.groupValues[1] to it.groupValues[2] }
        val overlayFields = Regex("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|\\d+|true|false|null|\\[[^]]*]|\\{[^}]*})")
            .findAll(overlay).associate { it.groupValues[1] to it.groupValues[2] }
        val merged = baseFields.toMutableMap()
        merged.putAll(overlayFields)
        return "{" + merged.joinToString(",") { (k, v) -> "\"$k\":$v" } + "}"
    }

    fun jsonToMap(json: String): Map<String, String> {
        val regex = Regex("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"")
        return regex.findAll(json).associate { it.groupValues[1] to it.groupValues[2] }
    }

    fun mapToJson(map: Map<String, String>): String {
        return "{" + map.joinToString(",") { (k, v) -> "\"$k\":\"${jsonEscape(v)}\"" } + "}"
    }

    fun flattenJson(json: String, separator: String = "."): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val regex = Regex("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"")
        for (match in regex.findAll(json)) {
            result[match.groupValues[1]] = match.groupValues[2]
        }
        return result
    }

    fun unflattenJson(map: Map<String, String>, separator: String = "."): String {
        val nested = mutableMapOf<String, Any>()
        for ((key, value) in map) {
            val parts = key.split(separator)
            var current = nested
            for (i in 0 until parts.size - 1) {
                val part = parts[i]
                @Suppress("UNCHECKED_CAST")
                current = current.getOrPut(part) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
            current[parts.last()] = value
        }
        return mapToJson(nested.mapValues { it.value.toString() })
    }

    fun jsonPath(json: String, path: String): String? {
        val parts = path.split(".")
        var current: String? = json
        for (part in parts) {
            if (current == null) return null
            val index = part.substringAfter("[").substringBefore("]").toIntOrNull()
            val fieldName = part.substringBefore("[")
            if (index != null) {
                val arrayRegex = Regex("\\[.*\\]")
                val arrayMatch = arrayRegex.find(current) ?: return null
                val arrayContent = arrayMatch.value
                val items = arrayContent.removeSurrounding("[", "]").split(",")
                current = items.getOrNull(index)?.trim()
            } else {
                current = extractJsonField(current, fieldName)
            }
        }
        return current
    }

    fun jsonQuery(json: String, query: String): List<String> {
        val results = mutableListOf<String>()
        val regex = Regex("\"$query\"\\s*:\\s*\"([^\"]*)\"")
        for (match in regex.findAll(json)) {
            results.add(match.groupValues[1])
        }
        return results
    }

    fun jsonFilter(json: String, predicate: (String, String) -> Boolean): Map<String, String> {
        val regex = Regex("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"")
        return regex.findAll(json)
            .filter { predicate(it.groupValues[1], it.groupValues[2]) }
            .associate { it.groupValues[1] to it.groupValues[2] }
    }

    fun jsonTransform(json: String, transform: (String, String) -> Pair<String, String>): String {
        val regex = Regex("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"")
        return regex.replace(json) { match ->
            val (newKey, newValue) = transform(match.groupValues[1], match.groupValues[2])
            "\"$newKey\":\"$newValue\""
        }
    }

    fun jsonMerge(vararg jsons: String): String {
        val allFields = mutableMapOf<String, String>()
        for (json in jsons) {
            val regex = Regex("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"")
            for (match in regex.findAll(json)) {
                allFields[match.groupValues[1]] = match.groupValues[2]
            }
        }
        return "{" + allFields.joinToString(",") { (k, v) -> "\"$k\":\"$v\"" } + "}"
    }

    fun jsonDiff(json1: String, json2: String): Map<String, Pair<String?, String?>> {
        val map1 = jsonToMap(json1)
        val map2 = jsonToMap(json2)
        val allKeys = (map1.keys + map2.keys).toSet()
        val result = mutableMapOf<String, Pair<String?, String?>>()
        for (key in allKeys) {
            val v1 = map1[key]
            val v2 = map2[key]
            if (v1 != v2) {
                result[key] = Pair(v1, v2)
            }
        }
        return result
    }

    fun jsonPatch(json: String, patches: Map<String, String?>): String {
        val fields = jsonToMap(json).toMutableMap()
        for ((key, value) in patches) {
            if (value == null) fields.remove(key) else fields[key] = value
        }
        return mapToJson(fields)
    }

    fun jsonValidate(json: String, schema: Map<String, String>): List<String> {
        val errors = mutableListOf<String>()
        val fields = jsonToMap(json)
        for ((key, type) in schema) {
            val value = fields[key] ?: continue
            when (type) {
                "string" -> {}
                "number" -> if (value.toDoubleOrNull() == null) errors.add("$key: expected number")
                "boolean" -> if (value !in listOf("true", "false")) errors.add("$key: expected boolean")
                "email" -> if (!isValidEmail(value)) errors.add("$key: invalid email")
                "url" -> if (!isValidUrl(value)) errors.add("$key: invalid URL")
            }
        }
        return errors
    }

    fun jsonSort(json: String): String {
        val fields = jsonToMap(json).toSortedMap()
        return mapToJson(fields)
    }

    fun jsonGroup(json: String, key: String): Map<String, List<Map<String, String>>> {
        val regex = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"")
        val groups = mutableMapOf<String, MutableList<Map<String, String>>>()
        for (match in regex.findAll(json)) {
            val groupKey = match.groupValues[1]
            val entry = jsonToMap(match.value)
            groups.getOrPut(groupKey) { mutableListOf() }.add(entry)
        }
        return groups
    }

    fun jsonAggregate(json: String, field: String, operation: String): Double {
        val regex = Regex("\"$field\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)")
        val values = regex.findAll(json).map { it.groupValues[1].toDouble() }.toList()
        return when (operation) {
            "sum" -> values.sum()
            "avg" -> values.average()
            "min" -> values.minOrNull() ?: 0.0
            "max" -> values.maxOrNull() ?: 0.0
            "count" -> values.size.toDouble()
            else -> 0.0
        }
    }

    fun jsonUnique(json: String, field: String): List<String> {
        val regex = Regex("\"$field\"\\s*:\\s*\"([^\"]*)\"")
        return regex.findAll(json).map { it.groupValues[1] }.distinct().toList()
    }

    fun jsonCount(json: String, field: String, value: String): Int {
        val regex = Regex("\"$field\"\\s*:\\s*\"$value\"")
        return regex.findAll(json).count()
    }

    fun jsonSearch(json: String, query: String): List<String> {
        val results = mutableListOf<String>()
        val entries = jsonToMap(json)
        for ((key, value) in entries) {
            if (key.contains(query, ignoreCase = true) || value.contains(query, ignoreCase = true)) {
                results.add("\"$key\":\"$value\"")
            }
        }
        return results
    }

    fun jsonReplace(json: String, oldValue: String, newValue: String): String {
        return json.replace(oldValue, newValue)
    }

    fun jsonRemove(json: String, field: String): String {
        val regex = Regex(",?\"$field\"\\s*:\\s*\"[^\"]*\"")
        return regex.replace(json, "")
    }

    fun jsonRename(json: String, oldName: String, newName: String): String {
        val regex = Regex("\"$oldName\"(\\s*:)")
        return regex.replace(json, "\"$newName\"$1")
    }

    fun jsonAdd(json: String, field: String, value: String): String {
        return json.removeSuffix("}") + ",\"$field\":\"$value\"}"
    }

    fun jsonRemoveNulls(json: String): String {
        val regex = Regex(",?\"[^\"]+\"\\s*:\\s*null")
        return regex.replace(json, "")
    }

    fun jsonRemoveEmpty(json: String): String {
        val regex = Regex(",?\"[^\"]+\"\\s*:\\s*\"\"")
        return regex.replace(json, "")
    }

    fun jsonCompress(json: String): String = minifyJson(json)

    fun jsonDecompress(json: String): String = prettyPrintJson(json)

    fun jsonToCsv(json: String): String {
        val fields = jsonToMap(json)
        val header = fields.keys.joinToString(",")
        val values = fields.values.joinToString(",") { csvEscape(it) }
        return "$header\n$values"
    }

    fun csvToJson(csv: String): String {
        val lines = csv.lines()
        if (lines.size < 2) return "{}"
        val headers = lines[0].split(",")
        val values = lines[1].split(",")
        val entries = headers.zip(values).joinToString(",") { (k, v) -> "\"$k\":\"$v\"" }
        return "{$entries}"
    }

    fun jsonToXml(json: String, rootTag: String = "root"): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString("") { (k, v) -> "<$k>${xmlEscape(v)}</$k>" }
        return "<$rootTag>$entries</$rootTag>"
    }

    fun xmlToJson(xml: String): String {
        val regex = Regex("<([^>]+)>([^<]*)</\\1>")
        val fields = regex.findAll(xml).associate { it.groupValues[1] to it.groupValues[2] }
        return mapToJson(fields)
    }

    fun jsonToYaml(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k: $v" }
    }

    fun yamlToJson(yaml: String): String {
        val fields = yaml.lines().filter { it.contains(":") }
            .associate { line ->
                val parts = line.split(":", limit = 2)
                parts[0].trim() to parts[1].trim()
            }
        return mapToJson(fields)
    }

    fun jsonToProperties(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k=$v" }
    }

    fun propertiesToJson(properties: String): String {
        val fields = properties.lines().filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=", limit = 2)
                parts[0].trim() to parts[1].trim()
            }
        return mapToJson(fields)
    }

    fun jsonToQueryString(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("&") { (k, v) -> "${urlEncode(k)}=${urlEncode(v)}" }
    }

    fun queryStringToJson(query: String): String {
        val fields = query.split("&").filter { it.contains("=") }
            .associate { param ->
                val parts = param.split("=", limit = 2)
                urlDecode(parts[0]) to urlDecode(parts.getOrElse(1) { "" })
            }
        return mapToJson(fields)
    }

    fun jsonToFormData(json: String): String = jsonToQueryString(json)

    fun formDataToJson(formData: String): String = queryStringToJson(formData)

    fun jsonToTable(json: String): String {
        val fields = jsonToMap(json)
        val maxKeyLen = fields.keys.maxOfOrNull { it.length } ?: 0
        val maxValLen = fields.values.maxOfOrNull { it.length } ?: 0
        val separator = "+" + "-".repeat(maxKeyLen + 2) + "+" + "-".repeat(maxValLen + 2) + "+"
        val rows = fields.joinToString("\n") { (k, v) ->
            "| ${k.padEnd(maxKeyLen)} | ${v.padEnd(maxValLen)} |"
        }
        return "$separator\n$rows\n$separator"
    }

    fun jsonToMarkdown(json: String): String {
        val fields = jsonToMap(json)
        val maxKeyLen = fields.keys.maxOfOrNull { it.length } ?: 0
        val header = "| ${"Key".padEnd(maxKeyLen)} | Value |"
        val separator = "|${"-".repeat(maxKeyLen + 2)}|-------|"
        val rows = fields.joinToString("\n") { (k, v) -> "| ${k.padEnd(maxKeyLen)} | $v |" }
        return "$header\n$separator\n$rows"
    }

    fun jsonToHtml(json: String): String {
        val fields = jsonToMap(json)
        val rows = fields.joinToString("") { (k, v) -> "<tr><td>$k</td><td>$v</td></tr>" }
        return "<table>$rows</table>"
    }

    fun jsonToText(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k: $v" }
    }

    fun jsonToKeyValue(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k=$v" }
    }

    fun jsonToIni(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k = $v" }
    }

    fun iniToJson(ini: String): String {
        val fields = ini.lines().filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=", limit = 2)
                parts[0].trim() to parts[1].trim()
            }
        return mapToJson(fields)
    }

    fun jsonToToml(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "$k = \"$v\"" }
    }

    fun tomlToJson(toml: String): String {
        val fields = toml.lines().filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=", limit = 2)
                parts[0].trim() to parts[1].trim().removeSurrounding("\"")
            }
        return mapToJson(fields)
    }

    fun jsonToEnv(json: String): String {
        val fields = jsonToMap(json)
        return fields.joinToString("\n") { (k, v) -> "${k.uppercase(Locale.getDefault())}=$v" }
    }

    fun envToJson(env: String): String {
        val fields = env.lines().filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=", limit = 2)
                parts[0].trim().lowercase(Locale.getDefault()) to parts[1].trim()
            }
        return mapToJson(fields)
    }

    fun jsonToXmlRpc(json: String): String {
        val fields = jsonToMap(json)
        val params = fields.joinToString("") { (k, v) -> "<param><value><string>$v</string></value></param>" }
        return "<methodCall><params>$params</params></methodCall>"
    }

    fun jsonToSoap(json: String, operation: String = "Request"): String {
        val fields = jsonToMap(json)
        val body = fields.joinToString("") { (k, v) -> "<$k>$v</$k>" }
        return "<soap:Envelope><soap:Body><$operation>$body</$operation></soap:Body></soap:Envelope>"
    }

    fun jsonToGraphQL(json: String, queryName: String = "Query"): String {
        val fields = jsonToMap(json)
        val fieldNames = fields.keys.joinToString(" ")
        return "query $queryName { $fieldNames }"
    }

    fun jsonToSqlInsert(json: String, table: String): String {
        val fields = jsonToMap(json)
        val columns = fields.keys.joinToString(", ")
        val values = fields.values.joinToString(", ") { "'$it'" }
        return "INSERT INTO $table ($columns) VALUES ($values);"
    }

    fun jsonToSqlUpdate(json: String, table: String, idField: String, idValue: String): String {
        val fields = jsonToMap(json).filterKeys { it != idField }
        val setClause = fields.joinToString(", ") { (k, v) -> "$k = '$v'" }
        return "UPDATE $table SET $setClause WHERE $idField = '$idValue';"
    }

    fun jsonToSqlSelect(json: String, table: String): String {
        val fields = jsonToMap(json)
        val columns = fields.keys.joinToString(", ")
        val where = fields.entries.joinToString(" AND ") { (k, v) -> "$k = '$v'" }
        return "SELECT $columns FROM $table WHERE $where;"
    }

    fun jsonToSqlDelete(json: String, table: String): String {
        val fields = jsonToMap(json)
        val where = fields.entries.joinToString(" AND ") { (k, v) -> "$k = '$v'" }
        return "DELETE FROM $table WHERE $where;"
    }

    fun jsonToMongoDb(json: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "db.collection.insertOne({$entries});"
    }

    fun jsonToRedis(json: String, key: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(" ") { (k, v) -> "HSET $key \"$k\" \"$v\"" }
        return entries
    }

    fun jsonToElasticsearch(json: String, index: String, id: String = "1"): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\"index\": {\"_index\": \"$index\", \"_id\": \"$id\"}}\n{$entries}"
    }

    fun jsonToDynamoDb(json: String, table: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": {\"S\": \"$v\"}" }
        return "{\"TableName\": \"$table\", \"Item\": {$entries}}"
    }

    fun jsonToFirebase(json: String, path: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\"path\": \"$path\", \"data\": {$entries}}"
    }

    fun jsonToProtobuf(json: String, messageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.mapIndexed { i, (k, v) -> "  string $k = ${i + 1}; // $v" }.joinToString("\n")
        return "message $messageName {\n$entries\n}"
    }

    fun jsonToAvro(json: String, recordName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.mapIndexed { i, (k, v) -> "{\"name\": \"$k\", \"type\": \"string\"}" }.joinToString(", ")
        return "{\"type\": \"record\", \"name\": \"$recordName\", \"fields\": [$entries]}"
    }

    fun jsonToParquet(json: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k: string" }
        return "message Schema {\n  $entries\n}"
    }

    fun jsonToOrc(json: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(", ")
        return "struct<$entries>"
    }

    fun jsonToArrow(json: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k: utf8" }
        return "Schema({$entries})"
    }

    fun jsonToFeather(json: String): String = jsonToArrow(json)

    fun jsonToHdf5(json: String, dataset: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "import h5py\nwith h5py.File('data.h5', 'w') as f:\n    f.create_dataset('$dataset', data={$entries})"
    }

    fun jsonToNetcdf(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "import netCDF4\nnc = netCDF4.Dataset('data.nc', 'w')\nnc.createVariable('$varName', 'S1', ())\nnc.variables['$varName'][:] = {$entries}"
    }

    fun jsonToMatlab(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "'$k', '$v'" }
        return "$varName = struct($entries);"
    }

    fun jsonToR(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> "\"$k\" = \"$v\"" }
        return "$varName <- list($entries)"
    }

    fun jsonToJulia(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.joinToString(", ") { (k, v) -> ":$k => \"$v\"" }
        return "$varName = Dict($entries)"
    }

    fun jsonToGo(json: String, structName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n\t") { (k, v) -> "${k.replaceFirstChar { it.uppercase() }} string `json:\"$k\"`" }
        return "type $structName struct {\n\t$entries\n}"
    }

    fun jsonToRust(json: String, structName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: String" }
        return "#[derive(Serialize, Deserialize)]\npub struct $structName {\n    $entries,\n}"
    }

    fun jsonToTypeScript(json: String, interfaceName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(";\n    ") { (k, v) -> "$k: string" }
        return "export interface $interfaceName {\n    $entries;\n}"
    }

    fun jsonToPython(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k='$v'" }
        return "class $className:\n    def __init__(self):\n        " + fields.entries.joinToString("\n        ") { (k, v) -> "self.$k = '$v'" }
    }

    fun jsonToJava(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(";\n    ") { (k, v) -> "private String $k" }
        val getters = fields.entries.joinToString("\n    ") { (k, v) ->
            "public String get${k.replaceFirstChar { it.uppercase() }}() { return $k; }"
        }
        return "public class $className {\n    $entries;\n    $getters\n}"
    }

    fun jsonToCSharp(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(";\n        ") { (k, v) -> "public string $k { get; set; }" }
        return "public class $className {\n        $entries;\n    }"
    }

    fun jsonToSwift(json: String, structName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(";\n    ") { (k, v) -> "var $k: String" }
        return "struct $structName: Codable {\n    $entries;\n}"
    }

    fun jsonToKotlin(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "val $k: String" }
        return "data class $className(\n    $entries\n)"
    }

    fun jsonToDart(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(";\n  ") { (k, v) -> "final String $k" }
        return "class $className {\n  $entries;\n  $className({${fields.keys.joinToString(", ") { "this.$it" }}});\n}"
    }

    fun jsonToRuby(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(", ") { ":$it" }
        return "class $className\n  attr_accessor $entries\n\n  def initialize(**args)\n    ${
            fields.keys.joinToString("\n    ") { "@$it = args[:$it]" }
        }\n  end\nend"
    }

    fun jsonToPhp(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(";\n    ") { "private \$$it" }
        return "class $className {\n    $entries;\n\n    public function __construct(${
            fields.keys.joinToString(", ") { "\$$it" }
        }) {\n        ${
            fields.keys.joinToString("\n        ") { "@$it = \$$it" }
        }\n    }\n}"
    }

    fun jsonToScala(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k: String" }
        return "case class $className($entries)"
    }

    fun jsonToHaskell(json: String, typeName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k :: String" }
        return "data $typeName = $typeName { $entries }"
    }

    fun jsonToElixir(json: String, moduleName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(", ") { it }
        return "defmodule $moduleName do\n  defstruct [$entries]\nend"
    }

    fun jsonToClojure(json: String, recordName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(" :") { it }
        return "(defrecord $recordName [$entries])"
    }

    fun jsonToErlang(json: String, recordName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.keys.joinToString(", ") { it }
        return "-record($recordName, {$entries})."
    }

    fun jsonToLua(json: String, tableName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k = \"$v\"" }
        return "$tableName = {$entries}"
    }

    fun jsonToPerl(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "'$k' => '$v'" }
        return "my %$varName = ($entries);"
    }

    fun jsonToBash(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "declare -A $varName=([$k]=\"$v\")" }
        return entries
    }

    fun jsonToPowerShell(json: String, varName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("; ") { (k, v) -> "`$$varName.$k = '$v'" }
        return "`$$varName = @{}; $entries"
    }

    fun jsonToYamlConfig(json: String, appName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "$k: $v" }
        return "$appName:\n  $entries"
    }

    fun jsonToDockerCompose(json: String, serviceName: String): String {
        val fields = jsonToMap(json)
        val env = fields.entries.joinToString("\n      ") { (k, v) -> "- $k=$v" }
        return "services:\n  $serviceName:\n    environment:\n      $env"
    }

    fun jsonToKubernetes(json: String, deploymentName: String): String {
        val fields = jsonToMap(json)
        val env = fields.entries.joinToString("\n        ") { (k, v) -> "- name: $k\n          value: \"$v\"" }
        return "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: $deploymentName\nspec:\n  template:\n    spec:\n      containers:\n        - name: app\n          env:\n        $env"
    }

    fun jsonToTerraform(json: String, resourceName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$k = \"$v\"" }
        return "resource \"null_resource\" \"$resourceName\" {\n  triggers = {\n    $entries\n  }\n}"
    }

    fun jsonToAnsible(json: String, playName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$k: $v" }
        return "- name: $playName\n  hosts: all\n  tasks:\n    - name: Configure\n      set_fact:\n        $entries"
    }

    fun jsonToChef(json: String, recipeName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "default['$recipeName']['$k'] = '$v'" }
        return entries
    }

    fun jsonToPuppet(json: String, className: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$$k = '$v'" }
        return "class $className {\n  $entries\n}"
    }

    fun jsonToVagrant(json: String, vmName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "config.vm.define \"$vmName\" do |$k|\n    $k.$k = \"$v\"\n  end" }
        return entries
    }

    fun jsonToGradle(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "$k = '$v'" }
        return "project('$projectName') {\n    $entries\n}"
    }

    fun jsonToMaven(json: String, artifactId: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n        ") { (k, v) -> "<$k>$v</$k>" }
        return "<project>\n    <artifactId>$artifactId</artifactId>\n    $entries\n</project>"
    }

    fun jsonToNpm(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$packageName\",\n  $entries\n}"
    }

    fun jsonToCargo(json: String, crateName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "$k = \"$v\"" }
        return "[package]\nname = \"$crateName\"\n$entries"
    }

    fun jsonToGem(json: String, gemName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "spec.$k = '$v'" }
        return "Gem::Specification.new do |spec|\n  spec.name = '$gemName'\n  $entries\nend"
    }

    fun jsonToComposer(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n    \"name\": \"$packageName\",\n    $entries\n}"
    }

    fun jsonToPip(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "$k=$v" }
        return "[metadata]\nname = $packageName\n$entries"
    }

    fun jsonToGoMod(json: String, moduleName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> "$k = $v" }
        return "module $moduleName\n\ngo 1.21\n\nrequire (\n    $entries\n)"
    }

    fun jsonToCabal(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "$k: $v" }
        return "name: $packageName\nversion: 0.1.0.0\n$entries"
    }

    fun jsonToMix(json: String, appName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: \"$v\"" }
        return "defmodule $appName.MixProject do\n  use Mix.Project\n\n  def project do\n    [\n      app: :$appName,\n      $entries\n    ]\n  end\nend"
    }

    fun jsonToStack(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "$k: $v" }
        return "resolver: lts-20.0\npackages:\n- .\nextra-deps:\n  $entries"
    }

    fun jsonToNix(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$k = \"$v\";" }
        return "{ pkgs ? import <nixpkgs> {} }:\npkgs.stdenv.mkDerivation {\n  name = \"$packageName\";\n  $entries\n}"
    }

    fun jsonToBazel(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "\"$k\": \"$v\"," }
        return "cc_binary(\n    name = \"$targetName\",\n    srcs = [\"$targetName.cc\"],\n    deps = [\n        $entries\n    ],\n)"
    }

    fun jsonToBuck(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k = \"$v\"," }
        return "android_binary(\n    name = \"$targetName\",\n    manifest = \"AndroidManifest.xml\",\n    deps = [\n        $entries\n    ],\n)"
    }

    fun jsonToPants(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k=\"$v\"," }
        return "python_binary(\n    name=\"$targetName\",\n    dependencies=[\n        $entries\n    ],\n)"
    }

    fun jsonToBlade(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k = \"$v\"," }
        return "java_library(\n    name = \"$targetName\",\n    srcs = glob([\"**/*.java\"]),\n    deps = [\n        $entries\n    ],\n)"
    }

    fun jsonToTup(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n") { (k, v) -> ": $k = $v" }
        return "ifeq ($(CONFIG),)\nCONFIG=endif\n\n$entries"
    }

    fun jsonToScons(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "\"$k\": \"$v\"" }
        return "env = Environment($entries)\nenv.Program(target = \"$targetName\", source = [\"$targetName.c\"])"
    }

    fun jsonToWaf(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(", ") { (k, v) -> "$k='$v'" }
        return "def configure(cfg):\n    cfg.load('compiler_c')\n    ${
            fields.entries.joinToString("\n    ") { (k, v) -> "cfg.env.$k = '$v'" }
        }\n\ndef build(bld):\n    bld.program(target='$targetName', source='$targetName.c')"
    }

    fun jsonToXmake(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "add_$k(\"$v\")" }
        return "target(\"$targetName\")\n    set_kind(\"binary\")\n    $entries\n    add_files(\"$targetName.cpp\")"
    }

    fun jsonToPremake(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "$k \"$v\"" }
        return "workspace \"$projectName\"\n    configurations { \"Debug\", \"Release\" }\n\nproject \"$projectName\"\n    kind \"ConsoleApp\"\n    language \"C++\"\n    $entries"
    }

    fun jsonToCmake(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "$k \"$v\"" }
        return "cmake_minimum_required(VERSION 3.20)\nproject($projectName)\n\nset(CMAKE_CXX_STANDARD 17)\n\nadd_executable($projectName main.cpp)\n\n$entries"
    }

    fun jsonToMeson(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "'$k': '$v'" }
        return "project('$projectName', 'c',\n  version : '0.1.0',\n  default_options : ['warning_level=3'],\n  $entries\n)"
    }

    fun jsonToBazelModule(json: String, moduleName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "$k = \"$v\"," }
        return "module(\n    name = \"$moduleName\",\n    version = \"0.1.0\",\\n    $entries\n)"
    }

    fun jsonToGazelle(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n    ") { (k, v) -> "# $k: $v" }
        return "# gazelle:prefix $packageName\n$entries"
    }

    fun jsonToPlease(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k = \"$v\"," }
        return "go_binary(\n    name = \"$targetName\",\n    srcs = [\"$targetName.go\"],\n    deps = [\n        $entries\n    ],\n)"
    }

    fun jsonToBuck2(json: String, targetName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k = \"$v\"," }
        return "rust_binary(\n    name = \"$targetName\",\n    srcs = [\"$targetName.rs\"],\n    deps = [\n        $entries\n    ],\n)"
    }

    fun jsonToNx(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: \"$v\"," }
        return "{\n  \"$projectName]: {\n    $entries\n  }\n}"
    }

    fun jsonToTurborepo(json: String, pipelineName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: \"$v\"," }
        return "{\n  \"$pipelineName]: {\n    $entries\n  }\n}"
    }

    fun jsonToLerna(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"packages\": [\"packages/*\"],\n  \"version\": \"independent\",\n  $entries\n}"
    }

    fun jsonToYarn(json: String, workspaceName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$workspaceName\",\n  \"private\": true,\n  $entries\n}"
    }

    fun jsonToPnpm(json: String, workspaceName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$workspaceName\",\n  \"private\": true,\n  $entries\n}"
    }

    fun jsonToBun(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$projectName\",\n  $entries\n}"
    }

    fun jsonToDeno(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$projectName\",\n  $entries\n}"
    }

    fun jsonToNode(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString(",\n  ") { (k, v) -> "\"$k\": \"$v\"" }
        return "{\n  \"name\": \"$projectName\",\n  $entries\n}"
    }

    fun jsonToDartPub(json: String, packageName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$k: $v" }
        return "name: $packageName\n$entries"
    }

    fun jsonToFlutter(json: String, projectName: String): String {
        val fields = jsonToMap(json)
        val entries = fields.entries.joinToString("\n  ") { (k, v) -> "$k: $v" }
        return "name: $projectName\n$entries"
    }

    fun jsonToReact(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        val props = fields.keys.joinToString(", ") { it }
        return "import React from 'react';\n\nfunction $componentName({ $props }) {\n  return (\n    <div>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<p>{ $k }</p>" }
        }\n    </div>\n  );\n}\n\nexport default $componentName;"
    }

    fun jsonToVue(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        val data = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: '$v'" }
        return "<template>\n  <div>\n    ${
            fields.entries.joinToString("\n    ") { (k, v) -> "<p>{{ $k }}</p>" }
        }\n  </div>\n</template>\n\n<script>\nexport default {\n  name: '$componentName',\n  data() {\n    return {\n      $data\n    }\n  }\n}\n</script>"
    }

    fun jsonToSvelte(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        val vars = fields.entries.joinToString("\n  ") { (k, v) -> "let $k = '$v';" }
        return "<script>\n  $vars\n</script>\n\n<div>\n  ${
            fields.entries.joinToString("\n  ") { (k, v) -> "<p>{$k}</p>" }
        }\n</div>"
    }

    fun jsonToAngular(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        val properties = fields.entries.joinToString(";\n  ") { (k, v) -> "$k = '$v'" }
        return "import { Component } from '@angular/core';\n\n@Component({\n  selector: 'app-$componentName',\n  template: `\n    <div>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<p>{{ $k }}</p>" }
        }\n    </div>\n  `\n})\nexport class ${componentName.replaceFirstChar { it.uppercase() }}Component {\n  $properties;\n}"
    }

    fun jsonToSolid(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString("\n  ") { (k, v) -> "const [$k] = createSignal('$v');" }
        return "import { createSignal } from 'solid-js';\n\nfunction $componentName() {\n  $consts\n  return (\n    <div>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<p>{$k()}</p>" }
        }\n    </div>\n  );\n}\n\nexport default $componentName;"
    }

    fun jsonToQwik(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString("\n  ") { (k, v) -> "const $k = useSignal('$v');" }
        return "import { component$, useSignal } from '@builder.io/qwik';\n\nexport const $componentName = component$(() => {\n  $consts\n  return (\n    <div>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<p>{$k.value}</p>" }
        }\n    </div>\n  );\n});"
    }

    fun jsonToAlpine(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(", ") { (k, v) -> "$k: '$v'" }
        return "<div x-data=\"{ $consts }\">\n  ${
            fields.entries.joinToString("\n  ") { (k, v) -> "<p x-text=\"$k\"></p>" }
        }\n</div>"
    }

    fun jsonToStimulus(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString("\n  ") { (k, v) -> "this.$k = '$v';" }
        return "import { Controller } from '@hotwired/stimulus';\n\nexport default class extends Controller {\n  connect() {\n    $consts\n  }\n}"
    }

    fun jsonToHTMX(json: String, elementId: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(" ") { (k, v) -> "data-$k=\"$v\"" }
        return "<div id=\"$elementId\" $consts>\n  ${
            fields.entries.joinToString("\n  ") { (k, v) -> "<p data-$k=\"$v\"></p>" }
        }\n</div>"
    }

    fun jsonToHotwire(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString("\n  ") { (k, v) -> "this.$k = '$v';" }
        return "import { Controller } from '@hotwired/stimulus';\n\nexport default class extends Controller {\n  static targets = [${fields.keys.joinToString(", ") { "\"$it\"" }}];\n\n  connect() {\n    $consts\n  }\n}"
    }

    fun jsonToPhoenix(json: String, moduleName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: \"$v\"" }
        return "defmodule $moduleName do\n  use Phoenix.LiveView\n\n  def render(assigns) do\n    ~H\"\"\"\n    <div>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<p><%= @#{it.key} %></p>" }
        }\n    </div>\n    \"\"\"\n  end\n\n  def mount(_params, _session, socket) do\n    {:ok, assign(socket, $consts)}\n  end\nend"
    }

    fun jsonToRails(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: '$v'" }
        return "class ${controllerName.replaceFirstChar { it.uppercase() }}Controller < ApplicationController\n  def index\n    @data = {\n      $consts\n    }\n  end\nend"
    }

    fun jsonToDjango(json: String, viewName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "'$k': '$v'" }
        return "from django.shortcuts import render\n\ndef $viewName(request):\n    context = {\n        $consts\n    }\n    return render(request, 'template.html', context)"
    }

    fun jsonToFlask(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k='$v'" }
        return "from flask import Flask, render_template\n\napp = Flask(__name__)\n\n@app.route('/')\ndef $routeName():\n    return render_template('template.html', $consts)"
    }

    fun jsonToExpress(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: '$v'" }
        return "const express = require('express');\nconst app = express();\n\napp.get('/', (req, res) => {\n  res.render('template', {\n    $consts\n  });\n});"
    }

    fun jsonToFastAPI(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k='$v'" }
        return "from fastapi import FastAPI\nfrom fastapi.responses import HTMLResponse\n\napp = FastAPI()\n\n@app.get('/')\ndef $routeName():\n    return HTMLResponse(content=f'''\n    <div>\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "<p>$k: { $k }</p>" }
        }\n    </div>\n    ''')"
    }

    fun jsonToNestJS(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n    ") { (k, v) -> "$k: '$v'" }
        return "import { Controller, Get } from '@nestjs/common';\n\n@Controller()\nexport class ${controllerName.replaceFirstChar { it.uppercase() }}Controller {\n  @Get()\n  getData() {\n    return {\n      $consts\n    };\n  }\n}"
    }

    fun jsonToSpring(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "$k: \"$v\"" }
        return "@RestController\npublic class ${controllerName.replaceFirstChar { it.uppercase() }}Controller {\n    @GetMapping\n    public Map<String, String> getData() {\n        return Map.of(\n            $consts\n        );\n    }\n}"
    }

    fun jsonToMicronaut(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "@Controller\npublic class ${controllerName.replaceFirstChar { it.uppercase() }}Controller {\n    @Get\n    public Map<String, String> getData() {\n        return Map.of(\n            $consts\n        );\n    }\n}"
    }

    fun jsonToQuarkus(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "@Path(\"/\")\npublic class ${controllerName.replaceFirstChar { it.uppercase() }}Controller {\n    @GET\n    public Map<String, String> getData() {\n        return Map.of(\n            $consts\n        );\n    }\n}"
    }

    fun jsonToVertx(json: String, verticleName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "public class ${verticleName.replaceFirstChar { it.uppercase() }}Verticle extends AbstractVerticle {\n  public void start() {\n    vertx.createHttpServer()\n      .requestHandler(req -> {\n        req.response()\n          .putHeader(\"content-type\", \"application/json\")\n          .end(\"{$consts}\");\n      })\n      .listen(8080);\n  }\n}"
    }

    fun jsonToKtor(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "import io.ktor.server.application.*\nimport io.ktor.server.response.*\nimport io.ktor.server.routing.*\n\nfun Application.module() {\n    routing {\n        get(\"/\") {\n            call.respond(mapOf(\n                $consts\n            ))\n        }\n    }\n}"
    }

    fun jsonToAkka(json: String, actorName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "public class ${actorName.replaceFirstChar { it.uppercase() }} extends AbstractActor {\n    @Override\n    public Receive createReceive() {\n        return receiveBuilder()\n            .matchAny(msg -> {\n                Map<String, String> data = Map.of(\n                    $consts\n                );\n                getSender().tell(data, getSelf());\n            })\n            .build();\n    }\n}"
    }

    fun jsonToPlay(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\": \"$v\"" }
        return "public class ${controllerName.replaceFirstChar { it.uppercase() }} extends Controller {\n    public Result index() {\n        return ok(Json.toJson(Map.of(\n            $consts\n        )));\n    }\n}"
    }

    fun jsonToLaravel(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n            ") { (k, v) -> "'$k' => '$v'" }
        return "class ${controllerName.replaceFirstChar { it.uppercase() }}Controller extends Controller\n{\n    public function index()\n    {\n        return view('template', [\n            $consts\n        ]);\n    }\n}"
    }

    fun jsonToSymfony(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n            ") { (k, v) -> "'$k' => '$v'" }
        return "class ${controllerName.replaceFirstChar { it.uppercase() }}Controller extends AbstractController\n{\n    #[Route('/', name: 'app_index')]\n    public function index(): Response\n    {\n        return $this->render('template.html.twig', [\n            $consts\n        ]);\n    }\n}"
    }

    fun jsonToCakePHP(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n            ") { (k, v) -> "'$k' => '$v'" }
        return "class ${controllerName.replaceFirstChar { it.uppercase() }}Controller extends AppController\n{\n    public function index()\n    {\n        $this->set([\n            $consts\n        ]);\n    }\n}"
    }

    fun jsonToCodeIgniter(json: String, controllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n            ") { (k, v) -> "'$k' => '$v'" }
        return "class ${controllerName.replaceFirstChar { it.uppercase() }} extends BaseController\n{\n    public function index()\n    {\n        $data = [\n            $consts\n        ];\n        return view('template', $data);\n    }\n}"
    }

    fun jsonToSlim(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\" => \"$v\"" }
        return "$app->get('/', function ($request, $response) {\n    $data = [\n        $consts\n    ];\n    return $this->view->render($response, 'template.phtml', $data);\n});"
    }

    fun jsonToLumen(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\" => \"$v\"" }
        return "$router->get('/', function () {\n    $data = [\n        $consts\n    ];\n    return view('template', $data);\n});"
    }

    fun jsonToSilex(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\" => \"$v\"" }
        return "$app->get('/', function (Request $request) use ($app) {\n    $data = [\n        $consts\n    ];\n    return $app->json($data);\n});"
    }

    fun jsonToSlimFramework(json: String, routeName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "\"$k\" => \"$v\"" }
        return "$app->get('/', function (Request $request, Response $response) {\n    $data = [\n        $consts\n    ];\n    return $this->view->render($response, 'template.phtml', $data);\n});"
    }

    fun jsonToMezzio(json: String, handlerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n            ") { (k, v) -> "\"$k\" => \"$v\"" }
        return "class $handlerName implements RequestHandlerInterface\n{\n    public function handle(ServerRequest $request): ResponseInterface\n    {\n        $data = [\n            $consts\n        ];\n        return new JsonResponse($data);\n    }\n}"
    }

    fun jsonToReactNative(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n  ") { (k, v) -> "const [$k, set${k.replaceFirstChar { it.uppercase() }}] = useState('$v')" }
        return "import React, { useState } from 'react';\nimport { View, Text } from 'react-native';\n\nfunction $componentName() {\n  $consts\n  return (\n    <View>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<Text>{ $k }</Text>" }
        }\n    </View>\n  );\n}\n\nexport default $componentName;"
    }

    fun jsonToIonic(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n  ") { (k, v) -> "$k = '$v'" }
        return "import { Component } from '@angular/core';\n\n@Component({\n  selector: 'app-$componentName',\n  template: `\n    <ion-content>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<ion-item><ion-label>{{ $k }}</ion-label></ion-item>" }
        }\n    </ion-content>\n  `\n})\nexport class ${componentName.replaceFirstChar { it.uppercase() }}Component {\n  $consts;\n}"
    }

    fun jsonToNativeScript(json: String, componentName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n  ") { (k, v) -> "public $k = '$v'" }
        return "import { Component } from '@angular/core';\n\n@Component({\n  selector: 'app-$componentName',\n  template: `\n    <StackLayout>\n      ${
            fields.entries.joinToString("\n      ") { (k, v) -> "<Label [text]=\"$k\"></Label>" }
        }\n    </StackLayout>\n  `\n})\nexport class ${componentName.replaceFirstChar { it.uppercase() }}Component {\n  $consts;\n}"
    }

    fun jsonToFlutterWidget(json: String, widgetName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n  ") { (k, v) -> "final String $k = '$v'" }
        return "import 'package:flutter/material.dart';\n\nclass ${widgetName.replaceFirstChar { it.uppercase() }} extends StatelessWidget {\n  $consts\n\n  @override\n  Widget build(BuildContext context) {\n    return Column(\n      children: [\n        ${
            fields.entries.joinToString(",\n        ") { (k, v) -> "Text($k)" }
        },\n      ],\n    );\n  }\n}"
    }

    fun jsonToSwiftUI(json: String, viewName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import SwiftUI\n\nstruct ${viewName.replaceFirstChar { it.uppercase() }}: View {\n    $consts\n\n    var body: some View {\n        VStack {\n            ${
            fields.entries.joinToString("\n            ") { (k, v) -> "Text($k)" }
        }\n        }\n    }\n}"
    }

    fun jsonToUIKit(json: String, viewControllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import UIKit\n\nclass ${viewControllerName.replaceFirstChar { it.uppercase() }}: UIViewController {\n    $consts\n\n    override func viewDidLoad() {\n        super.viewDidLoad()\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "let label = UILabel()\n        label.text = $k\n        view.addSubview(label)" }
        }\n    }\n}"
    }

    fun jsonToAppKit(json: String, viewControllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import Cocoa\n\nclass ${viewControllerName.replaceFirstChar { it.uppercase() }}: NSViewController {\n    $consts\n\n    override func viewDidLoad() {\n        super.viewDidLoad()\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "let label = NSTextField(labelWithString: $k)\n        view.addSubview(label)" }
        }\n    }\n}"
    }

    fun jsonToWatchKit(json: String, interfaceControllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import WatchKit\n\nclass ${interfaceControllerName.replaceFirstChar { it.uppercase() }}: WKInterfaceController {\n    $consts\n\n    override func awake(withContext context: Any?) {\n        super.awake(withContext: context)\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "let label = WKInterfaceLabel()\n        label.setText($k)" }
        }\n    }\n}"
    }

    fun jsonToTvOS(json: String, viewControllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import UIKit\n\nclass ${viewControllerName.replaceFirstChar { it.uppercase() }}: UIViewController {\n    $consts\n\n    override func viewDidLoad() {\n        super.viewDidLoad()\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "let label = UILabel()\n        label.text = $k\n        view.addSubview(label)" }
        }\n    }\n}"
    }

    fun jsonToMacCatalyst(json: String, viewControllerName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "let $k = \"$v\"" }
        return "import UIKit\n\nclass ${viewControllerName.replaceFirstChar { it.uppercase() }}: UIViewController {\n    $consts\n\n    override func viewDidLoad() {\n        super.viewDidLoad()\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "let label = UILabel()\n        label.text = $k\n        view.addSubview(label)" }
        }\n    }\n}"
    }

    fun jsonToAndroidView(json: String, viewName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "private val $k = \"$v\"" }
        return "class $viewName(context: Context) : View(context) {\n    $consts\n\n    override fun onDraw(canvas: Canvas) {\n        super.onDraw(canvas)\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "canvas.drawText($k, 0f, 0f, Paint())" }
        }\n    }\n}"
    }

    fun jsonToAndroidCompose(json: String, composableName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "val $k = \"$v\"" }
        return "@Composable\nfun ${composableName.replaceFirstChar { it.uppercase() }}() {\n    $consts\n    Column {\n        ${
            fields.entries.joinToString("\n        ") { (k, v) -> "Text(text = $k)" }
        }\n    }\n}"
    }

    fun jsonToAndroidViewModel(json: String, viewModelName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "private val _${it.key} = MutableStateFlow(\"$v\")\n    val $k: StateFlow<String> = _${it.key}.asStateFlow()" }
        return "class ${viewModelName.replaceFirstChar { it.uppercase() }} : ViewModel() {\n    $consts\n}"
    }

    fun jsonToAndroidRepository(json: String, repositoryName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "private val $k = \"$v\"" }
        return "class ${repositoryName.replaceFirstChar { it.uppercase() }} {\n    $consts\n\n    fun getData(): Map<String, String> {\n        return mapOf(\n            ${
            fields.entries.joinToString(",\n            ") { (k, v) -> "\"$k\" to $k" }
        }\n        )\n    }\n}"
    }

    fun jsonToAndroidUseCase(json: String, useCaseName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(";\n    ") { (k, v) -> "private val $k = \"$v\"" }
        return "class ${useCaseName.replaceFirstChar { it.uppercase() }} {\n    $consts\n\n    operator fun invoke(): Map<String, String> {\n        return mapOf(\n            ${
            fields.entries.joinToString(",\n            ") { (k, v) -> "\"$k\" to $k" }
        }\n        )\n    }\n}"
    }

    fun jsonToAndroidEntity(json: String, entityName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "@ColumnInfo(name = \"$k\")\n        val $k: String = \"$v\"" }
        return "@Entity(tableName = \"$entityName\")\ndata class ${entityName.replaceFirstChar { it.uppercase() }}(\n        @PrimaryKey val id: Int = 0,\n        $consts\n)"
    }

    fun jsonToAndroidDao(json: String, daoName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "@Query(\"SELECT * FROM $k WHERE $k = :$k\")\n        suspend fun get${k.replaceFirstChar { it.uppercase() }}($k: String): ${k.replaceFirstChar { it.uppercase() }}?" }
        return "@Dao\ninterface ${daoName.replaceFirstChar { it.uppercase() }} {\n    $consts\n}"
    }

    fun jsonToAndroidDatabase(json: String, databaseName: String): String {
        val fields = jsonToMap(json)
        consts = fields.entries.joinToString(",\n        ") { (k, v) -> "${k.replaceFirstChar { it.uppercase() }}::class" }
