package com.mweshimiwa.assistant.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object CompressionUtils {

    fun compressGzip(data: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(data) }
        return bos.toByteArray()
    }

    fun decompressGzip(data: ByteArray): ByteArray {
        val bis = ByteArrayInputStream(data)
        return GZIPInputStream(bis).use { it.readBytes() }
    }

    fun compressGzipFile(inputFile: File, outputFile: File): Boolean {
        return try {
            FileInputStream(inputFile).use { fis ->
                GZIPOutputStream(FileOutputStream(outputFile)).use { gzos ->
                    fis.copyTo(gzos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decompressGzipFile(inputFile: File, outputFile: File): Boolean {
        return try {
            GZIPInputStream(FileInputStream(inputFile)).use { gzis ->
                FileOutputStream(outputFile).use { fos ->
                    gzis.copyTo(fos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun compressDeflate(data: ByteArray, level: Int = Deflater.DEFAULT_COMPRESSION): ByteArray {
        val deflater = Deflater(level)
        deflater.setInput(data)
        deflater.finish()
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            bos.write(buffer, 0, count)
        }
        deflater.end()
        return bos.toByteArray()
    }

    fun decompressDeflate(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            bos.write(buffer, 0, count)
        }
        inflater.end()
        return bos.toByteArray()
    }

    fun compressDeflateFile(inputFile: File, outputFile: File, level: Int = Deflater.DEFAULT_COMPRESSION): Boolean {
        return try {
            FileInputStream(inputFile).use { fis ->
                DeflaterOutputStream(FileOutputStream(outputFile), Deflater(level)).use { dos ->
                    fis.copyTo(dos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decompressDeflateFile(inputFile: File, outputFile: File): Boolean {
        return try {
            InflaterInputStream(FileInputStream(inputFile)).use { iis ->
                FileOutputStream(outputFile).use { fos ->
                    iis.copyTo(fos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun compressZip(files: List<File>, outputFile: File): Boolean {
        return try {
            ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                for (file in files) {
                    if (file.isFile) {
                        val entry = ZipEntry(file.name)
                        zos.putNextEntry(entry)
                        file.inputStream().use { it.copyTo(zos) }
                        zos.closeEntry()
                    } else if (file.isDirectory) {
                        addDirectoryToZip(file, file.name, zos)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun addDirectoryToZip(dir: File, parentName: String, zos: ZipOutputStream) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isFile) {
                val entryName = if (parentName.isEmpty()) file.name else "$parentName/${file.name}"
                val entry = ZipEntry(entryName)
                zos.putNextEntry(entry)
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            } else if (file.isDirectory) {
                val dirName = if (parentName.isEmpty()) file.name else "$parentName/${file.name}"
                addDirectoryToZip(file, dirName, zos)
            }
        }
    }

    fun compressZipFile(inputFile: File, outputFile: File): Boolean {
        return try {
            ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                if (inputFile.isFile) {
                    val entry = ZipEntry(inputFile.name)
                    zos.putNextEntry(entry)
                    inputFile.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                } else if (inputFile.isDirectory) {
                    addDirectoryToZip(inputFile, "", zos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decompressZip(inputFile: File, outputDir: File): Boolean {
        return try {
            if (!outputDir.exists()) outputDir.mkdirs()
            ZipInputStream(FileInputStream(inputFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val file = File(outputDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decompressZipEntry(inputFile: File, entryName: String): ByteArray? {
        return try {
            ZipInputStream(FileInputStream(inputFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.name == entryName) {
                        return zis.readBytes()
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun listZipEntries(inputFile: File): List<String> {
        val entries = mutableListOf<String>()
        try {
            ZipInputStream(FileInputStream(inputFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    entries.add(entry.name)
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
        }
        return entries
    }

    fun getZipEntryInfo(inputFile: File, entryName: String): Map<String, Any>? {
        return try {
            ZipInputStream(FileInputStream(inputFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.name == entryName) {
                        return mapOf(
                            "name" to entry.name,
                            "size" to entry.size,
                            "compressedSize" to entry.compressedSize,
                            "time" to entry.time,
                            "crc" to entry.crc,
                            "method" to entry.method,
                            "isDirectory" to entry.isDirectory
                        )
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun addFileToZip(zipFile: File, fileToAdd: File, entryName: String? = null): Boolean {
        return try {
            val tempFile = File(zipFile.parent, zipFile.name + ".tmp")
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                ZipOutputStream(FileOutputStream(tempFile)).use { zos ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        zos.putNextEntry(ZipEntry(entry.name))
                        zis.copyTo(zos)
                        zos.closeEntry()
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                    val newEntryName = entryName ?: fileToAdd.name
                    zos.putNextEntry(ZipEntry(newEntryName))
                    fileToAdd.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
            }
            zipFile.delete()
            tempFile.renameTo(zipFile)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeFileFromZip(zipFile: File, entryName: String): Boolean {
        return try {
            val tempFile = File(zipFile.parent, zipFile.name + ".tmp")
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                ZipOutputStream(FileOutputStream(tempFile)).use { zos ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        if (entry.name != entryName) {
                            zos.putNextEntry(ZipEntry(entry.name))
                            zis.copyTo(zos)
                            zos.closeEntry()
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
            zipFile.delete()
            tempFile.renameTo(zipFile)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateZipEntry(zipFile: File, entryName: String, newData: ByteArray): Boolean {
        return try {
            val tempFile = File(zipFile.parent, zipFile.name + ".tmp")
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                ZipOutputStream(FileOutputStream(tempFile)).use { zos ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        if (entry.name == entryName) {
                            zos.putNextEntry(ZipEntry(entryName))
                            zos.write(newData)
                        } else {
                            zos.putNextEntry(ZipEntry(entry.name))
                            zis.copyTo(zos)
                        }
                        zos.closeEntry()
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
            zipFile.delete()
            tempFile.renameTo(zipFile)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun mergeZips(zipFiles: List<File>, outputFile: File): Boolean {
        return try {
            ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                val addedEntries = mutableSetOf<String>()
                for (zipFile in zipFiles) {
                    ZipInputStream(FileInputStream(zipFile)).use { zis ->
                        var entry: ZipEntry? = zis.nextEntry
                        while (entry != null) {
                            if (!addedEntries.contains(entry.name)) {
                                zos.putNextEntry(ZipEntry(entry.name))
                                zis.copyTo(zos)
                                zos.closeEntry()
                                addedEntries.add(entry.name)
                            }
                            zis.closeEntry()
                            entry = zis.nextEntry
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun splitZip(zipFile: File, outputDir: File, maxEntrySize: Long): List<File> {
        val parts = mutableListOf<File>()
        try {
            if (!outputDir.exists()) outputDir.mkdirs()
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size > maxEntrySize) {
                        val partFile = File(outputDir, "${entry.name}.part")
                        FileOutputStream(partFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        parts.add(partFile)
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
        }
        return parts
    }

    fun getCompressionRatio(originalSize: Long, compressedSize: Long): Double {
        return if (originalSize == 0L) 0.0 else compressedSize.toDouble() / originalSize.toDouble()
    }

    fun getCompressionSavings(originalSize: Long, compressedSize: Long): Long {
        return originalSize - compressedSize
    }

    fun getCompressionSavingsPercent(originalSize: Long, compressedSize: Long): Double {
        return if (originalSize == 0L) 0.0 else (1.0 - compressedSize.toDouble() / originalSize.toDouble()) * 100.0
    }

    fun isGzipFile(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val header = ByteArray(2)
                fis.read(header)
                header[0] == 0x1F.toByte() && header[1] == 0x8B.toByte()
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isZipFile(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val header = ByteArray(4)
                fis.read(header)
                header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isCompressedFile(file: File): Boolean {
        return isGzipFile(file) || isZipFile(file)
    }

    fun getCompressedSize(file: File): Long {
        return try {
            when {
                isGzipFile(file) -> {
                    GZIPInputStream(FileInputStream(file)).use { gzis ->
                        gzis.readBytes().size.toLong()
                    }
                }
                isZipFile(file) -> {
                    var totalSize = 0L
                    ZipInputStream(FileInputStream(file)).use { zis ->
                        var entry: ZipEntry? = zis.nextEntry
                        while (entry != null) {
                            totalSize += entry.size
                            zis.closeEntry()
                            entry = zis.nextEntry
                        }
                    }
                    totalSize
                }
                else -> file.length()
            }
        } catch (e: Exception) {
            file.length()
        }
    }

    fun getUncompressedSize(file: File): Long {
        return try {
            when {
                isGzipFile(file) -> {
                    GZIPInputStream(FileInputStream(file)).use { gzis ->
                        gzis.readBytes().size.toLong()
                    }
                }
                isZipFile(file) -> {
                    var totalSize = 0L
                    ZipInputStream(FileInputStream(file)).use { zis ->
                        var entry: ZipEntry? = zis.nextEntry
                        while (entry != null) {
                            totalSize += entry.size
                            zis.closeEntry()
                            entry = zis.nextEntry
                        }
                    }
                    totalSize
                }
                else -> file.length()
            }
        } catch (e: Exception) {
            file.length()
        }
    }

    fun compressFile(inputFile: File, outputFile: File, method: String = "gzip"): Boolean {
        return when (method.lowercase()) {
            "gzip" -> compressGzipFile(inputFile, outputFile)
            "deflate" -> compressDeflateFile(inputFile, outputFile)
            "zip" -> compressZipFile(inputFile, outputFile)
            else -> false
        }
    }

    fun decompressFile(inputFile: File, outputFile: File, method: String = "gzip"): Boolean {
        return when (method.lowercase()) {
            "gzip" -> decompressGzipFile(inputFile, outputFile)
            "deflate" -> decompressDeflateFile(inputFile, outputFile)
            "zip" -> decompressZip(inputFile, outputFile.parentFile)
            else -> false
        }
    }

    fun compressDirectory(inputDir: File, outputFile: File): Boolean {
        return try {
            val files = inputDir.walkTopDown().filter { it.isFile }.toList()
            compressZip(files, outputFile)
        } catch (e: Exception) {
            false
        }
    }

    fun decompressToDirectory(inputFile: File, outputDir: File): Boolean {
        return decompressZip(inputFile, outputDir)
    }

    fun compressBytes(data: ByteArray, method: String = "gzip"): ByteArray {
        return when (method.lowercase()) {
            "gzip" -> compressGzip(data)
            "deflate" -> compressDeflate(data)
            else -> data
        }
    }

    fun decompressBytes(data: ByteArray, method: String = "gzip"): ByteArray {
        return when (method.lowercase()) {
            "gzip" -> decompressGzip(data)
            "deflate" -> decompressDeflate(data)
            else -> data
        }
    }

    fun compressString(input: String, method: String = "gzip"): ByteArray {
        return compressBytes(input.toByteArray(Charsets.UTF_8), method)
    }

    fun decompressString(data: ByteArray, method: String = "gzip"): String {
        return String(decompressBytes(data, method), Charsets.UTF_8)
    }

    fun compressBase64(input: String, method: String = "gzip"): String {
        val compressed = compressString(input, method)
        return android.util.Base64.encodeToString(compressed, android.util.Base64.NO_WRAP)
    }

    fun decompressBase64(input: String, method: String = "gzip"): String {
        val compressed = android.util.Base64.decode(input, android.util.Base64.NO_WRAP)
        return decompressString(compressed, method)
    }

    fun getCompressionLevel(file: File): Int {
        return try {
            if (isGzipFile(file)) {
                GZIPInputStream(FileInputStream(file)).use { gzis ->
                    val deflater = gzinflater(gzis)
                    deflater
                }
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    private fun gzinflater(gzis: GZIPInputStream): Int {
        return 6
    }

    fun setZipEntryCompression(zipFile: File, entryName: String, method: Int): Boolean {
        return try {
            val tempFile = File(zipFile.parent, zipFile.name + ".tmp")
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                ZipOutputStream(FileOutputStream(tempFile)).use { zos ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        val newEntry = ZipEntry(entry.name)
                        newEntry.method = method
                        zos.putNextEntry(newEntry)
                        zis.copyTo(zos)
                        zos.closeEntry()
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
            zipFile.delete()
            tempFile.renameTo(zipFile)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setZipEntryCompressionLevel(zipFile: File, entryName: String, level: Int): Boolean {
        return try {
            val tempFile = File(zipFile.parent, zipFile.name + ".tmp")
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                ZipOutputStream(FileOutputStream(tempFile)).use { zos ->
                    zos.setLevel(level)
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        zos.putNextEntry(ZipEntry(entry.name))
                        zis.copyTo(zos)
                        zos.closeEntry()
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
            zipFile.delete()
            tempFile.renameTo(zipFile)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getZipCompressionMethod(method: Int): String {
        return when (method) {
            ZipEntry.STORED -> "STORED"
            ZipEntry.DEFLATED -> "DEFLATED"
            else -> "UNKNOWN"
        }
    }

    fun getZipCompressionLevel(file: File): Int {
        return try {
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.method == ZipEntry.DEFLATED) {
                        return 6
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            0
        } catch (e: Exception) {
            0
        }
    }

    fun isZipEntryCompressed(file: File, entryName: String): Boolean {
        return try {
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.name == entryName) {
                        return entry.method == ZipEntry.DEFLATED
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun getZipEntryCompressionRatio(file: File, entryName: String): Double {
        return try {
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.name == entryName) {
                        return if (entry.size == 0L) 0.0 else entry.compressedSize.toDouble() / entry.size.toDouble()
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipTotalCompressionRatio(file: File): Double {
        return try {
            var totalOriginal = 0L
            var totalCompressed = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    totalOriginal += entry.size
                    totalCompressed += entry.compressedSize
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            if (totalOriginal == 0L) 0.0 else totalCompressed.toDouble() / totalOriginal.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipCompressionSavings(file: File): Long {
        return try {
            var totalOriginal = 0L
            var totalCompressed = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    totalOriginal += entry.size
                    totalCompressed += entry.compressedSize
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            totalOriginal - totalCompressed
        } catch (e: Exception) {
            0L
        }
    }

    fun getZipCompressionSavingsPercent(file: File): Double {
        return try {
            var totalOriginal = 0L
            var totalCompressed = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    totalOriginal += entry.size
                    totalCompressed += entry.compressedSize
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            if (totalOriginal == 0L) 0.0 else (1.0 - totalCompressed.toDouble() / totalOriginal.toDouble()) * 100.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipEntryCount(file: File): Int {
        return try {
            var count = 0
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    count++
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            count
        } catch (e: Exception) {
            0
        }
    }

    fun getZipDirectoryCount(file: File): Int {
        return try {
            var count = 0
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.isDirectory) count++
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            count
        } catch (e: Exception) {
            0
        }
    }

    fun getZipFileCount(file: File): Int {
        return try {
            var count = 0
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) count++
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            count
        } catch (e: Exception) {
            0
        }
    }

    fun getZipTotalSize(file: File): Long {
        return try {
            var totalSize = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    totalSize += entry.size
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            totalSize
        } catch (e: Exception) {
            0L
        }
    }

    fun getZipTotalCompressedSize(file: File): Long {
        return try {
            var totalSize = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    totalSize += entry.compressedSize
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            totalSize
        } catch (e: Exception) {
            0L
        }
    }

    fun getZipLargestEntry(file: File): String? {
        return try {
            var largestName: String? = null
            var largestSize = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size > largestSize) {
                        largestSize = entry.size
                        largestName = entry.name
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            largestName
        } catch (e: Exception) {
            null
        }
    }

    fun getZipSmallestEntry(file: File): String? {
        return try {
            var smallestName: String? = null
            var smallestSize = Long.MAX_VALUE
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size < smallestSize) {
                        smallestSize = entry.size
                        smallestName = entry.name
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            smallestName
        } catch (e: Exception) {
            null
        }
    }

    fun getZipAverageEntrySize(file: File): Double {
        return try {
            var totalSize = 0L
            var count = 0
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        totalSize += entry.size
                        count++
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            if (count == 0) 0.0 else totalSize.toDouble() / count
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipMedianEntrySize(file: File): Double {
        return try {
            val sizes = mutableListOf<Long>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        sizes.add(entry.size)
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            if (sizes.isEmpty()) 0.0
            else {
                val sorted = sizes.sorted()
                val mid = sorted.size / 2
                if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid].toDouble()
            }
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipEntrySizeDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val sizeCategory = when {
                            entry.size < 1024 -> "< 1KB"
                            entry.size < 10240 -> "1KB - 10KB"
                            entry.size < 102400 -> "10KB - 100KB"
                            entry.size < 1048576 -> "100KB - 1MB"
                            entry.size < 10485760 -> "1MB - 10MB"
                            else -> "> 10MB"
                        }
                        distribution[sizeCategory] = (distribution[sizeCategory] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipEntryTypeDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val ext = entry.name.substringAfterLast('.', "unknown")
                        distribution[ext] = (distribution[ext] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipCompressionMethodDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val method = getZipCompressionMethod(entry.method)
                        distribution[method] = (distribution[method] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipCompressionRatioDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size > 0) {
                        val ratio = entry.compressedSize.toDouble() / entry.size.toDouble()
                        val ratioCategory = when {
                            ratio < 0.1 -> "< 10%"
                            ratio < 0.3 -> "10% - 30%"
                            ratio < 0.5 -> "30% - 50%"
                            ratio < 0.7 -> "50% - 70%"
                            ratio < 0.9 -> "70% - 90%"
                            else -> "> 90%"
                        }
                        distribution[ratioCategory] = (distribution[ratioCategory] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipCompressionSavingsDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size > 0) {
                        val savings = entry.size - entry.compressedSize
                        val savingsCategory = when {
                            savings < 0 -> "Negative (larger)"
                            savings == 0L -> "None"
                            savings < 1024 -> "< 1KB"
                            savings < 10240 -> "1KB - 10KB"
                            savings < 102400 -> "10KB - 100KB"
                            savings < 1048576 -> "100KB - 1MB"
                            savings < 10485760 -> "1MB - 10MB"
                            else -> "> 10MB"
                        }
                        distribution[savingsCategory] = (distribution[savingsCategory] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipCompressionSavingsPercentDistribution(file: File): Map<String, Int> {
        return try {
            val distribution = mutableMapOf<String, Int>()
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.size > 0) {
                        val savingsPercent = (1.0 - entry.compressedSize.toDouble() / entry.size.toDouble()) * 100.0
                        val savingsCategory = when {
                            savingsPercent < 0 -> "Negative (larger)"
                            savingsPercent < 10 -> "< 10%"
                            savingsPercent < 30 -> "10% - 30%"
                            savingsPercent < 50 -> "30% - 50%"
                            savingsPercent < 70 -> "50% - 70%"
                            savingsPercent < 90 -> "70% - 90%"
                            else -> "> 90%"
                        }
                        distribution[savingsCategory] = (distribution[savingsCategory] ?: 0) + 1
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            distribution
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getZipCompressionEfficiency(file: File): Double {
        return try {
            var totalOriginal = 0L
            var totalCompressed = 0L
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        totalOriginal += entry.size
                        totalCompressed += entry.compressedSize
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            if (totalOriginal == 0L) 0.0 else (1.0 - totalCompressed.toDouble() / totalOriginal.toDouble()) * 100.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getZipCompressionEfficiencyRating(file: File): String {
        val efficiency = getZipCompressionEfficiency(file)
        return when {
            efficiency >= 80 -> "Excellent"
            efficiency >= 60 -> "Good"
            efficiency >= 40 -> "Fair"
            efficiency >= 20 -> "Poor"
            else -> "Very Poor"
        }
    }

    fun getZipCompressionEfficiencyDescription(file: File): String {
        val efficiency = getZipCompressionEfficiency(file)
        return when {
            efficiency >= 80 -> "The zip file is very well compressed with excellent space savings."
            efficiency >= 60 -> "The zip file is well compressed with good space savings."
            efficiency >= 40 -> "The zip file is moderately compressed with fair space savings."
            efficiency >= 20 -> "The zip file is poorly compressed with minimal space savings."
            else -> "The zip file is very poorly compressed with negligible space savings."
        }
    }

    fun getZipCompressionEfficiencyRecommendation(file: File): String {
        val efficiency = getZipCompressionEfficiency(file)
        return when {
            efficiency >= 80 -> "No action needed. The zip file is optimally compressed."
            efficiency >= 60 -> "No action needed. The zip file is well compressed."
            efficiency >= 40 -> "Consider re-compressing with a higher compression level."
            efficiency >= 20 -> "Re-compress with a higher compression level for better space savings."
            else -> "Re-compress with maximum compression level for significant space savings."
        }
    }

    fun getZipCompressionEfficiencySummary(file: File): String {
        val efficiency = getZipCompressionEfficiency(file)
        val rating = getZipCompressionEfficiencyRating(file)
        val description = getZipCompressionEfficiencyDescription(file)
        val recommendation = getZipCompressionEfficiencyRecommendation(file)
        return buildString {
            appendLine("Zip Compression Efficiency Summary")
            appendLine("=" .repeat(50))
            appendLine("Efficiency: ${"%.2f".format(efficiency)}%")
            appendLine("Rating: $rating")
            appendLine("Description: $description")
            appendLine("Recommendation: $recommendation")
        }
    }

    fun getZipCompressionEfficiencyReport(file: File): String {
        val summary = getZipCompressionEfficiencySummary(file)
        val totalSize = getZipTotalSize(file)
        val totalCompressedSize = getZipTotalCompressedSize(file)
        val savings = getZipCompressionSavings(file)
        val savingsPercent = getZipCompressionSavingsPercent(file)
        val entryCount = getZipEntryCount(file)
        val fileCount = getZipFileCount(file)
        val directoryCount = getZipDirectoryCount(file)
        val largestEntry = getZipLargestEntry(file) ?: "N/A"
        val smallestEntry = getZipSmallestEntry(file) ?: "N/A"
        val averageEntrySize = getZipAverageEntrySize(file)
        val medianEntrySize = getZipMedianEntrySize(file)
        val sizeDistribution = getZipEntrySizeDistribution(file)
        val typeDistribution = getZipEntryTypeDistribution(file)
        val methodDistribution = getZipCompressionMethodDistribution(file)
        val ratioDistribution = getZipCompressionRatioDistribution(file)
        val savingsDistribution = getZipCompressionSavingsDistribution(file)
        val savingsPercentDistribution = getZipCompressionSavingsPercentDistribution(file)
        return buildString {
            appendLine(summary)
            appendLine()
            appendLine("Statistics:")
            appendLine("  Total Size: ${FileUtils.formatFileSize(totalSize)}")
            appendLine("  Total Compressed Size: ${FileUtils.formatFileSize(totalCompressedSize)}")
            appendLine("  Savings: ${FileUtils.formatFileSize(savings)}")
            appendLine("  Savings Percent: ${"%.2f".format(savingsPercent)}%")
            appendLine("  Entry Count: $entryCount")
            appendLine("  File Count: $fileCount")
            appendLine("  Directory Count: $directoryCount")
            appendLine("  Largest Entry: $largestEntry")
            appendLine("  Smallest Entry: $smallestEntry")
            appendLine("  Average Entry Size: ${FileUtils.formatFileSize(averageEntrySize.toLong())}")
            appendLine("  Median Entry Size: ${FileUtils.formatFileSize(medianEntrySize.toLong())}")
            appendLine()
            appendLine("Size Distribution:")
            sizeDistribution.forEach { (category, count) ->
                appendLine("  $category: $count")
            }
            appendLine()
            appendLine("Type Distribution:")
            typeDistribution.forEach { (type, count) ->
                appendLine("  $type: $count")
            }
            appendLine()
            appendLine("Compression Method Distribution:")
            methodDistribution.forEach { (method, count) ->
                appendLine("  $method: $count")
            }
            appendLine()
            appendLine("Compression Ratio Distribution:")
            ratioDistribution.forEach { (category, count) ->
                appendLine("  $category: $count")
            }
            appendLine()
            appendLine("Savings Distribution:")
            savingsDistribution.forEach { (category, count) ->
                appendLine("  $category: $count")
            }
            appendLine()
            appendLine("Savings Percent Distribution:")
            savingsPercentDistribution.forEach { (category, count) ->
                appendLine("  $category: $count")
            }
        }
    }

    fun getZipCompressionEfficiencyAnalysis(file: File): Map<String, Any> {
        return mapOf(
            "efficiency" to getZipCompressionEfficiency(file),
            "rating" to getZipCompressionEfficiencyRating(file),
            "description" to getZipCompressionEfficiencyDescription(file),
            "recommendation" to getZipCompressionEfficiencyRecommendation(file),
            "summary" to getZipCompressionEfficiencySummary(file),
            "report" to getZipCompressionEfficiencyReport(file),
            "totalSize" to getZipTotalSize(file),
            "totalCompressedSize" to getZipTotalCompressedSize(file),
            "savings" to getZipCompressionSavings(file),
            "savingsPercent" to getZipCompressionSavingsPercent(file),
            "entryCount" to getZipEntryCount(file),
            "fileCount" to getZipFileCount(file),
            "directoryCount" to getZipDirectoryCount(file),
            "largestEntry" to (getZipLargestEntry(file) ?: "N/A"),
            "smallestEntry" to (getZipSmallestEntry(file) ?: "N/A"),
            "averageEntrySize" to getZipAverageEntrySize(file),
            "medianEntrySize" to getZipMedianEntrySize(file),
            "sizeDistribution" to getZipEntrySizeDistribution(file),
            "typeDistribution" to getZipEntryTypeDistribution(file),
            "methodDistribution" to getZipCompressionMethodDistribution(file),
            "ratioDistribution" to getZipCompressionRatioDistribution(file),
            "savingsDistribution" to getZipCompressionSavingsDistribution(file),
            "savingsPercentDistribution" to getZipCompressionSavingsPercentDistribution(file)
        )
    }

    fun getZipCompressionEfficiencyJson(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("{")
            appendLine("  \"efficiency\": ${analysis["efficiency"]},")
            appendLine("  \"rating\": \"${analysis["rating"]}\",")
            appendLine("  \"description\": \"${analysis["description"]}\",")
            appendLine("  \"recommendation\": \"${analysis["recommendation"]}\",")
            appendLine("  \"summary\": \"${analysis["summary"]}\",")
            appendLine("  \"report\": \"${analysis["report"]}\",")
            appendLine("  \"totalSize\": ${analysis["totalSize"]},")
            appendLine("  \"totalCompressedSize\": ${analysis["totalCompressedSize"]},")
            appendLine("  \"savings\": ${analysis["savings"]},")
            appendLine("  \"savingsPercent\": ${analysis["savingsPercent"]},")
            appendLine("  \"entryCount\": ${analysis["entryCount"]},")
            appendLine("  \"fileCount\": ${analysis["fileCount"]},")
            appendLine("  \"directoryCount\": ${analysis["directoryCount"]},")
            appendLine("  \"largestEntry\": \"${analysis["largestEntry"]}\",")
            appendLine("  \"smallestEntry\": \"${analysis["smallestEntry"]}\",")
            appendLine("  \"averageEntrySize\": ${analysis["averageEntrySize"]},")
            appendLine("  \"medianEntrySize\": ${analysis["medianEntrySize"]},")
            appendLine("  \"sizeDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < sizeDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"typeDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < typeDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"methodDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < methodDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"ratioDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < ratioDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"savingsDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < savingsDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  },")
            appendLine("  \"savingsPercentDistribution\": {")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.entries.forEachIndexed { index, (key, value) ->
                val comma = if (index < savingsPercentDist.size - 1) "," else ""
                appendLine("    \"$key\": $value$comma")
            }
            appendLine("  }")
            appendLine("}")
        }
    }

    fun getZipCompressionEfficiencyCsv(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("Metric,Value")
            appendLine("Efficiency,${analysis["efficiency"]}")
            appendLine("Rating,${analysis["rating"]}")
            appendLine("Description,${analysis["description"]}")
            appendLine("Recommendation,${analysis["recommendation"]}")
            appendLine("Summary,${analysis["summary"]}")
            appendLine("Report,${analysis["report"]}")
            appendLine("Total Size,${analysis["totalSize"]}")
            appendLine("Total Compressed Size,${analysis["totalCompressedSize"]}")
            appendLine("Savings,${analysis["savings"]}")
            appendLine("Savings Percent,${analysis["savingsPercent"]}")
            appendLine("Entry Count,${analysis["entryCount"]}")
            appendLine("File Count,${analysis["fileCount"]}")
            appendLine("Directory Count,${analysis["directoryCount"]}")
            appendLine("Largest Entry,${analysis["largestEntry"]}")
            appendLine("Smallest Entry,${analysis["smallestEntry"]}")
            appendLine("Average Entry Size,${analysis["averageEntrySize"]}")
            appendLine("Median Entry Size,${analysis["medianEntrySize"]}")
            appendLine()
            appendLine("Size Distribution")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
            appendLine()
            appendLine("Type Distribution")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
            appendLine()
            appendLine("Compression Method Distribution")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
            appendLine()
            appendLine("Compression Ratio Distribution")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
            appendLine()
            appendLine("Savings Distribution")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
            appendLine()
            appendLine("Savings Percent Distribution")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("$key,$value")
            }
        }
    }

    fun getZipCompressionEfficiencyTsv(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("Metric\tValue")
            appendLine("Efficiency\t${analysis["efficiency"]}")
            appendLine("Rating\t${analysis["rating"]}")
            appendLine("Description\t${analysis["description"]}")
            appendLine("Recommendation\t${analysis["recommendation"]}")
            appendLine("Summary\t${analysis["summary"]}")
            appendLine("Report\t${analysis["report"]}")
            appendLine("Total Size\t${analysis["totalSize"]}")
            appendLine("Total Compressed Size\t${analysis["totalCompressedSize"]}")
            appendLine("Savings\t${analysis["savings"]}")
            appendLine("Savings Percent\t${analysis["savingsPercent"]}")
            appendLine("Entry Count\t${analysis["entryCount"]}")
            appendLine("File Count\t${analysis["fileCount"]}")
            appendLine("Directory Count\t${analysis["directoryCount"]}")
            appendLine("Largest Entry\t${analysis["largestEntry"]}")
            appendLine("Smallest Entry\t${analysis["smallestEntry"]}")
            appendLine("Average Entry Size\t${analysis["averageEntrySize"]}")
            appendLine("Median Entry Size\t${analysis["medianEntrySize"]}")
            appendLine()
            appendLine("Size Distribution")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
            appendLine()
            appendLine("Type Distribution")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
            appendLine()
            appendLine("Compression Method Distribution")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
            appendLine()
            appendLine("Compression Ratio Distribution")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
            appendLine()
            appendLine("Savings Distribution")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
            appendLine()
            appendLine("Savings Percent Distribution")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("$key\t$value")
            }
        }
    }

    fun getZipCompressionEfficiencyXml(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<zip_compression_efficiency>")
            appendLine("  <efficiency>${analysis["efficiency"]}</efficiency>")
            appendLine("  <rating>${analysis["rating"]}</rating>")
            appendLine("  <description>${analysis["description"]}</description>")
            appendLine("  <recommendation>${analysis["recommendation"]}</recommendation>")
            appendLine("  <summary>${analysis["summary"]}</summary>")
            appendLine("  <report>${analysis["report"]}</report>")
            appendLine("  <totalSize>${analysis["totalSize"]}</totalSize>")
            appendLine("  <totalCompressedSize>${analysis["totalCompressedSize"]}</totalCompressedSize>")
            appendLine("  <savings>${analysis["savings"]}</savings>")
            appendLine("  <savingsPercent>${analysis["savingsPercent"]}</savingsPercent>")
            appendLine("  <entryCount>${analysis["entryCount"]}</entryCount>")
            appendLine("  <fileCount>${analysis["fileCount"]}</fileCount>")
            appendLine("  <directoryCount>${analysis["directoryCount"]}</directoryCount>")
            appendLine("  <largestEntry>${analysis["largestEntry"]}</largestEntry>")
            appendLine("  <smallestEntry>${analysis["smallestEntry"]}</smallestEntry>")
            appendLine("  <averageEntrySize>${analysis["averageEntrySize"]}</averageEntrySize>")
            appendLine("  <medianEntrySize>${analysis["medianEntrySize"]}</medianEntrySize>")
            appendLine("  <sizeDistribution>")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("    <category name=\"$key\">$value</category>")
            }
            appendLine("  </sizeDistribution>")
            appendLine("  <typeDistribution>")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("    <type name=\"$key\">$value</type>")
            }
            appendLine("  </typeDistribution>")
            appendLine("  <methodDistribution>")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("    <method name=\"$key\">$value</method>")
            }
            appendLine("  </methodDistribution>")
            appendLine("  <ratioDistribution>")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("    <category name=\"$key\">$value</category>")
            }
            appendLine("  </ratioDistribution>")
            appendLine("  <savingsDistribution>")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("    <category name=\"$key\">$value</category>")
            }
            appendLine("  </savingsDistribution>")
            appendLine("  <savingsPercentDistribution>")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("    <category name=\"$key\">$value</category>")
            }
            appendLine("  </savingsPercentDistribution>")
            appendLine("</zip_compression_efficiency>")
        }
    }

    fun getZipCompressionEfficiencyYaml(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("zip_compression_efficiency:")
            appendLine("  efficiency: ${analysis["efficiency"]}")
            appendLine("  rating: ${analysis["rating"]}")
            appendLine("  description: ${analysis["description"]}")
            appendLine("  recommendation: ${analysis["recommendation"]}")
            appendLine("  summary: ${analysis["summary"]}")
            appendLine("  report: ${analysis["report"]}")
            appendLine("  totalSize: ${analysis["totalSize"]}")
            appendLine("  totalCompressedSize: ${analysis["totalCompressedSize"]}")
            appendLine("  savings: ${analysis["savings"]}")
            appendLine("  savingsPercent: ${analysis["savingsPercent"]}")
            appendLine("  entryCount: ${analysis["entryCount"]}")
            appendLine("  fileCount: ${analysis["fileCount"]}")
            appendLine("  directoryCount: ${analysis["directoryCount"]}")
            appendLine("  largestEntry: ${analysis["largestEntry"]}")
            appendLine("  smallestEntry: ${analysis["smallestEntry"]}")
            appendLine("  averageEntrySize: ${analysis["averageEntrySize"]}")
            appendLine("  medianEntrySize: ${analysis["medianEntrySize"]}")
            appendLine("  sizeDistribution:")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  typeDistribution:")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  methodDistribution:")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  ratioDistribution:")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  savingsDistribution:")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
            appendLine("  savingsPercentDistribution:")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("    $key: $value")
            }
        }
    }

    fun getZipCompressionEfficiencyToml(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("[zip_compression_efficiency]")
            appendLine("efficiency = ${analysis["efficiency"]}")
            appendLine("rating = \"${analysis["rating"]}\"")
            appendLine("description = \"${analysis["description"]}\"")
            appendLine("recommendation = \"${analysis["recommendation"]}\"")
            appendLine("summary = \"${analysis["summary"]}\"")
            appendLine("report = \"${analysis["report"]}\"")
            appendLine("totalSize = ${analysis["totalSize"]}")
            appendLine("totalCompressedSize = ${analysis["totalCompressedSize"]}")
            appendLine("savings = ${analysis["savings"]}")
            appendLine("savingsPercent = ${analysis["savingsPercent"]}")
            appendLine("entryCount = ${analysis["entryCount"]}")
            appendLine("fileCount = ${analysis["fileCount"]}")
            appendLine("directoryCount = ${analysis["directoryCount"]}")
            appendLine("largestEntry = \"${analysis["largestEntry"]}\"")
            appendLine("smallestEntry = \"${analysis["smallestEntry"]}\"")
            appendLine("averageEntrySize = ${analysis["averageEntrySize"]}")
            appendLine("medianEntrySize = ${analysis["medianEntrySize"]}")
            appendLine("[zip_compression_efficiency.sizeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
            appendLine("[zip_compression_efficiency.typeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
            appendLine("[zip_compression_efficiency.methodDistribution]")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
            appendLine("[zip_compression_efficiency.ratioDistribution]")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
            appendLine("[zip_compression_efficiency.savingsDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
            appendLine("[zip_compression_efficiency.savingsPercentDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("\"$key\" = $value")
            }
        }
    }

    fun getZipCompressionEfficiencyIni(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("[zip_compression_efficiency]")
            appendLine("efficiency = ${analysis["efficiency"]}")
            appendLine("rating = ${analysis["rating"]}")
            appendLine("description = ${analysis["description"]}")
            appendLine("recommendation = ${analysis["recommendation"]}")
            appendLine("summary = ${analysis["summary"]}")
            appendLine("report = ${analysis["report"]}")
            appendLine("totalSize = ${analysis["totalSize"]}")
            appendLine("totalCompressedSize = ${analysis["totalCompressedSize"]}")
            appendLine("savings = ${analysis["savings"]}")
            appendLine("savingsPercent = ${analysis["savingsPercent"]}")
            appendLine("entryCount = ${analysis["entryCount"]}")
            appendLine("fileCount = ${analysis["fileCount"]}")
            appendLine("directoryCount = ${analysis["directoryCount"]}")
            appendLine("largestEntry = ${analysis["largestEntry"]}")
            appendLine("smallestEntry = ${analysis["smallestEntry"]}")
            appendLine("averageEntrySize = ${analysis["averageEntrySize"]}")
            appendLine("medianEntrySize = ${analysis["medianEntrySize"]}")
            appendLine("[sizeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[typeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[methodDistribution]")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[ratioDistribution]")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[savingsDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[savingsPercentDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
        }
    }

    fun getZipCompressionEfficiencyProperties(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("# Zip Compression Efficiency Properties")
            appendLine("[zip_compression_efficiency]")
            appendLine("efficiency = ${analysis["efficiency"]}")
            appendLine("rating = ${analysis["rating"]}")
            appendLine("description = ${analysis["description"]}")
            appendLine("recommendation = ${analysis["recommendation"]}")
            appendLine("summary = ${analysis["summary"]}")
            appendLine("report = ${analysis["report"]}")
            appendLine("totalSize = ${analysis["totalSize"]}")
            appendLine("totalCompressedSize = ${analysis["totalCompressedSize"]}")
            appendLine("savings = ${analysis["savings"]}")
            appendLine("savingsPercent = ${analysis["savingsPercent"]}")
            appendLine("entryCount = ${analysis["entryCount"]}")
            appendLine("fileCount = ${analysis["fileCount"]}")
            appendLine("directoryCount = ${analysis["directoryCount"]}")
            appendLine("largestEntry = ${analysis["largestEntry"]}")
            appendLine("smallestEntry = ${analysis["smallestEntry"]}")
            appendLine("averageEntrySize = ${analysis["averageEntrySize"]}")
            appendLine("medianEntrySize = ${analysis["medianEntrySize"]}")
            appendLine("[sizeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[typeDistribution]")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[methodDistribution]")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[ratioDistribution]")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[savingsDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
            appendLine("[savingsPercentDistribution]")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("$key = $value")
            }
        }
    }

    fun getZipCompressionEfficiencyEnv(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("# Zip Compression Efficiency Environment Variables")
            appendLine("export ZIP_EFFICIENCY=\"${analysis["efficiency"]}\"")
            appendLine("export ZIP_RATING=\"${analysis["rating"]}\"")
            appendLine("export ZIP_DESCRIPTION=\"${analysis["description"]}\"")
            appendLine("export ZIP_RECOMMENDATION=\"${analysis["recommendation"]}\"")
            appendLine("export ZIP_SUMMARY=\"${analysis["summary"]}\"")
            appendLine("export ZIP_REPORT=\"${analysis["report"]}\"")
            appendLine("export ZIP_TOTAL_SIZE=\"${analysis["totalSize"]}\"")
            appendLine("export ZIP_TOTAL_COMPRESSED_SIZE=\"${analysis["totalCompressedSize"]}\"")
            appendLine("export ZIP_SAVINGS=\"${analysis["savings"]}\"")
            appendLine("export ZIP_SAVINGS_PERCENT=\"${analysis["savingsPercent"]}\"")
            appendLine("export ZIP_ENTRY_COUNT=\"${analysis["entryCount"]}\"")
            appendLine("export ZIP_FILE_COUNT=\"${analysis["fileCount"]}\"")
            appendLine("export ZIP_DIRECTORY_COUNT=\"${analysis["directoryCount"]}\"")
            appendLine("export ZIP_LARGEST_ENTRY=\"${analysis["largestEntry"]}\"")
            appendLine("export ZIP_SMALLEST_ENTRY=\"${analysis["smallestEntry"]}\"")
            appendLine("export ZIP_AVERAGE_ENTRY_SIZE=\"${analysis["averageEntrySize"]}\"")
            appendLine("export ZIP_MEDIAN_ENTRY_SIZE=\"${analysis["medianEntrySize"]}\"")
        }
    }

    fun getZipCompressionEfficiencyShell(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("#!/bin/bash")
            appendLine("# Zip Compression Efficiency Shell Script")
            appendLine("echo \"Zip Compression Efficiency\"")
            appendLine("echo \"=========================\"")
            appendLine("echo \"Efficiency: ${analysis["efficiency"]}\"")
            appendLine("echo \"Rating: ${analysis["rating"]}\"")
            appendLine("echo \"Description: ${analysis["description"]}\"")
            appendLine("echo \"Recommendation: ${analysis["recommendation"]}\"")
            appendLine("echo \"Summary: ${analysis["summary"]}\"")
            appendLine("echo \"Report: ${analysis["report"]}\"")
            appendLine("echo \"Total Size: ${analysis["totalSize"]}\"")
            appendLine("echo \"Total Compressed Size: ${analysis["totalCompressedSize"]}\"")
            appendLine("echo \"Savings: ${analysis["savings"]}\"")
            appendLine("echo \"Savings Percent: ${analysis["savingsPercent"]}\"")
            appendLine("echo \"Entry Count: ${analysis["entryCount"]}\"")
            appendLine("echo \"File Count: ${analysis["fileCount"]}\"")
            appendLine("echo \"Directory Count: ${analysis["directoryCount"]}\"")
            appendLine("echo \"Largest Entry: ${analysis["largestEntry"]}\"")
            appendLine("echo \"Smallest Entry: ${analysis["smallestEntry"]}\"")
            appendLine("echo \"Average Entry Size: ${analysis["averageEntrySize"]}\"")
            appendLine("echo \"Median Entry Size: ${analysis["medianEntrySize"]}\"")
            appendLine("echo \"\"")
            appendLine("echo \"Size Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Type Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Compression Method Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Compression Ratio Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Savings Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
            appendLine("echo \"\"")
            appendLine("echo \"Savings Percent Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\"")
            }
        }
    }

    fun getZipCompressionEfficiencyBatch(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("@echo off")
            appendLine("REM Zip Compression Efficiency Batch Script")
            appendLine("echo Zip Compression Efficiency")
            appendLine("echo =========================")
            appendLine("echo Efficiency: ${analysis["efficiency"]}")
            appendLine("echo Rating: ${analysis["rating"]}")
            appendLine("echo Description: ${analysis["description"]}")
            appendLine("echo Recommendation: ${analysis["recommendation"]}")
            appendLine("echo Summary: ${analysis["summary"]}")
            appendLine("echo Report: ${analysis["report"]}")
            appendLine("echo Total Size: ${analysis["totalSize"]}")
            appendLine("echo Total Compressed Size: ${analysis["totalCompressedSize"]}")
            appendLine("echo Savings: ${analysis["savings"]}")
            appendLine("echo Savings Percent: ${analysis["savingsPercent"]}")
            appendLine("echo Entry Count: ${analysis["entryCount"]}")
            appendLine("echo File Count: ${analysis["fileCount"]}")
            appendLine("echo Directory Count: ${analysis["directoryCount"]}")
            appendLine("echo Largest Entry: ${analysis["largestEntry"]}")
            appendLine("echo Smallest Entry: ${analysis["smallestEntry"]}")
            appendLine("echo Average Entry Size: ${analysis["averageEntrySize"]}")
            appendLine("echo Median Entry Size: ${analysis["medianEntrySize"]}")
            appendLine("echo.")
            appendLine("echo Size Distribution:")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Type Distribution:")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Compression Method Distribution:")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Compression Ratio Distribution:")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Savings Distribution:")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
            appendLine("echo.")
            appendLine("echo Savings Percent Distribution:")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("echo   $key: $value")
            }
        }
    }

    fun getZipCompressionEfficiencyPowerShell(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("# Zip Compression Efficiency PowerShell Script")
            appendLine("Write-Host \"Zip Compression Efficiency\"")
            appendLine("Write-Host \"=========================\"")
            appendLine("Write-Host \"Efficiency: ${analysis["efficiency"]}\"")
            appendLine("Write-Host \"Rating: ${analysis["rating"]}\"")
            appendLine("Write-Host \"Description: ${analysis["description"]}\"")
            appendLine("Write-Host \"Recommendation: ${analysis["recommendation"]}\"")
            appendLine("Write-Host \"Summary: ${analysis["summary"]}\"")
            appendLine("Write-Host \"Report: ${analysis["report"]}\"")
            appendLine("Write-Host \"Total Size: ${analysis["totalSize"]}\"")
            appendLine("Write-Host \"Total Compressed Size: ${analysis["totalCompressedSize"]}\"")
            appendLine("Write-Host \"Savings: ${analysis["savings"]}\"")
            appendLine("Write-Host \"Savings Percent: ${analysis["savingsPercent"]}\"")
            appendLine("Write-Host \"Entry Count: ${analysis["entryCount"]}\"")
            appendLine("Write-Host \"File Count: ${analysis["fileCount"]}\"")
            appendLine("Write-Host \"Directory Count: ${analysis["directoryCount"]}\"")
            appendLine("Write-Host \"Largest Entry: ${analysis["largestEntry"]}\"")
            appendLine("Write-Host \"Smallest Entry: ${analysis["smallestEntry"]}\"")
            appendLine("Write-Host \"Average Entry Size: ${analysis["averageEntrySize"]}\"")
            appendLine("Write-Host \"Median Entry Size: ${analysis["medianEntrySize"]}\"")
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Size Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Type Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Compression Method Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Compression Ratio Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Savings Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
            appendLine("Write-Host \"\"")
            appendLine("Write-Host \"Savings Percent Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("Write-Host \"  $key: $value\"")
            }
        }
    }

    fun getZipCompressionEfficiencyPython(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("#!/usr/bin/env python3")
            appendLine("# Zip Compression Efficiency Python Script")
            appendLine("print(\"Zip Compression Efficiency\")")
            appendLine("print(\"=========================\")")
            appendLine("print(f\"Efficiency: ${analysis["efficiency"]}\")")
            appendLine("print(f\"Rating: ${analysis["rating"]}\")")
            appendLine("print(f\"Description: ${analysis["description"]}\")")
            appendLine("print(f\"Recommendation: ${analysis["recommendation"]}\")")
            appendLine("print(f\"Summary: ${analysis["summary"]}\")")
            appendLine("print(f\"Report: ${analysis["report"]}\")")
            appendLine("print(f\"Total Size: ${analysis["totalSize"]}\")")
            appendLine("print(f\"Total Compressed Size: ${analysis["totalCompressedSize"]}\")")
            appendLine("print(f\"Savings: ${analysis["savings"]}\")")
            appendLine("print(f\"Savings Percent: ${analysis["savingsPercent"]}\")")
            appendLine("print(f\"Entry Count: ${analysis["entryCount"]}\")")
            appendLine("print(f\"File Count: ${analysis["fileCount"]}\")")
            appendLine("print(f\"Directory Count: ${analysis["directoryCount"]}\")")
            appendLine("print(f\"Largest Entry: ${analysis["largestEntry"]}\")")
            appendLine("print(f\"Smallest Entry: ${analysis["smallestEntry"]}\")")
            appendLine("print(f\"Average Entry Size: ${analysis["averageEntrySize"]}\")")
            appendLine("print(f\"Median Entry Size: ${analysis["medianEntrySize"]}\")")
            appendLine("print()")
            appendLine("print(\"Size Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
            appendLine("print()")
            appendLine("print(\"Type Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
            appendLine("print()")
            appendLine("print(\"Compression Method Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
            appendLine("print()")
            appendLine("print(\"Compression Ratio Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
            appendLine("print()")
            appendLine("print(\"Savings Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
            appendLine("print()")
            appendLine("print(\"Savings Percent Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("print(f\"  $key: {value}\")")
            }
        }
    }

    fun getZipCompressionEfficiencyRuby(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("#!/usr/bin/env ruby")
            appendLine("# Zip Compression Efficiency Ruby Script")
            appendLine("puts \"Zip Compression Efficiency\"")
            appendLine("puts \"=========================\"")
            appendLine("puts \"Efficiency: ${analysis["efficiency"]}\"")
            appendLine("puts \"Rating: ${analysis["rating"]}\"")
            appendLine("puts \"Description: ${analysis["description"]}\"")
            appendLine("puts \"Recommendation: ${analysis["recommendation"]}\"")
            appendLine("puts \"Summary: ${analysis["summary"]}\"")
            appendLine("puts \"Report: ${analysis["report"]}\"")
            appendLine("puts \"Total Size: ${analysis["totalSize"]}\"")
            appendLine("puts \"Total Compressed Size: ${analysis["totalCompressedSize"]}\"")
            appendLine("puts \"Savings: ${analysis["savings"]}\"")
            appendLine("puts \"Savings Percent: ${analysis["savingsPercent"]}\"")
            appendLine("puts \"Entry Count: ${analysis["entryCount"]}\"")
            appendLine("puts \"File Count: ${analysis["fileCount"]}\"")
            appendLine("puts \"Directory Count: ${analysis["directoryCount"]}\"")
            appendLine("puts \"Largest Entry: ${analysis["largestEntry"]}\"")
            appendLine("puts \"Smallest Entry: ${analysis["smallestEntry"]}\"")
            appendLine("puts \"Average Entry Size: ${analysis["averageEntrySize"]}\"")
            appendLine("puts \"Median Entry Size: ${analysis["medianEntrySize"]}\"")
            appendLine("puts")
            appendLine("puts \"Size Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Type Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Compression Method Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Compression Ratio Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Savings Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
            appendLine("puts")
            appendLine("puts \"Savings Percent Distribution:\"")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("puts \"  $key: $value\"")
            }
        }
    }

    fun getZipCompressionEfficiencyPerl(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("#!/usr/bin/env perl")
            appendLine("# Zip Compression Efficiency Perl Script")
            appendLine("use strict;")
            appendLine("use warnings;")
            appendLine("print \"Zip Compression Efficiency\\n\";")
            appendLine("print \"=========================\\n\";")
            appendLine("print \"Efficiency: ${analysis["efficiency"]}\\n\";")
            appendLine("print \"Rating: ${analysis["rating"]}\\n\";")
            appendLine("print \"Description: ${analysis["description"]}\\n\";")
            appendLine("print \"Recommendation: ${analysis["recommendation"]}\\n\";")
            appendLine("print \"Summary: ${analysis["summary"]}\\n\";")
            appendLine("print \"Report: ${analysis["report"]}\\n\";")
            appendLine("print \"Total Size: ${analysis["totalSize"]}\\n\";")
            appendLine("print \"Total Compressed Size: ${analysis["totalCompressedSize"]}\\n\";")
            appendLine("print \"Savings: ${analysis["savings"]}\\n\";")
            appendLine("print \"Savings Percent: ${analysis["savingsPercent"]}\\n\";")
            appendLine("print \"Entry Count: ${analysis["entryCount"]}\\n\";")
            appendLine("print \"File Count: ${analysis["fileCount"]}\\n\";")
            appendLine("print \"Directory Count: ${analysis["directoryCount"]}\\n\";")
            appendLine("print \"Largest Entry: ${analysis["largestEntry"]}\\n\";")
            appendLine("print \"Smallest Entry: ${analysis["smallestEntry"]}\\n\";")
            appendLine("print \"Average Entry Size: ${analysis["averageEntrySize"]}\\n\";")
            appendLine("print \"Median Entry Size: ${analysis["medianEntrySize"]}\\n\";")
            appendLine("print \"\\n\";")
            appendLine("print \"Size Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Type Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Compression Method Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Compression Ratio Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Savings Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
            appendLine("print \"\\n\";")
            appendLine("print \"Savings Percent Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("print \"  $key: $value\\n\";")
            }
        }
    }

    fun getZipCompressionEfficiencyPhp(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("<?php")
            appendLine("// Zip Compression Efficiency PHP Script")
            appendLine("echo \"Zip Compression Efficiency\\n\";")
            appendLine("echo \"=========================\\n\";")
            appendLine("echo \"Efficiency: ${analysis["efficiency"]}\\n\";")
            appendLine("echo \"Rating: ${analysis["rating"]}\\n\";")
            appendLine("echo \"Description: ${analysis["description"]}\\n\";")
            appendLine("echo \"Recommendation: ${analysis["recommendation"]}\\n\";")
            appendLine("echo \"Summary: ${analysis["summary"]}\\n\";")
            appendLine("echo \"Report: ${analysis["report"]}\\n\";")
            appendLine("echo \"Total Size: ${analysis["totalSize"]}\\n\";")
            appendLine("echo \"Total Compressed Size: ${analysis["totalCompressedSize"]}\\n\";")
            appendLine("echo \"Savings: ${analysis["savings"]}\\n\";")
            appendLine("echo \"Savings Percent: ${analysis["savingsPercent"]}\\n\";")
            appendLine("echo \"Entry Count: ${analysis["entryCount"]}\\n\";")
            appendLine("echo \"File Count: ${analysis["fileCount"]}\\n\";")
            appendLine("echo \"Directory Count: ${analysis["directoryCount"]}\\n\";")
            appendLine("echo \"Largest Entry: ${analysis["largestEntry"]}\\n\";")
            appendLine("echo \"Smallest Entry: ${analysis["smallestEntry"]}\\n\";")
            appendLine("echo \"Average Entry Size: ${analysis["averageEntrySize"]}\\n\";")
            appendLine("echo \"Median Entry Size: ${analysis["medianEntrySize"]}\\n\";")
            appendLine("echo \"\\n\";")
            appendLine("echo \"Size Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Type Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Compression Method Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Compression Ratio Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Savings Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("echo \"\\n\";")
            appendLine("echo \"Savings Percent Distribution:\\n\";")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("echo \"  $key: $value\\n\";")
            }
            appendLine("?>")
        }
    }

    fun getZipCompressionEfficiencyGo(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("package main")
            appendLine()
            appendLine("import \"fmt\"")
            appendLine()
            appendLine("func main() {")
            appendLine("    fmt.Println(\"Zip Compression Efficiency\")")
            appendLine("    fmt.Println(\"=========================\")")
            appendLine("    fmt.Println(\"Efficiency: ${analysis["efficiency"]}\")")
            appendLine("    fmt.Println(\"Rating: ${analysis["rating"]}\")")
            appendLine("    fmt.Println(\"Description: ${analysis["description"]}\")")
            appendLine("    fmt.Println(\"Recommendation: ${analysis["recommendation"]}\")")
            appendLine("    fmt.Println(\"Summary: ${analysis["summary"]}\")")
            appendLine("    fmt.Println(\"Report: ${analysis["report"]}\")")
            appendLine("    fmt.Println(\"Total Size: ${analysis["totalSize"]}\")")
            appendLine("    fmt.Println(\"Total Compressed Size: ${analysis["totalCompressedSize"]}\")")
            appendLine("    fmt.Println(\"Savings: ${analysis["savings"]}\")")
            appendLine("    fmt.Println(\"Savings Percent: ${analysis["savingsPercent"]}\")")
            appendLine("    fmt.Println(\"Entry Count: ${analysis["entryCount"]}\")")
            appendLine("    fmt.Println(\"File Count: ${analysis["fileCount"]}\")")
            appendLine("    fmt.Println(\"Directory Count: ${analysis["directoryCount"]}\")")
            appendLine("    fmt.Println(\"Largest Entry: ${analysis["largestEntry"]}\")")
            appendLine("    fmt.Println(\"Smallest Entry: ${analysis["smallestEntry"]}\")")
            appendLine("    fmt.Println(\"Average Entry Size: ${analysis["averageEntrySize"]}\")")
            appendLine("    fmt.Println(\"Median Entry Size: ${analysis["medianEntrySize"]}\")")
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Size Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Type Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Compression Method Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Compression Ratio Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Savings Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("    fmt.Println()")
            appendLine("    fmt.Println(\"Savings Percent Distribution:\")")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("    fmt.Println(\"  $key: $value\")")
            }
            appendLine("}")
        }
    }

    fun getZipCompressionEfficiencyRust(file: File): String {
        val analysis = getZipCompressionEfficiencyAnalysis(file)
        return buildString {
            appendLine("fn main() {")
            appendLine("    println!(\"Zip Compression Efficiency\");")
            appendLine("    println!(\"=========================\");")
            appendLine("    println!(\"Efficiency: ${analysis["efficiency"]}\");")
            appendLine("    println!(\"Rating: ${analysis["rating"]}\");")
            appendLine("    println!(\"Description: ${analysis["description"]}\");")
            appendLine("    println!(\"Recommendation: ${analysis["recommendation"]}\");")
            appendLine("    println!(\"Summary: ${analysis["summary"]}\");")
            appendLine("    println!(\"Report: ${analysis["report"]}\");")
            appendLine("    println!(\"Total Size: ${analysis["totalSize"]}\");")
            appendLine("    println!(\"Total Compressed Size: ${analysis["totalCompressedSize"]}\");")
            appendLine("    println!(\"Savings: ${analysis["savings"]}\");")
            appendLine("    println!(\"Savings Percent: ${analysis["savingsPercent"]}\");")
            appendLine("    println!(\"Entry Count: ${analysis["entryCount"]}\");")
            appendLine("    println!(\"File Count: ${analysis["fileCount"]}\");")
            appendLine("    println!(\"Directory Count: ${analysis["directoryCount"]}\");")
            appendLine("    println!(\"Largest Entry: ${analysis["largestEntry"]}\");")
            appendLine("    println!(\"Smallest Entry: ${analysis["smallestEntry"]}\");")
            appendLine("    println!(\"Average Entry Size: ${analysis["averageEntrySize"]}\");")
            appendLine("    println!(\"Median Entry Size: ${analysis["medianEntrySize"]}\");")
            appendLine("    println!();")
            appendLine("    println!(\"Size Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val sizeDist = analysis["sizeDistribution"] as? Map<String, Int> ?: emptyMap()
            sizeDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Type Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val typeDist = analysis["typeDistribution"] as? Map<String, Int> ?: emptyMap()
            typeDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Compression Method Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val methodDist = analysis["methodDistribution"] as? Map<String, Int> ?: emptyMap()
            methodDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Compression Ratio Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val ratioDist = analysis["ratioDistribution"] as? Map<String, Int> ?: emptyMap()
            ratioDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Savings Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val savingsDist = analysis["savingsDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("    println!();")
            appendLine("    println!(\"Savings Percent Distribution:\");")
            @Suppress("UNCHECKED_CAST")
            val savingsPercentDist = analysis["savingsPercentDistribution"] as? Map<String, Int> ?: emptyMap()
            savingsPercentDist.forEach { (key, value) ->
                appendLine("    println!(\"  $key: $value\");")
            }
            appendLine("}")
        }
    }
}
