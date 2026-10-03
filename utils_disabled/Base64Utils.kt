package com.mweshimiwa.assistant.utils

import android.util.Base64
import java.nio.charset.Charset

object Base64Utils {

    fun encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun encode(data: ByteArray, flags: Int): String {
        return Base64.encodeToString(data, flags)
    }

    fun encodeNoWrap(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    fun encodeURLSafe(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun encodeString(text: String, charset: Charset = Charsets.UTF_8): String {
        return encode(text.toByteArray(charset))
    }

    fun encodeStringNoWrap(text: String, charset: Charset = Charsets.UTF_8): String {
        return encodeNoWrap(text.toByteArray(charset))
    }

    fun encodeStringURLSafe(text: String, charset: Charset = Charsets.UTF_8): String {
        return encodeURLSafe(text.toByteArray(charset))
    }

    fun decode(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.DEFAULT)
    }

    fun decode(encoded: String, flags: Int): ByteArray {
        return Base64.decode(encoded, flags)
    }

    fun decodeNoWrap(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.NO_WRAP)
    }

    fun decodeURLSafe(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun decodeToString(encoded: String, charset: Charset = Charsets.UTF_8): String {
        return String(decode(encoded), charset)
    }

    fun decodeToStringNoWrap(encoded: String, charset: Charset = Charsets.UTF_8): String {
        return String(decodeNoWrap(encoded), charset)
    }

    fun decodeToStringURLSafe(encoded: String, charset: Charset = Charsets.UTF_8): String {
        return String(decodeURLSafe(encoded), charset)
    }

    fun isBase64(data: String): Boolean {
        return try {
            Base64.decode(data, Base64.DEFAULT)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun isBase64NoWrap(data: String): Boolean {
        return try {
            Base64.decode(data, Base64.NO_WRAP)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun isBase64URLSafe(data: String): Boolean {
        return try {
            Base64.decode(data, Base64.URL_SAFE or Base64.NO_WRAP)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun isValidBase64(data: String): Boolean {
        return isBase64(data) || isBase64NoWrap(data) || isBase64URLSafe(data)
    }

    fun encodeLines(data: ByteArray, lineLength: Int = 76): String {
        val encoded = encode(data)
        val sb = StringBuilder()
        var i = 0
        while (i < encoded.length) {
            val end = minOf(i + lineLength, encoded.length)
            sb.append(encoded.substring(i, end))
            if (end < encoded.length) sb.append("\n")
            i = end
        }
        return sb.toString()
    }

    fun decodeLines(encoded: String): ByteArray {
        val cleaned = encoded.replace("\n", "").replace("\r", "")
        return decode(cleaned)
    }

    fun encodeWithPadding(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun encodeWithoutPadding(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun decodeWithPadding(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.DEFAULT)
    }

    fun decodeWithoutPadding(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun addPadding(encoded: String): String {
        val padding = (4 - encoded.length % 4) % 4
        return encoded + "=".repeat(padding)
    }

    fun removePadding(encoded: String): String {
        return encoded.trimEnd('=')
    }

    fun encodeChunked(data: ByteArray, chunkSize: Int = 76): List<String> {
        val encoded = encode(data)
        return encoded.chunked(chunkSize)
    }

    fun decodeChunked(chunks: List<String>): ByteArray {
        return decode(chunks.joinToString(""))
    }

    fun encodeFile(file: java.io.File): String {
        return encode(file.readBytes())
    }

    fun decodeToFile(encoded: String, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decode(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeFileNoWrap(file: java.io.File): String {
        return encodeNoWrap(file.readBytes())
    }

    fun decodeToFileNoWrap(encoded: String, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decodeNoWrap(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeFileURLSafe(file: java.io.File): String {
        return encodeURLSafe(file.readBytes())
    }

    fun decodeToFileURLSafe(encoded: String, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decodeURLSafe(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImage(image: android.graphics.Bitmap, format: android.graphics.Bitmap.CompressFormat = android.graphics.Bitmap.CompressFormat.PNG, quality: Int = 100): String {
        val stream = java.io.ByteArrayOutputStream()
        image.compress(format, quality, stream)
        return encode(stream.toByteArray())
    }

    fun decodeImage(encoded: String): android.graphics.Bitmap? {
        return try {
            val bytes = decode(encoded)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun encodeImageNoWrap(image: android.graphics.Bitmap, format: android.graphics.Bitmap.CompressFormat = android.graphics.Bitmap.CompressFormat.PNG, quality: Int = 100): String {
        val stream = java.io.ByteArrayOutputStream()
        image.compress(format, quality, stream)
        return encodeNoWrap(stream.toByteArray())
    }

    fun decodeImageNoWrap(encoded: String): android.graphics.Bitmap? {
        return try {
            val bytes = decodeNoWrap(encoded)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun encodeImageURLSafe(image: android.graphics.Bitmap, format: android.graphics.Bitmap.CompressFormat = android.graphics.Bitmap.CompressFormat.PNG, quality: Int = 100): String {
        val stream = java.io.ByteArrayOutputStream()
        image.compress(format, quality, stream)
        return encodeURLSafe(stream.toByteArray())
    }

    fun decodeImageURLSafe(encoded: String): android.graphics.Bitmap? {
        return try {
            val bytes = decodeURLSafe(encoded)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun encodeImageToJPEG(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImage(image, android.graphics.Bitmap.CompressFormat.JPEG, quality)
    }

    fun encodeImageToPNG(image: android.graphics.Bitmap): String {
        return encodeImage(image, android.graphics.Bitmap.CompressFormat.PNG, 100)
    }

    fun encodeImageToWebP(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImage(image, android.graphics.Bitmap.CompressFormat.WEBP, quality)
    }

    fun decodeImageFromJPEG(encoded: String): android.graphics.Bitmap? {
        return decodeImage(encoded)
    }

    fun decodeImageFromPNG(encoded: String): android.graphics.Bitmap? {
        return decodeImage(encoded)
    }

    fun decodeImageFromWebP(encoded: String): android.graphics.Bitmap? {
        return decodeImage(encoded)
    }

    fun encodeImageToJPEGNoWrap(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageNoWrap(image, android.graphics.Bitmap.CompressFormat.JPEG, quality)
    }

    fun encodeImageToPNGNoWrap(image: android.graphics.Bitmap): String {
        return encodeImageNoWrap(image, android.graphics.Bitmap.CompressFormat.PNG, 100)
    }

    fun encodeImageToWebPNoWrap(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageNoWrap(image, android.graphics.Bitmap.CompressFormat.WEBP, quality)
    }

    fun decodeImageFromJPEGNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageNoWrap(encoded)
    }

    fun decodeImageFromPNGNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageNoWrap(encoded)
    }

    fun decodeImageFromWebPNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageNoWrap(encoded)
    }

    fun encodeImageToJPEGURLSafe(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageURLSafe(image, android.graphics.Bitmap.CompressFormat.JPEG, quality)
    }

    fun encodeImageToPNGURLSafe(image: android.graphics.Bitmap): String {
        return encodeImageURLSafe(image, android.graphics.Bitmap.CompressFormat.PNG, 100)
    }

    fun encodeImageToWebPURLSafe(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageURLSafe(image, android.graphics.Bitmap.CompressFormat.WEBP, quality)
    }

    fun decodeImageFromJPEGURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageURLSafe(encoded)
    }

    fun decodeImageFromPNGURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageURLSafe(encoded)
    }

    fun decodeImageFromWebPURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageURLSafe(encoded)
    }

    fun encodeImageToJPEGFile(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decode(encodeImageToJPEG(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFile(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decode(encodeImageToPNG(image)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFile(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decode(encodeImageToWebP(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToJPEGFileNoWrap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decodeNoWrap(encodeImageToJPEGNoWrap(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFileNoWrap(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decodeNoWrap(encodeImageToPNGNoWrap(image)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFileNoWrap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decodeNoWrap(encodeImageToWebPNoWrap(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToJPEGFileURLSafe(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decodeURLSafe(encodeImageToJPEGURLSafe(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFileURLSafe(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return try {
            file.writeBytes(decodeURLSafe(encodeImageToPNGURLSafe(image)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFileURLSafe(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return try {
            file.writeBytes(decodeURLSafe(encodeImageToWebPURLSafe(image, quality)))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToJPEGString(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToJPEG(image, quality)
    }

    fun encodeImageToPNGString(image: android.graphics.Bitmap): String {
        return encodeImageToPNG(image)
    }

    fun encodeImageToWebPString(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToWebP(image, quality)
    }

    fun encodeImageToJPEGStringNoWrap(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToJPEGNoWrap(image, quality)
    }

    fun encodeImageToPNGStringNoWrap(image: android.graphics.Bitmap): String {
        return encodeImageToPNGNoWrap(image)
    }

    fun encodeImageToWebPStringNoWrap(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToWebPNoWrap(image, quality)
    }

    fun encodeImageToJPEGStringURLSafe(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToJPEGURLSafe(image, quality)
    }

    fun encodeImageToPNGStringURLSafe(image: android.graphics.Bitmap): String {
        return encodeImageToPNGURLSafe(image)
    }

    fun encodeImageToWebPStringURLSafe(image: android.graphics.Bitmap, quality: Int = 90): String {
        return encodeImageToWebPURLSafe(image, quality)
    }

    fun decodeImageFromJPEGString(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromJPEG(encoded)
    }

    fun decodeImageFromPNGString(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromPNG(encoded)
    }

    fun decodeImageFromWebPString(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromWebP(encoded)
    }

    fun decodeImageFromJPEGStringNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGNoWrap(encoded)
    }

    fun decodeImageFromPNGStringNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromPNGNoWrap(encoded)
    }

    fun decodeImageFromWebPStringNoWrap(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromWebPNoWrap(encoded)
    }

    fun decodeImageFromJPEGStringURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGURLSafe(encoded)
    }

    fun decodeImageFromPNGStringURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromPNGURLSafe(encoded)
    }

    fun decodeImageFromWebPStringURLSafe(encoded: String): android.graphics.Bitmap? {
        return decodeImageFromWebPURLSafe(encoded)
    }

    fun encodeImageToJPEGFileFromPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFile(image, java.io.File(filePath), quality)
    }

    fun encodeImageToPNGFileFromPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFile(image, java.io.File(filePath))
    }

    fun encodeImageToWebPFileFromPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFile(image, java.io.File(filePath), quality)
    }

    fun encodeImageToJPEGFileFromPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileNoWrap(image, java.io.File(filePath), quality)
    }

    fun encodeImageToPNGFileFromPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileNoWrap(image, java.io.File(filePath))
    }

    fun encodeImageToWebPFileFromPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileNoWrap(image, java.io.File(filePath), quality)
    }

    fun encodeImageToJPEGFileFromPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileURLSafe(image, java.io.File(filePath), quality)
    }

    fun encodeImageToPNGFileFromPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileURLSafe(image, java.io.File(filePath))
    }

    fun encodeImageToWebPFileFromPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileURLSafe(image, java.io.File(filePath), quality)
    }

    fun decodeImageFromJPEGFile(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEG(encodeFile(java.io.File(filePath)))
    }

    fun decodeImageFromPNGFile(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNG(encodeFile(java.io.File(filePath)))
    }

    fun decodeImageFromWebPFile(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebP(encodeFile(java.io.File(filePath)))
    }

    fun decodeImageFromJPEGFileNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGNoWrap(encodeFileNoWrap(java.io.File(filePath)))
    }

    fun decodeImageFromPNGFileNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGNoWrap(encodeFileNoWrap(java.io.File(filePath)))
    }

    fun decodeImageFromWebPFileNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPNoWrap(encodeFileNoWrap(java.io.File(filePath)))
    }

    fun decodeImageFromJPEGFileURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGURLSafe(encodeFileURLSafe(java.io.File(filePath)))
    }

    fun decodeImageFromPNGFileURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGURLSafe(encodeFileURLSafe(java.io.File(filePath)))
    }

    fun decodeImageFromWebPFileURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPURLSafe(encodeFileURLSafe(java.io.File(filePath)))
    }

    fun encodeImageToJPEGFileFromBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, stream)
            val encoded = encode(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decode(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFileFromBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            val encoded = encode(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decode(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFileFromBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.WEBP, quality, stream)
            val encoded = encode(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decode(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToJPEGFileFromBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, stream)
            val encoded = encodeNoWrap(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeNoWrap(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFileFromBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            val encoded = encodeNoWrap(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeNoWrap(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFileFromBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.WEBP, quality, stream)
            val encoded = encodeNoWrap(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeNoWrap(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToJPEGFileFromBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, stream)
            val encoded = encodeURLSafe(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeURLSafe(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToPNGFileFromBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            val encoded = encodeURLSafe(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeURLSafe(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun encodeImageToWebPFileFromBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return try {
            val stream = java.io.ByteArrayOutputStream()
            image.compress(android.graphics.Bitmap.CompressFormat.WEBP, quality, stream)
            val encoded = encodeURLSafe(stream.toByteArray())
            java.io.File(String(bytes)).writeBytes(decodeURLSafe(encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun decodeImageFromJPEGBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEG(encode(bytes))
    }

    fun decodeImageFromPNGBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNG(encode(bytes))
    }

    fun decodeImageFromWebPBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebP(encode(bytes))
    }

    fun decodeImageFromJPEGBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGNoWrap(encodeNoWrap(bytes))
    }

    fun decodeImageFromPNGBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGNoWrap(encodeNoWrap(bytes))
    }

    fun decodeImageFromWebPBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPNoWrap(encodeNoWrap(bytes))
    }

    fun decodeImageFromJPEGBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGURLSafe(encodeURLSafe(bytes))
    }

    fun decodeImageFromPNGBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGURLSafe(encodeURLSafe(bytes))
    }

    fun decodeImageFromWebPBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPURLSafe(encodeURLSafe(bytes))
    }

    fun encodeImageToJPEGFileFromBitmap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToJPEGFile(image, file, quality)
    }

    fun encodeImageToPNGFileFromBitmap(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return encodeImageToPNGFile(image, file)
    }

    fun encodeImageToWebPFileFromBitmap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToWebPFile(image, file, quality)
    }

    fun encodeImageToJPEGFileFromBitmapNoWrap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileNoWrap(image, file, quality)
    }

    fun encodeImageToPNGFileFromBitmapNoWrap(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return encodeImageToPNGFileNoWrap(image, file)
    }

    fun encodeImageToWebPFileFromBitmapNoWrap(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToWebPFileNoWrap(image, file, quality)
    }

    fun encodeImageToJPEGFileFromBitmapURLSafe(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileURLSafe(image, file, quality)
    }

    fun encodeImageToPNGFileFromBitmapURLSafe(image: android.graphics.Bitmap, file: java.io.File): Boolean {
        return encodeImageToPNGFileURLSafe(image, file)
    }

    fun encodeImageToWebPFileFromBitmapURLSafe(image: android.graphics.Bitmap, file: java.io.File, quality: Int = 90): Boolean {
        return encodeImageToWebPFileURLSafe(image, file, quality)
    }

    fun decodeImageFromJPEGFileFromBitmap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromJPEGFile(file.absolutePath)
    }

    fun decodeImageFromPNGFileFromBitmap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromPNGFile(file.absolutePath)
    }

    fun decodeImageFromWebPFileFromBitmap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromWebPFile(file.absolutePath)
    }

    fun decodeImageFromJPEGFileFromBitmapNoWrap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileNoWrap(file.absolutePath)
    }

    fun decodeImageFromPNGFileFromBitmapNoWrap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromPNGFileNoWrap(file.absolutePath)
    }

    fun decodeImageFromWebPFileFromBitmapNoWrap(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromWebPFileNoWrap(file.absolutePath)
    }

    fun decodeImageFromJPEGFileFromBitmapURLSafe(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileURLSafe(file.absolutePath)
    }

    fun decodeImageFromPNGFileFromBitmapURLSafe(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromPNGFileURLSafe(file.absolutePath)
    }

    fun decodeImageFromWebPFileFromBitmapURLSafe(file: java.io.File): android.graphics.Bitmap? {
        return decodeImageFromWebPFileURLSafe(file.absolutePath)
    }

    fun encodeImageToJPEGFileFromBitmapPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFile(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFile(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFile(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytes(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytes(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytes(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapString(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapString(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapString(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapStringNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapStringNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapStringNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapStringURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapStringURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapStringURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapString(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapString(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapString(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapStringNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapStringNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapStringNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapStringURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapStringURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapStringURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPath(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPath(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPath(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesString(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPath(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPath(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathNoWrap(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathNoWrap(image, filePath, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String): Boolean {
        return encodeImageToPNGFileFromBitmapPathURLSafe(image, filePath)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(image: android.graphics.Bitmap, filePath: String, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapPathURLSafe(image, filePath, quality)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPath(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPath(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPath(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathNoWrap(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathNoWrap(filePath)
    }

    fun decodeImageFromJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromJPEGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromPNGFileFromBitmapPathURLSafe(filePath)
    }

    fun decodeImageFromWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathURLSafe(filePath: String): android.graphics.Bitmap? {
        return decodeImageFromWebPFileFromBitmapPathURLSafe(filePath)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytes(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytes(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesNoWrap(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesNoWrap(image, bytes, quality)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToJPEGFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun encodeImageToPNGFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray): Boolean {
        return encodeImageToPNGFileFromBitmapBytesURLSafe(image, bytes)
    }

    fun encodeImageToWebPFileFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(image: android.graphics.Bitmap, bytes: ByteArray, quality: Int = 90): Boolean {
        return encodeImageToWebPFileFromBitmapBytesURLSafe(image, bytes, quality)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytes(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesNoWrap(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapNoWrap(bytes)
    }

    fun decodeImageFromJPEGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromJPEGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromPNGBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromPNGBytesFromBitmapURLSafe(bytes)
    }

    fun decodeImageFromWebPBytesFromBitmapBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesStringPathBytesURLSafe(bytes: ByteArray): android.graphics.Bitmap? {
        return decodeImageFromWebPBytesFromBitmapURLSafe(bytes)
    }

    fun encodeImageToJPEGFileFromBitmapBytesStringPathBytesStringPathBytesStringPath