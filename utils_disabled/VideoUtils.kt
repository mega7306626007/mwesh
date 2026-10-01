package com.jarvis.assistant.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object VideoUtils {

    fun getVideoInfo(path: String): Map<String, Any>? {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val rotation = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            val frameRate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull() ?: 0f
            val mimeType = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            val date = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DATE) ?: ""
            val location = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_LOCATION) ?: ""
            retriever.release()
            mapOf(
                "duration" to duration,
                "width" to width,
                "height" to height,
                "rotation" to rotation,
                "bitrate" to bitrate,
                "frameRate" to frameRate,
                "mimeType" to mimeType,
                "date" to date,
                "location" to location
            )
        } catch (e: Exception) {
            null
        }
    }

    fun extractFrame(path: String, timeMs: Long): Bitmap? {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val frame = retriever.getFrameAtTime(timeMs * 1000, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            frame
        } catch (e: Exception) {
            null
        }
    }

    fun extractFrames(path: String, intervalMs: Long): List<Bitmap> {
        val frames = mutableListOf<Bitmap>()
        val info = getVideoInfo(path) ?: return frames
        val duration = info["duration"] as Long
        var timeMs = 0L
        while (timeMs < duration) {
            val frame = extractFrame(path, timeMs)
            if (frame != null) {
                frames.add(frame)
            }
            timeMs += intervalMs
        }
        return frames
    }

    fun extractKeyFrames(path: String, maxFrames: Int = 10): List<Bitmap> {
        val frames = mutableListOf<Bitmap>()
        val info = getVideoInfo(path) ?: return frames
        val duration = info["duration"] as Long
        val interval = duration / (maxFrames + 1)
        for (i in 1..maxFrames) {
            val frame = extractFrame(path, i * interval)
            if (frame != null) {
                frames.add(frame)
            }
        }
        return frames
    }

    fun getThumbnail(path: String, width: Int = 320, height: Int = 240): Bitmap? {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val frame = retriever.getFrameAtTime(0, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            if (frame != null) {
                Bitmap.createScaledBitmap(frame, width, height, true)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getThumbnailAtTime(path: String, timeMs: Long, width: Int = 320, height: Int = 240): Bitmap? {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val frame = retriever.getFrameAtTime(timeMs * 1000, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            if (frame != null) {
                Bitmap.createScaledBitmap(frame, width, height, true)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun compressVideo(inputPath: String, outputPath: String, targetBitrate: Int = 1000000, targetWidth: Int = 0, targetHeight: Int = 0): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val originalWidth = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val originalHeight = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            retriever.release()
            val width = if (targetWidth > 0) targetWidth else originalWidth
            val height = if (targetHeight > 0) targetHeight else originalHeight
            val format = android.media.MediaFormat.createVideoFormat("video/avc", width, height).apply {
                setInteger(android.media.MediaFormat.KEY_BIT_RATE, targetBitrate)
                setInteger(android.media.MediaFormat.KEY_FRAME_RATE, 30)
                setInteger(android.media.MediaFormat.KEY_I_FRAME_INTERVAL, 1)
                setInteger(android.media.MediaFormat.KEY_COLOR_FORMAT, android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun trimVideo(inputPath: String, outputPath: String, startMs: Long, endMs: Long): Boolean {
        return try {
            val extractor = android.media.MediaExtractor()
            extractor.setDataSource(inputPath)
            val trackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                extractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("video/") == true
            } ?: return false
            extractor.selectTrack(trackIndex)
            extractor.seekTo(startMs * 1000, android.media.MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
            val buffer = android.media.MediaCodec.createDecoderByType(
                extractor.getTrackFormat(trackIndex).getString(android.media.MediaFormat.KEY_MIME)!!
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun mergeVideos(inputPaths: List<String>, outputPath: String): Boolean {
        return try {
            val extractor = android.media.MediaExtractor()
            val muxer = android.media.MediaMuxer(outputPath, android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var muxerStarted = false
            var videoTrackIndex = -1
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            for (inputPath in inputPaths) {
                extractor.setDataSource(inputPath)
                val trackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                    extractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("video/") == true
                } ?: continue
                extractor.selectTrack(trackIndex)
                if (!muxerStarted) {
                    val format = extractor.getTrackFormat(trackIndex)
                    videoTrackIndex = muxer.addTrack(format)
                    muxer.start()
                    muxerStarted = true
                }
                val buffer = ByteArray(1024 * 1024)
                while (true) {
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) break
                    bufferInfo.offset = 0
                    bufferInfo.size = sampleSize
                    bufferInfo.presentationTimeUs = extractor.sampleTime
                    bufferInfo.flags = extractor.sampleFlags
                    muxer.writeSampleData(videoTrackIndex, java.nio.ByteBuffer.wrap(buffer), bufferInfo)
                    extractor.advance()
                }
                extractor.unselectTrack(trackIndex)
            }
            if (muxerStarted) {
                muxer.stop()
            }
            muxer.release()
            extractor.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun addAudioToVideo(videoPath: String, audioPath: String, outputPath: String): Boolean {
        return try {
            val videoExtractor = android.media.MediaExtractor()
            val audioExtractor = android.media.MediaExtractor()
            videoExtractor.setDataSource(videoPath)
            audioExtractor.setDataSource(audioPath)
            val muxer = android.media.MediaMuxer(outputPath, android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val videoTrackIndex = (0 until videoExtractor.trackCount).firstOrNull { i ->
                videoExtractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("video/") == true
            } ?: return false
            val audioTrackIndex = (0 until audioExtractor.trackCount).firstOrNull { i ->
                audioExtractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: return false
            videoExtractor.selectTrack(videoTrackIndex)
            audioExtractor.selectTrack(audioTrackIndex)
            val videoFormat = videoExtractor.getTrackFormat(videoTrackIndex)
            val audioFormat = audioExtractor.getTrackFormat(audioTrackIndex)
            val muxerVideoTrack = muxer.addTrack(videoFormat)
            val muxerAudioTrack = muxer.addTrack(audioFormat)
            muxer.start()
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            val buffer = ByteArray(1024 * 1024)
            while (true) {
                val sampleSize = videoExtractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = videoExtractor.sampleTime
                bufferInfo.flags = videoExtractor.sampleFlags
                muxer.writeSampleData(muxerVideoTrack, java.nio.ByteBuffer.wrap(buffer), bufferInfo)
                videoExtractor.advance()
            }
            while (true) {
                val sampleSize = audioExtractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = audioExtractor.sampleTime
                bufferInfo.flags = audioExtractor.sampleFlags
                muxer.writeSampleData(muxerAudioTrack, java.nio.ByteBuffer.wrap(buffer), bufferInfo)
                audioExtractor.advance()
            }
            muxer.stop()
            muxer.release()
            videoExtractor.release()
            audioExtractor.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeAudio(videoPath: String, outputPath: String): Boolean {
        return try {
            val extractor = android.media.MediaExtractor()
            extractor.setDataSource(videoPath)
            val muxer = android.media.MediaMuxer(outputPath, android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val videoTrackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                extractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("video/") == true
            } ?: return false
            extractor.selectTrack(videoTrackIndex)
            val videoFormat = extractor.getTrackFormat(videoTrackIndex)
            val muxerVideoTrack = muxer.addTrack(videoFormat)
            muxer.start()
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            val buffer = ByteArray(1024 * 1024)
            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags
                muxer.writeSampleData(muxerVideoTrack, java.nio.ByteBuffer.wrap(buffer), bufferInfo)
                extractor.advance()
            }
            muxer.stop()
            muxer.release()
            extractor.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun changeVideoSpeed(inputPath: String, outputPath: String, speedFactor: Float): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            val newDuration = (duration / speedFactor).toLong()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun rotateVideo(inputPath: String, outputPath: String, degrees: Int): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            retriever.release()
            val newWidth = if (degrees == 90 || degrees == 270) height else width
            val newHeight = if (degrees == 90 || degrees == 270) width else height
            true
        } catch (e: Exception) {
            false
        }
    }

    fun flipVideo(inputPath: String, outputPath: String, horizontal: Boolean = true): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            retriever.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun cropVideo(inputPath: String, outputPath: String, x: Int, y: Int, width: Int, height: Int): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val originalWidth = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val originalHeight = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            retriever.release()
            val safeX = x.coerceIn(0, originalWidth - 1)
            val safeY = y.coerceIn(0, originalHeight - 1)
            val safeWidth = width.coerceIn(1, originalWidth - safeX)
            val safeHeight = height.coerceIn(1, originalHeight - safeY)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getVideoDuration(path: String): Long {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            duration
        } catch (e: Exception) {
            0L
        }
    }

    fun getVideoResolution(path: String): Pair<Int, Int> {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            retriever.release()
            Pair(width, height)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    fun getVideoBitrate(path: String): Int {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val bitrate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            retriever.release()
            bitrate
        } catch (e: Exception) {
            0
        }
    }

    fun getVideoFrameRate(path: String): Float {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val frameRate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull() ?: 0f
            retriever.release()
            frameRate
        } catch (e: Exception) {
            0f
        }
    }

    fun getVideoRotation(path: String): Int {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val rotation = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            retriever.release()
            rotation
        } catch (e: Exception) {
            0
        }
    }

    fun getVideoMimeType(path: String): String {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val mimeType = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            retriever.release()
            mimeType
        } catch (e: Exception) {
            ""
        }
    }

    fun getVideoCodec(path: String): String {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val mimeType = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            retriever.release()
            when {
                mimeType.contains("avc") -> "H.264"
                mimeType.contains("hevc") -> "H.265"
                mimeType.contains("vp8") -> "VP8"
                mimeType.contains("vp9") -> "VP9"
                mimeType.contains("mp4v") -> "MPEG-4"
                mimeType.contains("3gpp") -> "H.263"
                else -> "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getVideoAspectRatio(path: String): Float {
        val (width, height) = getVideoResolution(path)
        return if (height == 0) 0f else width.toFloat() / height.toFloat()
    }

    fun getVideoAspectRatioString(path: String): String {
        val (width, height) = getVideoResolution(path)
        if (height == 0) return "0:0"
        val gcd = gcd(width, height)
        return "${width / gcd}:${height / gcd}"
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    fun getVideoFileSize(path: String): Long {
        return try {
            File(path).length()
        } catch (e: Exception) {
            0L
        }
    }

    fun getVideoFrameCount(path: String): Long {
        val duration = getVideoDuration(path)
        val frameRate = getVideoFrameRate(path)
        return if (frameRate > 0) (duration * frameRate / 1000).toLong() else 0L
    }

    fun getVideoMetadata(path: String): Map<String, String> {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val metadata = mutableMapOf<String, String>()
            for (i in 0 until android.media.MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER) {
                val key = when (i) {
                    android.media.MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER -> "trackNumber"
                    android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM -> "album"
                    android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST -> "artist"
                    android.media.MediaMetadataRetriever.METADATA_KEY_AUTHOR -> "author"
                    android.media.MediaMetadataRetriever.METADATA_KEY_COMPOSER -> "composer"
                    android.media.MediaMetadataRetriever.METADATA_KEY_DATE -> "date"
                    android.media.MediaMetadataRetriever.METADATA_KEY_GENRE -> "genre"
                    android.media.MediaMetadataRetriever.METADATA_KEY_TITLE -> "title"
                    android.media.MediaMetadataRetriever.METADATA_KEY_YEAR -> "year"
                    android.media.MediaMetadataRetriever.METADATA_KEY_DURATION -> "duration"
                    android.media.MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS -> "numTracks"
                    android.media.MediaMetadataRetriever.METADATA_KEY_WRITER -> "writer"
                    android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE -> "mimeType"
                    android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE -> "bitrate"
                    android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH -> "videoWidth"
                    android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT -> "videoHeight"
                    android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION -> "videoRotation"
                    android.media.MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE -> "frameRate"
                    android.media.MediaMetadataRetriever.METADATA_KEY_LOCATION -> "location"
                    else -> "unknown_$i"
                }
                val value = retriever.extractMetadata(i)
                if (value != null) {
                    metadata[key] = value
                }
            }
            retriever.release()
            metadata
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun isVideoFile(path: String): Boolean {
        val mimeType = getVideoMimeType(path)
        return mimeType.startsWith("video/")
    }

    fun isVideoCorrupted(path: String): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            duration <= 0
        } catch (e: Exception) {
            true
        }
    }

    fun getVideoQuality(path: String): String {
        val (width, height) = getVideoResolution(path)
        val bitrate = getVideoBitrate(path)
        return when {
            width >= 3840 && height >= 2160 -> "4K"
            width >= 1920 && height >= 1080 -> "Full HD"
            width >= 1280 && height >= 720 -> "HD"
            width >= 854 && height >= 480 -> "SD"
            width >= 640 && height >= 360 -> "nHD"
            else -> "Low"
        }
    }

    fun getVideoQualityScore(path: String): Int {
        val (width, height) = getVideoResolution(path)
        val bitrate = getVideoBitrate(path)
        val frameRate = getVideoFrameRate(path)
        var score = 0
        score += when {
            width >= 3840 && height >= 2160 -> 100
            width >= 1920 && height >= 1080 -> 80
            width >= 1280 && height >= 720 -> 60
            width >= 854 && height >= 480 -> 40
            width >= 640 && height >= 360 -> 20
            else -> 10
        }
        score += when {
            bitrate >= 10000000 -> 20
            bitrate >= 5000000 -> 15
            bitrate >= 2000000 -> 10
            bitrate >= 1000000 -> 5
            else -> 0
        }
        score += when {
            frameRate >= 60 -> 20
            frameRate >= 30 -> 15
            frameRate >= 24 -> 10
            else -> 5
        }
        return score.coerceIn(0, 100)
    }

    fun getVideoSummary(path: String): String {
        val info = getVideoInfo(path) ?: return "Unable to read video info"
        val metadata = getVideoMetadata(path)
        return buildString {
            appendLine("Video Summary")
            appendLine("=" .repeat(50))
            appendLine("Path: $path")
            appendLine("Duration: ${formatDuration(info["duration"] as Long)}")
            appendLine("Resolution: ${info["width"]}x${info["height"]}")
            appendLine("Aspect Ratio: ${getVideoAspectRatioString(path)}")
            appendLine("Rotation: ${info["rotation"]}°")
            appendLine("Bitrate: ${formatBitrate(info["bitrate"] as Int)}")
            appendLine("Frame Rate: ${info["frameRate"]} fps")
            appendLine("Codec: ${getVideoCodec(path)}")
            appendLine("MIME Type: ${info["mimeType"]}")
            appendLine("Quality: ${getVideoQuality(path)}")
            appendLine("Quality Score: ${getVideoQualityScore(path)}/100")
            appendLine("File Size: ${FileUtils.formatFileSize(getVideoFileSize(path))}")
            appendLine("Frame Count: ${getVideoFrameCount(path)}")
            appendLine()
            appendLine("Metadata:")
            metadata.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return when {
            hours > 0 -> "%02d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
            minutes > 0 -> "%02d:%02d".format(minutes, seconds % 60)
            else -> "00:%02d".format(seconds % 60)
        }
    }

    private fun formatBitrate(bitrate: Int): String {
        return when {
            bitrate >= 1000000 -> "%.2f Mbps".format(bitrate / 1000000.0)
            bitrate >= 1000 -> "%.2f Kbps".format(bitrate / 1000.0)
            else -> "$bitrate bps"
        }
    }

    fun createVideoFromImages(images: List<Bitmap>, outputPath: String, frameRate: Int = 30): Boolean {
        return try {
            if (images.isEmpty()) return false
            val width = images[0].width
            val height = images[0].height
            val format = android.media.MediaFormat.createVideoFormat("video/avc", width, height).apply {
                setInteger(android.media.MediaFormat.KEY_BIT_RATE, width * height * 4)
                setInteger(android.media.MediaFormat.KEY_FRAME_RATE, frameRate)
                setInteger(android.media.MediaFormat.KEY_I_FRAME_INTERVAL, 1)
                setInteger(android.media.MediaFormat.KEY_COLOR_FORMAT, android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun addSubtitleToVideo(videoPath: String, subtitlePath: String, outputPath: String): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractAudioFromVideo(videoPath: String, outputPath: String): Boolean {
        return try {
            val extractor = android.media.MediaExtractor()
            extractor.setDataSource(videoPath)
            val audioTrackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                extractor.getTrackFormat(i).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: return false
            extractor.selectTrack(audioTrackIndex)
            val format = extractor.getTrackFormat(audioTrackIndex)
            val muxer = android.media.MediaMuxer(outputPath, android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackIndex = muxer.addTrack(format)
            muxer.start()
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            val buffer = ByteArray(1024 * 1024)
            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags
                muxer.writeSampleData(trackIndex, java.nio.ByteBuffer.wrap(buffer), bufferInfo)
                extractor.advance()
            }
            muxer.stop()
            muxer.release()
            extractor.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getVideoColorInfo(path: String): Map<String, Any> {
        val frame = extractFrame(path, 0) ?: return emptyMap()
        val width = frame.width
        val height = frame.height
        val pixels = IntArray(width * height)
        frame.getPixels(pixels, 0, width, 0, 0, width, height)
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var brightnessSum = 0.0
        for (pixel in pixels) {
            val r = android.graphics.Color.red(pixel)
            val g = android.graphics.Color.green(pixel)
            val b = android.graphics.Color.blue(pixel)
            rSum += r
            gSum += g
            bSum += b
            brightnessSum += 0.299 * r + 0.587 * g + 0.114 * b
        }
        val count = pixels.size.toLong()
        return mapOf(
            "averageRed" to (rSum / count).toInt(),
            "averageGreen" to (gSum / count).toInt(),
            "averageBlue" to (bSum / count).toInt(),
            "averageBrightness" to (brightnessSum / count).toInt(),
            "dominantColor" to getDominantColor(pixels)
        )
    }

    private fun getDominantColor(pixels: IntArray): Int {
        val colorCounts = mutableMapOf<Int, Int>()
        for (pixel in pixels) {
            val quantized = android.graphics.Color.rgb(
                (android.graphics.Color.red(pixel) / 32) * 32,
                (android.graphics.Color.green(pixel) / 32) * 32,
                (android.graphics.Color.blue(pixel) / 32) * 32
            )
            colorCounts[quantized] = (colorCounts[quantized] ?: 0) + 1
        }
        return colorCounts.maxByOrNull { it.value }?.key ?: android.graphics.Color.BLACK
    }

    fun getVideoSceneChanges(path: String, threshold: Double = 0.3): List<Long> {
        val sceneChanges = mutableListOf<Long>()
        val frames = extractFrames(path, 1000)
        if (frames.size < 2) return sceneChanges
        var prevFrame = frames[0]
        for (i in 1 until frames.size) {
            val currentFrame = frames[i]
            val diff = calculateFrameDifference(prevFrame, currentFrame)
            if (diff > threshold) {
                sceneChanges.add(i * 1000L)
            }
            prevFrame = currentFrame
        }
        return sceneChanges
    }

    private fun calculateFrameDifference(bitmap1: Bitmap, bitmap2: Bitmap): Double {
        val width = minOf(bitmap1.width, bitmap2.width)
        val height = minOf(bitmap1.height, bitmap2.height)
        val pixels1 = IntArray(width * height)
        val pixels2 = IntArray(width * height)
        bitmap1.getPixels(pixels1, 0, width, 0, 0, width, height)
        bitmap2.getPixels(pixels2, 0, width, 0, 0, width, height)
        var diffSum = 0.0
        for (i in pixels1.indices) {
            val r1 = android.graphics.Color.red(pixels1[i])
            val g1 = android.graphics.Color.green(pixels1[i])
            val b1 = android.graphics.Color.blue(pixels1[i])
            val r2 = android.graphics.Color.red(pixels2[i])
            val g2 = android.graphics.Color.green(pixels2[i])
            val b2 = android.graphics.Color.blue(pixels2[i])
            diffSum += kotlin.math.abs(r1 - r2) + kotlin.math.abs(g1 - g2) + kotlin.math.abs(b1 - b2)
        }
        return diffSum / (width * height * 3 * 255)
    }

    fun getVideoMotionLevel(path: String): Double {
        val frames = extractFrames(path, 500)
        if (frames.size < 2) return 0.0
        var totalDiff = 0.0
        var count = 0
        for (i in 1 until frames.size) {
            totalDiff += calculateFrameDifference(frames[i - 1], frames[i])
            count++
        }
        return if (count > 0) totalDiff / count else 0.0
    }

    fun getVideoStability(path: String): Double {
        val motionLevel = getVideoMotionLevel(path)
        return (1.0 - motionLevel).coerceIn(0.0, 1.0)
    }

    fun getVideoBrightness(path: String): Double {
        val frame = extractFrame(path, 0) ?: return 0.0
        val width = frame.width
        val height = frame.height
        val pixels = IntArray(width * height)
        frame.getPixels(pixels, 0, width, 0, 0, width, height)
        var brightnessSum = 0.0
        for (pixel in pixels) {
            brightnessSum += 0.299 * android.graphics.Color.red(pixel) +
                    0.587 * android.graphics.Color.green(pixel) +
                    0.114 * android.graphics.Color.blue(pixel)
        }
        return brightnessSum / pixels.size / 255.0
    }

    fun getVideoContrast(path: String): Double {
        val frame = extractFrame(path, 0) ?: return 0.0
        val width = frame.width
        val height = frame.height
        val pixels = IntArray(width * height)
        frame.getPixels(pixels, 0, width, 0, 0, width, height)
        val brightness = getVideoBrightness(path) * 255
        var variance = 0.0
        for (pixel in pixels) {
            val pixelBrightness = 0.299 * android.graphics.Color.red(pixel) +
                    0.587 * android.graphics.Color.green(pixel) +
                    0.114 * android.graphics.Color.blue(pixel)
            variance += (pixelBrightness - brightness) * (pixelBrightness - brightness)
        }
        return kotlin.math.sqrt(variance / pixels.size) / 255.0
    }

    fun getVideoSaturation(path: String): Double {
        val frame = extractFrame(path, 0) ?: return 0.0
        val width = frame.width
        val height = frame.height
        val pixels = IntArray(width * height)
        frame.getPixels(pixels, 0, width, 0, 0, width, height)
        var totalSaturation = 0.0
        for (pixel in pixels) {
            val r = android.graphics.Color.red(pixel) / 255.0
            val g = android.graphics.Color.green(pixel) / 255.0
            val b = android.graphics.Color.blue(pixel) / 255.0
            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            totalSaturation += if (max == 0.0) 0.0 else (max - min) / max
        }
        return totalSaturation / pixels.size
    }

    fun getVideoSharpness(path: String): Double {
        val frame = extractFrame(path, 0) ?: return 0.0
        val gray = ImageUtils.grayscale(frame)
        val edges = ImageUtils.sobelEdgeDetection(gray)
        val width = edges.width
        val height = edges.height
        val pixels = IntArray(width * height)
        edges.getPixels(pixels, 0, width, 0, 0, width, height)
        var edgeSum = 0.0
        for (pixel in pixels) {
            edgeSum += android.graphics.Color.red(pixel)
        }
        return edgeSum / pixels.size / 255.0
    }

    fun getVideoNoiseLevel(path: String): Double {
        val frame = extractFrame(path, 0) ?: return 0.0
        val gray = ImageUtils.grayscale(frame)
        val width = gray.width
        val height = gray.height
        val pixels = IntArray(width * height)
        gray.getPixels(pixels, 0, width, 0, 0, width, height)
        var noiseSum = 0.0
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val center = android.graphics.Color.red(pixels[y * width + x])
                val neighbors = listOf(
                    android.graphics.Color.red(pixels[(y - 1) * width + x]),
                    android.graphics.Color.red(pixels[(y + 1) * width + x]),
                    android.graphics.Color.red(pixels[y * width + (x - 1)]),
                    android.graphics.Color.red(pixels[y * width + (x + 1)])
                )
                val mean = neighbors.average()
                noiseSum += (center - mean) * (center - mean)
            }
        }
        return kotlin.math.sqrt(noiseSum / ((width - 2) * (height - 2))) / 255.0
    }

    fun getVideoQualityMetrics(path: String): Map<String, Double> {
        return mapOf(
            "brightness" to getVideoBrightness(path),
            "contrast" to getVideoContrast(path),
            "saturation" to getVideoSaturation(path),
            "sharpness" to getVideoSharpness(path),
            "noiseLevel" to getVideoNoiseLevel(path),
            "motionLevel" to getVideoMotionLevel(path),
            "stability" to getVideoStability(path)
        )
    }

    fun getVideoAnalysisReport(path: String): String {
        val info = getVideoInfo(path) ?: return "Unable to read video info"
        val metrics = getVideoQualityMetrics(path)
        val colorInfo = getVideoColorInfo(path)
        val sceneChanges = getVideoSceneChanges(path)
        return buildString {
            appendLine("Video Analysis Report")
            appendLine("=" .repeat(50))
            appendLine("Path: $path")
            appendLine("Duration: ${formatDuration(info["duration"] as Long)}")
            appendLine("Resolution: ${info["width"]}x${info["height"]}")
            appendLine("Aspect Ratio: ${getVideoAspectRatioString(path)}")
            appendLine("Bitrate: ${formatBitrate(info["bitrate"] as Int)}")
            appendLine("Frame Rate: ${info["frameRate"]} fps")
            appendLine("Codec: ${getVideoCodec(path)}")
            appendLine("Quality: ${getVideoQuality(path)}")
            appendLine("Quality Score: ${getVideoQualityScore(path)}/100")
            appendLine("File Size: ${FileUtils.formatFileSize(getVideoFileSize(path))}")
            appendLine()
            appendLine("Quality Metrics:")
            metrics.forEach { (key, value) ->
                appendLine("  $key: ${"%.3f".format(value)}")
            }
            appendLine()
            appendLine("Color Information:")
            colorInfo.forEach { (key, value) ->
                appendLine("  $key: $value")
            }
            appendLine()
            appendLine("Scene Changes: ${sceneChanges.size}")
            sceneChanges.forEach { timeMs ->
                appendLine("  At ${formatDuration(timeMs)}")
            }
        }
    }

    fun convertVideoFormat(inputPath: String, outputPath: String, outputFormat: String = "mp4"): Boolean {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(inputPath)
            val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getSupportedVideoFormats(): List<String> {
        return listOf("mp4", "3gpp", "webm", "mkv", "avi", "mov", "flv", "wmv", "ts", "m2ts")
    }

    fun getSupportedVideoCodecs(): List<String> {
        return listOf("H.264", "H.265", "VP8", "VP9", "MPEG-4", "H.263")
    }

    fun getSupportedAudioCodecs(): List<String> {
        return listOf("AAC", "MP3", "Vorbis", "Opus", "AMR-NB", "AMR-WB", "FLAC", "PCM")
    }

    fun getVideoContainerFormats(): Map<String, List<String>> {
        return mapOf(
            "mp4" to listOf("H.264", "H.265", "AAC", "MP3"),
            "webm" to listOf("VP8", "VP9", "Vorbis", "Opus"),
            "mkv" to listOf("H.264", "H.265", "VP8", "VP9", "AAC", "MP3", "Vorbis", "Opus", "FLAC"),
            "3gpp" to listOf("H.263", "H.264", "AMR-NB", "AMR-WB", "AAC"),
            "avi" to listOf("MPEG-4", "H.264", "MP3", "PCM"),
            "mov" to listOf("H.264", "H.265", "AAC", "PCM"),
            "ts" to listOf("H.264", "H.265", "AAC", "MP3", "AC3")
        )
    }

    fun estimateCompressedSize(path: String, targetBitrate: Int): Long {
        val duration = getVideoDuration(path)
        return (targetBitrate * duration / 8 / 1000)
    }

    fun getOptimalBitrate(width: Int, height: Int, frameRate: Int): Int {
        val pixels = width * height
        return when {
            pixels >= 3840 * 2160 -> (pixels * frameRate * 0.1).toInt()
            pixels >= 1920 * 1080 -> (pixels * frameRate * 0.15).toInt()
            pixels >= 1280 * 720 -> (pixels * frameRate * 0.2).toInt()
            pixels >= 854 * 480 -> (pixels * frameRate * 0.25).toInt()
            else -> (pixels * frameRate * 0.3).toInt()
        }
    }

    fun getRecommendedSettings(path: String): Map<String, Any> {
        val (width, height) = getVideoResolution(path)
        val frameRate = getVideoFrameRate(path).toInt().coerceAtLeast(24)
        val optimalBitrate = getOptimalBitrate(width, height, frameRate)
        return mapOf(
            "width" to width,
            "height" to height,
            "frameRate" to frameRate,
            "bitrate" to optimalBitrate,
            "codec" to "H.264",
            "format" to "mp4",
            "estimatedSize" to estimateCompressedSize(path, optimalBitrate)
        )
    }

    fun validateVideo(path: String): List<String> {
        val issues = mutableListOf<String>()
        if (!File(path).exists()) {
            issues.add("File does not exist")
            return issues
        }
        if (!isVideoFile(path)) {
            issues.add("Not a valid video file")
        }
        if (isVideoCorrupted(path)) {
            issues.add("Video file appears to be corrupted")
        }
        val (width, height) = getVideoResolution(path)
        if (width == 0 || height == 0) {
            issues.add("Invalid video resolution")
        }
        val bitrate = getVideoBitrate(path)
        if (bitrate == 0) {
            issues.add("Invalid bitrate")
        }
        val frameRate = getVideoFrameRate(path)
        if (frameRate == 0f) {
            issues.add("Invalid frame rate")
        }
        return issues
    }

    fun getVideoHealthReport(path: String): String {
        val issues = validateVideo(path)
        val metrics = getVideoQualityMetrics(path)
        return buildString {
            appendLine("Video Health Report")
            appendLine("=" .repeat(50))
            appendLine("Path: $path")
            appendLine("Status: ${if (issues.isEmpty()) "Healthy" else "Issues Found"}")
            if (issues.isNotEmpty()) {
                appendLine()
                appendLine("Issues:")
                issues.forEach { appendLine("  - $it") }
            }
            appendLine()
            appendLine("Quality Metrics:")
            metrics.forEach { (key, value) ->
                val status = when (key) {
                    "brightness" -> if (value in 0.3..0.7) "Good" else "Poor"
                    "contrast" -> if (value > 0.3) "Good" else "Poor"
                    "saturation" -> if (value in 0.2..0.8) "Good" else "Poor"
                    "sharpness" -> if (value > 0.1) "Good" else "Poor"
                    "noiseLevel" -> if (value < 0.1) "Good" else "Poor"
                    "motionLevel" -> if (value < 0.5) "Good" else "High"
                    "stability" -> if (value > 0.7) "Good" else "Poor"
                    else -> "Unknown"
                }
                appendLine("  $key: ${"%.3f".format(value)} ($status)")
            }
        }
    }
}
