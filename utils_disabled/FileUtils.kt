package com.jarvis.assistant.utils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FileUtils {

    private const val BUFFER_SIZE = 8192
    private const val KB = 1024L
    private const val MB = KB * 1024
    private const val GB = MB * 1024
    private const val TB = GB * 1024

    fun exists(file: File): Boolean = file.exists()

    fun isFile(file: File): Boolean = file.isFile

    fun isDirectory(file: File): Boolean = file.isDirectory

    fun isHidden(file: File): Boolean = file.isHidden

    fun canRead(file: File): Boolean = file.canRead()

    fun canWrite(file: File): Boolean = file.canWrite()

    fun canExecute(file: File): Boolean = file.canExecute()

    fun length(file: File): Long = file.length()

    fun lastModified(file: File): Long = file.lastModified()

    fun setLastModified(file: File, time: Long): Boolean = file.setLastModified(time)

    fun createFile(file: File): Boolean {
        return try {
            file.parentFile?.mkdirs()
            file.createNewFile()
        } catch (e: IOException) {
            false
        }
    }

    fun createDirectory(dir: File): Boolean {
        return dir.mkdirs()
    }

    fun createDirectories(dir: File): Boolean {
        return dir.mkdirs()
    }

    fun delete(file: File): Boolean {
        return file.delete()
    }

    fun deleteRecursively(file: File): Boolean {
        return file.deleteRecursively()
    }

    fun deleteOnExit(file: File) {
        file.deleteOnExit()
    }

    fun renameTo(source: File, dest: File): Boolean {
        return source.renameTo(dest)
    }

    fun copy(source: File, dest: File): Boolean {
        return try {
            if (source.isDirectory) {
                copyDirectory(source, dest)
            } else {
                copyFile(source, dest)
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun copyFile(source: File, dest: File) {
        dest.parentFile?.mkdirs()
        FileInputStream(source).use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output, BUFFER_SIZE)
            }
        }
    }

    private fun copyDirectory(source: File, dest: File) {
        if (!dest.exists()) dest.mkdirs()
        source.listFiles()?.forEach { file ->
            val destFile = File(dest, file.name)
            if (file.isDirectory) {
                copyDirectory(file, destFile)
            } else {
                copyFile(file, destFile)
            }
        }
    }

    fun move(source: File, dest: File): Boolean {
        return try {
            if (source.isDirectory) {
                moveDirectory(source, dest)
            } else {
                moveFile(source, dest)
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun moveFile(source: File, dest: File) {
        dest.parentFile?.mkdirs()
        if (!source.renameTo(dest)) {
            copyFile(source, dest)
            source.delete()
        }
    }

    private fun moveDirectory(source: File, dest: File) {
        if (!dest.exists()) dest.mkdirs()
        source.listFiles()?.forEach { file ->
            val destFile = File(dest, file.name)
            if (file.isDirectory) {
                moveDirectory(file, destFile)
            } else {
                moveFile(file, destFile)
            }
        }
        source.delete()
    }

    fun readText(file: File, charset: Charset = Charsets.UTF_8): String {
        return file.readText(charset)
    }

    fun readBytes(file: File): ByteArray {
        return file.readBytes()
    }

    fun readLines(file: File, charset: Charset = Charsets.UTF_8): List<String> {
        return file.readLines(charset)
    }

    fun writeText(file: File, text: String, charset: Charset = Charsets.UTF_8) {
        file.parentFile?.mkdirs()
        file.writeText(text, charset)
    }

    fun writeBytes(file: File, bytes: ByteArray) {
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
    }

    fun appendText(file: File, text: String, charset: Charset = Charsets.UTF_8) {
        file.parentFile?.mkdirs()
        file.appendText(text, charset)
    }

    fun appendBytes(file: File, bytes: ByteArray) {
        file.parentFile?.mkdirs()
        file.appendBytes(bytes)
    }

    fun bufferedReader(file: File, charset: Charset = Charsets.UTF_8): BufferedReader {
        return file.bufferedReader(charset)
    }

    fun bufferedWriter(file: File, charset: Charset = Charsets.UTF_8): BufferedWriter {
        return file.bufferedWriter(charset)
    }

    fun printWriter(file: File, charset: Charset = Charsets.UTF_8): java.io.PrintWriter {
        return file.printWriter(charset)
    }

    fun fileReader(file: File, charset: Charset = Charsets.UTF_8): FileReader {
        return FileReader(file, charset)
    }

    fun fileWriter(file: File, charset: Charset = Charsets.UTF_8): FileWriter {
        return FileWriter(file, charset)
    }

    fun inputStream(file: File): InputStream {
        return FileInputStream(file)
    }

    fun outputStream(file: File): OutputStream {
        return FileOutputStream(file)
    }

    fun walk(file: File): Sequence<File> {
        return file.walkTopDown()
    }

    fun walkBottomUp(file: File): Sequence<File> {
        return file.walkBottomUp()
    }

    fun listFiles(file: File, regex: Regex? = null): List<File> {
        return file.listFiles()?.filter { regex?.matches(it.name) ?: true }?.toList() ?: emptyList()
    }

    fun listFilesRecursive(file: File, regex: Regex? = null): List<File> {
        return file.walkTopDown().filter { it.isFile && (regex?.matches(it.name) ?: true) }.toList()
    }

    fun listDirectories(file: File): List<File> {
        return file.listFiles()?.filter { it.isDirectory }?.toList() ?: emptyList()
    }

    fun listDirectoriesRecursive(file: File): List<File> {
        return file.walkTopDown().filter { it.isDirectory }.toList()
    }

    fun findFiles(file: File, predicate: (File) -> Boolean): List<File> {
        return file.walkTopDown().filter(predicate).toList()
    }

    fun findByName(file: File, name: String): File? {
        return file.walkTopDown().find { it.name == name }
    }

    fun findByExtension(file: File, extension: String): List<File> {
        val ext = if (extension.startsWith(".")) extension else ".$extension"
        return file.walkTopDown().filter { it.isFile && it.name.endsWith(ext, ignoreCase = true) }.toList()
    }

    fun findByPattern(file: File, pattern: String): List<File> {
        val regex = pattern.toRegex()
        return file.walkTopDown().filter { it.isFile && regex.matches(it.name) }.toList()
    }

    fun countFiles(file: File): Int {
        return file.walkTopDown().count { it.isFile }
    }

    fun countDirectories(file: File): Int {
        return file.walkTopDown().count { it.isDirectory }
    }

    fun totalSize(file: File): Long {
        return file.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    fun totalFileCount(file: File): Long {
        return file.walkTopDown().count { it.isFile }.toLong()
    }

    fun totalDirectoryCount(file: File): Long {
        return file.walkTopDown().count { it.isDirectory }.toLong()
    }

    fun getExtension(file: File): String {
        return file.extension
    }

    fun getNameWithoutExtension(file: File): String {
        return file.nameWithoutExtension
    }

    fun getBaseName(file: File): String {
        return file.nameWithoutExtension
    }

    fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "txt" -> "text/plain"
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js" -> "application/javascript"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "pdf" -> "application/pdf"
            "zip" -> "application/zip"
            "gz" -> "application/gzip"
            "tar" -> "application/x-tar"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "svg" -> "image/svg+xml"
            "webp" -> "image/webp"
            "ico" -> "image/x-icon"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            "flac" -> "audio/flac"
            "mp4" -> "video/mp4"
            "avi" -> "video/x-msvideo"
            "mkv" -> "video/x-matroska"
            "mov" -> "video/quicktime"
            "wmv" -> "video/x-ms-wmv"
            "flv" -> "video/x-flv"
            "webm" -> "video/webm"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "csv" -> "text/csv"
            "md" -> "text/markdown"
            "apk" -> "application/vnd.android.package-archive"
            else -> "application/octet-stream"
        }
    }

    fun getFileType(file: File): String {
        val mimeType = getMimeType(file)
        return mimeType.substringBefore("/")
    }

    fun isTextFile(file: File): Boolean {
        return getFileType(file) == "text"
    }

    fun isImageFile(file: File): Boolean {
        return getFileType(file) == "image"
    }

    fun isAudioFile(file: File): Boolean {
        return getFileType(file) == "audio"
    }

    fun isVideoFile(file: File): Boolean {
        return getFileType(file) == "video"
    }

    fun isArchiveFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("zip", "gz", "tar", "rar", "7z", "bz2", "xz")
    }

    fun isDocumentFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt", "md", "csv")
    }

    fun isCodeFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("kt", "java", "js", "ts", "py", "rb", "go", "rs", "c", "cpp", "h", "hpp", "cs", "swift", "m", "mm", "php", "pl", "sh", "bash", "zsh", "fish", "ps1", "bat", "cmd", "sql", "html", "htm", "css", "scss", "sass", "less", "xml", "json", "yaml", "yml", "toml", "ini", "cfg", "conf", "properties", "gradle", "cmake", "makefile", "dockerfile")
    }

    fun isConfigFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("json", "yaml", "yml", "toml", "ini", "cfg", "conf", "properties", "xml")
    }

    fun isLogFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("log", "logs") || file.name.contains("log", ignoreCase = true)
    }

    fun isTempFile(file: File): Boolean {
        return file.name.startsWith("tmp") || file.name.startsWith("temp") || file.name.endsWith("~") || file.name.endsWith(".tmp") || file.name.endsWith(".temp")
    }

    fun isBackupFile(file: File): Boolean {
        return file.name.endsWith(".bak") || file.name.endsWith(".backup") || file.name.endsWith(".old") || file.name.endsWith(".orig")
    }

    fun isHiddenFile(file: File): Boolean {
        return file.isHidden || file.name.startsWith(".")
    }

    fun isSymlink(file: File): Boolean {
        return file.absolutePath != file.canonicalPath
    }

    fun isAbsolute(file: File): Boolean = file.isAbsolute

    fun isRelative(file: File): Boolean = !file.isAbsolute

    fun getAbsolutePath(file: File): String = file.absolutePath

    fun getCanonicalPath(file: File): String = file.canonicalPath

    fun getAbsolutePathName(file: File): String = file.absolutePath

    fun getCanonicalPathName(file: File): String = file.canonicalPath

    fun getPath(file: File): String = file.path

    fun getName(file: File): String = file.name

    fun getParent(file: File): String? = file.parent

    fun getParentFile(file: File): File? = file.parentFile

    fun getRoot(file: File): File = file.root

    fun getUsableSpace(file: File): Long = file.usableSpace

    fun getTotalSpace(file: File): Long = file.totalSpace

    fun getFreeSpace(file: File): Long = file.freeSpace

    fun getPartition(file: File): File {
        var current = file.absoluteFile
        while (current.parentFile != null) {
            current = current.parentFile
        }
        return current
    }

    fun getPartitionUsage(file: File): Triple<Long, Long, Long> {
        val total = file.totalSpace
        val free = file.freeSpace
        val used = total - free
        return Triple(total, used, free)
    }

    fun getPartitionUsagePercent(file: File): Double {
        val (total, used, _) = getPartitionUsage(file)
        return if (total == 0L) 0.0 else (used.toDouble() / total.toDouble()) * 100.0
    }

    fun getInodeCount(file: File): Long {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("stat", "-f", file.absolutePath))
            val output = process.inputStream.bufferedReader().readText()
            val match = Regex("Inodes:\\s+(\\d+)").find(output)
            match?.groupValues?.get(1)?.toLongOrNull() ?: -1L
        } catch (e: Exception) {
            -1L
        }
    }

    fun getBlockSize(file: File): Long {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("stat", "-f", file.absolutePath))
            val output = process.inputStream.bufferedReader().readText()
            val match = Regex("Block size:\\s+(\\d+)").find(output)
            match?.groupValues?.get(1)?.toLongOrNull() ?: -1L
        } catch (e: Exception) {
            -1L
        }
    }

    fun getHardLinkCount(file: File): Int {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("stat", file.absolutePath))
            val output = process.inputStream.bufferedReader().readText()
            val match = Regex("Links:\\s+(\\d+)").find(output)
            match?.groupValues?.get(1)?.toIntOrNull() ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    fun getUserId(file: File): Int {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("stat", "-c", "%u", file.absolutePath))
            val output = process.inputStream.bufferedReader().readText().trim()
            output.toIntOrNull() ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    fun getGroupId(file: File): Int {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("stat", "-c", "%g", file.absolutePath))
            val output = process.inputStream.bufferedReader().readText().trim()
            output.toIntOrNull() ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    fun getPermissions(file: File): String {
        val sb = StringBuilder()
        sb.append(if (file.isDirectory) "d" else "-")
        sb.append(if (file.canRead()) "r" else "-")
        sb.append(if (file.canWrite()) "w" else "-")
        sb.append(if (file.canExecute()) "x" else "-")
        return sb.toString()
    }

    fun getOctalPermissions(file: File): String {
        var perms = 0
        if (file.canRead()) perms += 4
        if (file.canWrite()) perms += 2
        if (file.canExecute()) perms += 1
        return perms.toString()
    }

    fun setPermissions(file: File, readable: Boolean, writable: Boolean, executable: Boolean): Boolean {
        return file.setReadable(readable) && file.setWritable(writable) && file.setExecutable(executable)
    }

    fun setReadOnly(file: File): Boolean {
        return file.setReadOnly()
    }

    fun setWritable(file: File, writable: Boolean): Boolean {
        return file.setWritable(writable)
    }

    fun setExecutable(file: File, executable: Boolean): Boolean {
        return file.setExecutable(executable)
    }

    fun touch(file: File): Boolean {
        return try {
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            } else {
                file.setLastModified(System.currentTimeMillis())
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun truncate(file: File): Boolean {
        return try {
            FileOutputStream(file).use { it.channel.truncate(0) }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun appendLine(file: File, line: String) {
        appendText(file, "$line\n")
    }

    fun appendLines(file: File, lines: List<String>) {
        appendText(file, lines.joinToString("\n", postfix = "\n"))
    }

    fun prependText(file: File, text: String) {
        val existing = if (file.exists()) readText(file) else ""
        writeText(file, text + existing)
    }

    fun prependLine(file: File, line: String) {
        prependText(file, "$line\n")
    }

    fun prependLines(file: File, lines: List<String>) {
        prependText(file, lines.joinToString("\n", postfix = "\n"))
    }

    fun insertLine(file: File, lineNumber: Int, line: String) {
        val lines = readLines(file).toMutableList()
        val index = lineNumber.coerceIn(0, lines.size)
        lines.add(index, line)
        writeText(file, lines.joinToString("\n"))
    }

    fun insertLines(file: File, lineNumber: Int, lines: List<String>) {
        val existingLines = readLines(file).toMutableList()
        val index = lineNumber.coerceIn(0, existingLines.size)
        existingLines.addAll(index, lines)
        writeText(file, existingLines.joinToString("\n"))
    }

    fun removeLine(file: File, lineNumber: Int) {
        val lines = readLines(file).toMutableList()
        if (lineNumber in lines.indices) {
            lines.removeAt(lineNumber)
            writeText(file, lines.joinToString("\n"))
        }
    }

    fun removeLines(file: File, predicate: (String) -> Boolean) {
        val lines = readLines(file).filter { !predicate(it) }
        writeText(file, lines.joinToString("\n"))
    }

    fun replaceLine(file: File, lineNumber: Int, newLine: String) {
        val lines = readLines(file).toMutableList()
        if (lineNumber in lines.indices) {
            lines[lineNumber] = newLine
            writeText(file, lines.joinToString("\n"))
        }
    }

    fun replaceLines(file: File, regex: Regex, replacement: String) {
        val content = readText(file)
        val newContent = content.replace(regex, replacement)
        writeText(file, newContent)
    }

    fun replaceAll(file: File, oldText: String, newText: String) {
        val content = readText(file)
        val newContent = content.replace(oldText, newText)
        writeText(file, newContent)
    }

    fun findAndReplace(file: File, regex: Regex, transform: (MatchResult) -> String) {
        val content = readText(file)
        val newContent = regex.replace(content, transform)
        writeText(file, newContent)
    }

    fun grep(file: File, pattern: String): List<String> {
        val regex = pattern.toRegex()
        return readLines(file).filter { regex.containsMatchIn(it) }
    }

    fun grep(file: File, regex: Regex): List<String> {
        return readLines(file).filter { regex.containsMatchIn(it) }
    }

    fun grepWithLineNumbers(file: File, pattern: String): List<Pair<Int, String>> {
        val regex = pattern.toRegex()
        return readLines(file).mapIndexedNotNull { index, line ->
            if (regex.containsMatchIn(line)) Pair(index + 1, line) else null
        }
    }

    fun grepWithLineNumbers(file: File, regex: Regex): List<Pair<Int, String>> {
        return readLines(file).mapIndexedNotNull { index, line ->
            if (regex.containsMatchIn(line)) Pair(index + 1, line) else null
        }
    }

    fun grepCount(file: File, pattern: String): Int {
        val regex = pattern.toRegex()
        return readLines(file).count { regex.containsMatchIn(it) }
    }

    fun grepCount(file: File, regex: Regex): Int {
        return readLines(file).count { regex.containsMatchIn(it) }
    }

    fun grepContext(file: File, pattern: String, contextLines: Int = 3): List<String> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepContext(file: File, regex: Regex, contextLines: Int = 3): List<String> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepInverted(file: File, pattern: String): List<String> {
        val regex = pattern.toRegex()
        return readLines(file).filter { !regex.containsMatchIn(it) }
    }

    fun grepInverted(file: File, regex: Regex): List<String> {
        return readLines(file).filter { !regex.containsMatchIn(it) }
    }

    fun grepInvertedCount(file: File, pattern: String): Int {
        val regex = pattern.toRegex()
        return readLines(file).count { !regex.containsMatchIn(it) }
    }

    fun grepInvertedCount(file: File, regex: Regex): Int {
        return readLines(file).count { !regex.containsMatchIn(it) }
    }

    fun grepInvertedWithLineNumbers(file: File, pattern: String): List<Pair<Int, String>> {
        val regex = pattern.toRegex()
        return readLines(file).mapIndexedNotNull { index, line ->
            if (!regex.containsMatchIn(line)) Pair(index + 1, line) else null
        }
    }

    fun grepInvertedWithLineNumbers(file: File, regex: Regex): List<Pair<Int, String>> {
        return readLines(file).mapIndexedNotNull { index, line ->
            if (!regex.containsMatchIn(line)) Pair(index + 1, line) else null
        }
    }

    fun grepInvertedContext(file: File, pattern: String, contextLines: Int = 3): List<String> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepInvertedContext(file: File, regex: Regex, contextLines: Int = 3): List<String> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepInvertedContextWithLineNumbers(file: File, pattern: String, contextLines: Int = 3): List<String> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepInvertedContextWithLineNumbers(file: File, regex: Regex, contextLines: Int = 3): List<String> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            }
        }
        return result
    }

    fun grepInvertedContextWithLineNumbersAndStats(file: File, pattern: String, contextLines: Int = 3): Pair<List<String>, Int> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        return Pair(result, matchCount)
    }

    fun grepInvertedContextWithLineNumbersAndStats(file: File, regex: Regex, contextLines: Int = 3): Pair<List<String>, Int> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        return Pair(result, matchCount)
    }

    fun grepInvertedContextWithLineNumbersAndStatsAndSummary(file: File, pattern: String, contextLines: Int = 3): Triple<List<String>, Int, String> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        val summary = "Found $matchCount matches in ${lines.size} lines"
        return Triple(result, matchCount, summary)
    }

    fun grepInvertedContextWithLineNumbersAndStatsAndSummary(file: File, regex: Regex, contextLines: Int = 3): Triple<List<String>, Int, String> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        val summary = "Found $matchCount matches in ${lines.size} lines"
        return Triple(result, matchCount, summary)
    }

    fun grepInvertedContextWithLineNumbersAndStatsAndSummaryAndMetadata(file: File, pattern: String, contextLines: Int = 3): Quadruple<List<String>, Int, String, Map<String, Any>> {
        val regex = pattern.toRegex()
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        val summary = "Found $matchCount matches in ${lines.size} lines"
        val metadata = mapOf(
            "file" to file.absolutePath,
            "pattern" to pattern,
            "totalLines" to lines.size,
            "matchCount" to matchCount,
            "contextLines" to contextLines
        )
        return Quadruple(result, matchCount, summary, metadata)
    }

    fun grepInvertedContextWithLineNumbersAndStatsAndSummaryAndMetadata(file: File, regex: Regex, contextLines: Int = 3): Quadruple<List<String>, Int, String, Map<String, Any>> {
        val lines = readLines(file)
        val result = mutableListOf<String>()
        var matchCount = 0
        for (i in lines.indices) {
            if (!regex.containsMatchIn(lines[i])) {
                val start = (i - contextLines).coerceAtLeast(0)
                val end = (i + contextLines).coerceAtMost(lines.size - 1)
                for (j in start..end) {
                    result.add("${j + 1}: ${lines[j]}")
                }
                result.add("--")
            } else {
                matchCount++
            }
        }
        val summary = "Found $matchCount matches in ${lines.size} lines"
        val metadata = mapOf(
            "file" to file.absolutePath,
            "pattern" to regex.pattern,
            "totalLines" to lines.size,
            "matchCount" to matchCount,
            "contextLines" to contextLines
        )
        return Quadruple(result, matchCount, summary, metadata)
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    fun md5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun sha1(file: File): String {
        val md = MessageDigest.getInstance("SHA-1")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun sha256(file: File): String {
        val md = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun sha512(file: File): String {
        val md = MessageDigest.getInstance("SHA-512")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun crc32(file: File): Long {
        val crc = java.util.zip.CRC32()
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                crc.update(buffer, 0, bytesRead)
            }
        }
        return crc.value
    }

    fun adler32(file: File): Long {
        val adler = java.util.zip.Adler32()
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                adler.update(buffer, 0, bytesRead)
            }
        }
        return adler.value
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < KB -> "$bytes B"
            bytes < MB -> "%.2f KB".format(bytes.toDouble() / KB)
            bytes < GB -> "%.2f MB".format(bytes.toDouble() / MB)
            bytes < TB -> "%.2f GB".format(bytes.toDouble() / GB)
            else -> "%.2f TB".format(bytes.toDouble() / TB)
        }
    }

    fun parseFileSize(sizeStr: String): Long {
        val upper = sizeStr.uppercase().trim()
        val number = upper.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: return 0L
        return when {
            upper.endsWith("TB") || upper.endsWith("T") -> (number * TB).toLong()
            upper.endsWith("GB") || upper.endsWith("G") -> (number * GB).toLong()
            upper.endsWith("MB") || upper.endsWith("M") -> (number * MB).toLong()
            upper.endsWith("KB") || upper.endsWith("K") -> (number * KB).toLong()
            upper.endsWith("B") -> number.toLong()
            else -> number.toLong()
        }
    }

    fun getLineCount(file: File): Int {
        return file.readLines().size
    }

    fun getWordCount(file: File): Int {
        return file.readText().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    }

    fun getCharacterCount(file: File): Int {
        return file.readText().length
    }

    fun getCharacterCountWithoutSpaces(file: File): Int {
        return file.readText().count { !it.isWhitespace() }
    }

    fun getByteCount(file: File): Long {
        return file.length()
    }

    fun getNonEmptyLineCount(file: File): Int {
        return file.readLines().count { it.isNotBlank() }
    }

    fun getEmptyLineCount(file: File): Int {
        return file.readLines().count { it.isBlank() }
    }

    fun getLongestLine(file: File): String? {
        return file.readLines().maxByOrNull { it.length }
    }

    fun getShortestLine(file: File): String? {
        return file.readLines().filter { it.isNotBlank() }.minByOrNull { it.length }
    }

    fun getAverageLineLength(file: File): Double {
        val lines = file.readLines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return 0.0
        return lines.sumOf { it.length }.toDouble() / lines.size
    }

    fun getMedianLineLength(file: File): Double {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }.sorted()
        if (lengths.isEmpty()) return 0.0
        val mid = lengths.size / 2
        return if (lengths.size % 2 == 0) {
            (lengths[mid - 1] + lengths[mid]) / 2.0
        } else {
            lengths[mid].toDouble()
        }
    }

    fun getModeLineLength(file: File): Int? {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.isEmpty()) return null
        val frequency = lengths.groupingBy { it }.eachCount()
        return frequency.maxByOrNull { it.value }?.key
    }

    fun getLineLengthDistribution(file: File): Map<Int, Int> {
        return file.readLines().filter { it.isNotBlank() }.groupingBy { it.length }.eachCount()
    }

    fun getLineLengthHistogram(file: File, binSize: Int = 10): Map<IntRange, Int> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.isEmpty()) return emptyMap()
        val maxLen = lengths.max()
        val bins = mutableMapOf<IntRange, Int>()
        var start = 0
        while (start <= maxLen) {
            val end = start + binSize - 1
            val count = lengths.count { it in start..end }
            if (count > 0) {
                bins[start..end] = count
            }
            start += binSize
        }
        return bins
    }

    fun getLineLengthStats(file: File): Map<String, Any> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.isEmpty()) return emptyMap()
        val sorted = lengths.sorted()
        val mid = sorted.size / 2
        val median = if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid].toDouble()
        }
        val mean = lengths.average()
        val variance = lengths.sumOf { (it - mean) * (it - mean) } / lengths.size
        val stdDev = kotlin.math.sqrt(variance)
        return mapOf(
            "count" to lengths.size,
            "min" to sorted.first(),
            "max" to sorted.last(),
            "mean" to mean,
            "median" to median,
            "mode" to (getModeLineLength(file) ?: 0),
            "variance" to variance,
            "stdDev" to stdDev,
            "sum" to lengths.sum()
        )
    }

    fun getLineLengthPercentiles(file: File): Map<String, Double> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }.sorted()
        if (lengths.isEmpty()) return emptyMap()
        fun percentile(p: Double): Double {
            val index = (p / 100.0 * (lengths.size - 1)).toInt()
            return lengths[index].toDouble()
        }
        return mapOf(
            "p25" to percentile(25.0),
            "p50" to percentile(50.0),
            "p75" to percentile(75.0),
            "p90" to percentile(90.0),
            "p95" to percentile(95.0),
            "p99" to percentile(99.0)
        )
    }

    fun getLineLengthOutliers(file: File): List<Int> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.size < 4) return emptyList()
        val sorted = lengths.sorted()
        val q1Index = (sorted.size * 0.25).toInt()
        val q3Index = (sorted.size * 0.75).toInt()
        val q1 = sorted[q1Index].toDouble()
        val q3 = sorted[q3Index].toDouble()
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        return lengths.filter { it < lowerBound || it > upperBound }
    }

    fun getLineLengthZScores(file: File): List<Double> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.size < 2) return emptyList()
        val mean = lengths.average()
        val variance = lengths.sumOf { (it - mean) * (it - mean) } / lengths.size
        val stdDev = kotlin.math.sqrt(variance)
        if (stdDev == 0.0) return lengths.map { 0.0 }
        return lengths.map { (it - mean) / stdDev }
    }

    fun getLineLengthModifiedZScores(file: File): List<Double> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.size < 2) return emptyList()
        val sorted = lengths.sorted()
        val mid = sorted.size / 2
        val median = if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid].toDouble()
        }
        val absoluteDeviations = lengths.map { kotlin.math.abs(it - median) }
        val sortedDeviations = absoluteDeviations.sorted()
        val midDev = sortedDeviations.size / 2
        val mad = if (sortedDeviations.size % 2 == 0) {
            (sortedDeviations[midDev - 1] + sortedDeviations[midDev]) / 2.0
        } else {
            sortedDeviations[midDev].toDouble()
        }
        if (mad == 0.0) return lengths.map { 0.0 }
        return lengths.map { 0.6745 * (it - median) / mad }
    }

    fun getLineLengthRobustStats(file: File): Map<String, Double> {
        val lengths = file.readLines().filter { it.isNotBlank() }.map { it.length }
        if (lengths.isEmpty()) return emptyMap()
        val sorted = lengths.sorted()
        val mid = sorted.size / 2
        val median = if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid].toDouble()
        }
        val absoluteDeviations = lengths.map { kotlin.math.abs(it - median) }
        val sortedDeviations = absoluteDeviations.sorted()
        val midDev = sortedDeviations.size / 2
        val mad = if (sortedDeviations.size % 2 == 0) {
            (sortedDeviations[midDev - 1] + sortedDeviations[midDev]) / 2.0
        } else {
            sortedDeviations[midDev].toDouble()
        }
        val q1Index = (sorted.size * 0.25).toInt()
        val q3Index = (sorted.size * 0.75).toInt()
        val q1 = sorted[q1Index].toDouble()
        val q3 = sorted[q3Index].toDouble()
        val iqr = q3 - q1
        return mapOf(
            "median" to median,
            "mad" to mad,
            "q1" to q1,
            "q3" to q3,
            "iqr" to iqr,
            "lowerFence" to (q1 - 1.5 * iqr),
            "upperFence" to (q3 + 1.5 * iqr)
        )
    }

    fun getLineLengthSummary(file: File): String {
        val stats = getLineLengthStats(file)
        val percentiles = getLineLengthPercentiles(file)
        val robustStats = getLineLengthRobustStats(file)
        return buildString {
            appendLine("Line Length Summary for ${file.name}")
            appendLine("=" .repeat(50))
            appendLine("Total lines: ${stats["count"]}")
            appendLine("Min length: ${stats["min"]}")
            appendLine("Max length: ${stats["max"]}")
            appendLine("Mean length: ${"%.2f".format(stats["mean"])}")
            appendLine("Median length: ${"%.2f".format(stats["median"])}")
            appendLine("Mode length: ${stats["mode"]}")
            appendLine("Std Dev: ${"%.2f".format(stats["stdDev"])}")
            appendLine("Variance: ${"%.2f".format(stats["variance"])}")
            appendLine("Sum: ${stats["sum"]}")
            appendLine()
            appendLine("Percentiles:")
            appendLine("  25th: ${"%.2f".format(percentiles["p25"])}")
            appendLine("  50th: ${"%.2f".format(percentiles["p50"])}")
            appendLine("  75th: ${"%.2f".format(percentiles["p75"])}")
            appendLine("  90th: ${"%.2f".format(percentiles["p90"])}")
            appendLine("  95th: ${"%.2f".format(percentiles["p95"])}")
            appendLine("  99th: ${"%.2f".format(percentiles["p99"])}")
            appendLine()
            appendLine("Robust Statistics:")
            appendLine("  MAD: ${"%.2f".format(robustStats["mad"])}")
            appendLine("  IQR: ${"%.2f".format(robustStats["iqr"])}")
            appendLine("  Lower fence: ${"%.2f".format(robustStats["lowerFence"])}")
            appendLine("  Upper fence: ${"%.2f".format(robustStats["upperFence"])}")
        }
    }

    fun getLineLengthReport(file: File): String {
        val summary = getLineLengthSummary(file)
        val outliers = getLineLengthOutliers(file)
        val zScores = getLineLengthZScores(file)
        val modifiedZScores = getLineLengthModifiedZScores(file)
        val histogram = getLineLengthHistogram(file)
        return buildString {
            appendLine(summary)
            appendLine()
            appendLine("Outliers: ${outliers.size}")
            if (outliers.isNotEmpty()) {
                appendLine("  Values: ${outliers.joinToString(", ")}")
            }
            appendLine()
            appendLine("Z-Scores (>2 or <-2):")
            val extremeZScores = zScores.withIndex().filter { kotlin.math.abs(it.value) > 2 }
            if (extremeZScores.isNotEmpty()) {
                extremeZScores.forEach { (index, z) ->
                    appendLine("  Line ${index + 1}: z = ${"%.2f".format(z)}")
                }
            } else {
                appendLine("  None")
            }
            appendLine()
            appendLine("Modified Z-Scores (>3.5 or <-3.5):")
            val extremeModZScores = modifiedZScores.withIndex().filter { kotlin.math.abs(it.value) > 3.5 }
            if (extremeModZScores.isNotEmpty()) {
                extremeModZScores.forEach { (index, z) ->
                    appendLine("  Line ${index + 1}: modified z = ${"%.2f".format(z)}")
                }
            } else {
                appendLine("  None")
            }
            appendLine()
            appendLine("Histogram:")
            histogram.forEach { (range, count) ->
                appendLine("  ${range.first}-${range.last}: $count")
            }
        }
    }

    fun getLineLengthAnalysis(file: File): Map<String, Any> {
        val stats = getLineLengthStats(file)
        val percentiles = getLineLengthPercentiles(file)
        val robustStats = getLineLengthRobustStats(file)
        val outliers = getLineLengthOutliers(file)
        val zScores = getLineLengthZScores(file)
        val modifiedZScores = getLineLengthModifiedZScores(file)
        val histogram = getLineLengthHistogram(file)
        val distribution = getLineLengthDistribution(file)
        return mapOf(
            "stats" to stats,
            "percentiles" to percentiles,
            "robustStats" to robustStats,
            "outliers" to outliers,
            "zScores" to zScores,
            "modifiedZScores" to modifiedZScores,
            "histogram" to histogram,
            "distribution" to distribution
        )
    }

    fun getLineLengthVisualization(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "█".repeat(barLength)
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationAscii(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "#".repeat(barLength)
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationUnicode(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▓".repeat(barLength)
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBar(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "█".repeat(barLength) + "░".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDot(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "●".repeat(barLength) + "○".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationStar(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "★".repeat(barLength) + "☆".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationHeart(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "♥".repeat(barLength) + "♡".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDiamond(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "◆".repeat(barLength) + "◇".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCircle(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "◉".repeat(barLength) + "◎".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSquare(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "■".repeat(barLength) + "□".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationTriangle(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▲".repeat(barLength) + "△".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationArrow(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "►".repeat(barLength) + "▷".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCross(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✖".repeat(barLength) + "✗".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPlus(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✚".repeat(barLength) + "✞".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMinus(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "━".repeat(barLength) + "─".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationEquals(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "═".repeat(barLength) + "=".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPipe(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "┃".repeat(barLength) + "│".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationColon(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▌".repeat(barLength) + "▏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSemicolon(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▋".repeat(barLength) + "▎".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationComma(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▊".repeat(barLength) + "▍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPeriod(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▉".repeat(barLength) + "▌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSlash(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▐".repeat(barLength) + "▌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBackslash(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▐".repeat(barLength) + "▌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationTilde(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▔".repeat(barLength) + "▁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationUnderscore(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▁".repeat(barLength) + "▂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOverline(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▔".repeat(barLength) + "▕".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationUnderline(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "▁".repeat(barLength) + "▏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDoubleLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "═".repeat(barLength) + "─".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDottedLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "┅".repeat(barLength) + "┄".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDashedLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "┄".repeat(barLength) + "┈".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDashDotLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "╌".repeat(barLength) + "╄".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationDashDotDotLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "╌".repeat(barLength) + "╆".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLongDash(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "╾".repeat(barLength) + "╼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationWaveLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "∿".repeat(barLength) + "∼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationZigzagLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "⋎".repeat(barLength) + "⋏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "꩜".repeat(barLength) + "꩛".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLoopLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "➰".repeat(barLength) + "➿".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationChainLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "⛓".repeat(barLength) + "🔗".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLinkLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🔗".repeat(barLength) + "🔗".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPaperclipLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📎".repeat(barLength) + "📎".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPushpinLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📌".repeat(barLength) + "📌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationRoundPushpinLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📍".repeat(barLength) + "📍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationStraightRulerLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📏".repeat(barLength) + "📏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationTriangularRulerLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📐".repeat(barLength) + "📐".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationClipboardLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📋".repeat(barLength) + "📋".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCalendarLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📅".repeat(barLength) + "📅".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationTearOffCalendarLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗓".repeat(barLength) + "🗓".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralCalendarLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗓".repeat(barLength) + "🗓".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardIndexLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📇".repeat(barLength) + "📇".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardIndexDividersLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗂".repeat(barLength) + "🗂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFileFolderLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📁".repeat(barLength) + "📁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOpenFileFolderLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📂".repeat(barLength) + "📂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardFileBoxLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗃".repeat(barLength) + "🗃".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFileCabinetLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗄".repeat(barLength) + "🗄".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationWastebasketLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗑".repeat(barLength) + "🗑".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralNotepadLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗒".repeat(barLength) + "🗒".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralCalendarLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗓".repeat(barLength) + "🗓".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPencilLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✏".repeat(barLength) + "✏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBlackNibLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✒".repeat(barLength) + "✒".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLowerLeftFountainPenLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖋".repeat(barLength) + "🖋".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLowerLeftBallpointPenLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖊".repeat(barLength) + "🖊".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLowerLeftPaintbrushLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖌".repeat(barLength) + "🖌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationLowerLeftCrayonLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖍".repeat(barLength) + "🖍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBriefcaseLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "💼".repeat(barLength) + "💼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFolderLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📁".repeat(barLength) + "📁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOpenFolderLine(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📂".repeat(barLength) + "📂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardIndexDividersLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗂".repeat(barLength) + "🗂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCalendarLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📅".repeat(barLength) + "📅".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralNotepadLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗒".repeat(barLength) + "🗒".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationSpiralCalendarLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗓".repeat(barLength) + "🗓".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationWastebasketLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗑".repeat(barLength) + "🗑".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFileCabinetLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗄".repeat(barLength) + "🗄".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardFileBoxLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗃".repeat(barLength) + "🗃".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOpenFileFolderLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📂".repeat(barLength) + "📂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFolderLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📁".repeat(barLength) + "📁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBriefcaseLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "💼".repeat(barLength) + "💼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCrayonLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖍".repeat(barLength) + "🖍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPaintbrushLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖌".repeat(barLength) + "🖌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBallpointPenLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖊".repeat(barLength) + "🖊".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFountainPenLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖋".repeat(barLength) + "🖋".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBlackNibLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✒".repeat(barLength) + "✒".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPencilLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✏".repeat(barLength) + "✏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCalendarLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📅".repeat(barLength) + "📅".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardIndexDividersLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗂".repeat(barLength) + "🗂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOpenFolderLine2(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📂".repeat(barLength) + "📂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFolderLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📁".repeat(barLength) + "📁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBriefcaseLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "💼".repeat(barLength) + "💼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine5(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCrayonLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖍".repeat(barLength) + "🖍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPaintbrushLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖌".repeat(barLength) + "🖌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBallpointPenLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖊".repeat(barLength) + "🖊".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFountainPenLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖋".repeat(barLength) + "🖋".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBlackNibLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✒".repeat(barLength) + "✒".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPencilLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "✏".repeat(barLength) + "✏".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine6(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCalendarLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📅".repeat(barLength) + "📅".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCardIndexDividersLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🗂".repeat(barLength) + "🗂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationOpenFolderLine3(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📂".repeat(barLength) + "📂".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationFolderLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📁".repeat(barLength) + "📁".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBriefcaseLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "💼".repeat(barLength) + "💼".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationMemoLine7(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "📝".repeat(barLength) + "📝".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationCrayonLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖍".repeat(barLength) + "🖍".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationPaintbrushLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat(width))
        histogram.forEach { (range, count) ->
            val barLength = (count.toDouble() / maxCount * (width - 30)).toInt()
            val bar = "🖌".repeat(barLength) + "🖌".repeat((width - 30 - barLength).coerceAtLeast(0))
            sb.appendLine("${range.first.toString().padStart(4)}-${range.last.toString().padEnd(4)} | $bar $count")
        }
        return sb.toString()
    }

    fun getLineLengthVisualizationBallpointPenLine4(file: File, width: Int = 80): String {
        val histogram = getLineLengthHistogram(file)
        if (histogram.isEmpty()) return "No data"
        val maxCount = histogram.values.max()
        val sb = StringBuilder()
        sb.appendLine("Line Length Histogram for ${file.name}")
        sb.appendLine("=".repeat