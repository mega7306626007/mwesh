package com.mweshimiwa.assistant.utils

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object AudioUtils {

    private const val SAMPLE_RATE = 44100
    private const val CHANNELS = 2
    private const val BITS_PER_SAMPLE = 16
    private const val BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8

    fun generateSineWave(frequency: Double, durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * frequency * i / sampleRate
            val amplitude = (Short.MAX_VALUE * kotlin.math.sin(angle)).toInt().toShort()
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generateSquareWave(frequency: Double, durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        val period = sampleRate / frequency
        for (i in 0 until numSamples) {
            val amplitude = if ((i % period) < period / 2) Short.MAX_VALUE else Short.MIN_VALUE
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generateSawtoothWave(frequency: Double, durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        val period = sampleRate / frequency
        for (i in 0 until numSamples) {
            val phase = (i % period).toDouble() / period
            val amplitude = ((phase * 2 - 1) * Short.MAX_VALUE).toInt().toShort()
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generateTriangleWave(frequency: Double, durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        val period = sampleRate / frequency
        for (i in 0 until numSamples) {
            val phase = (i % period).toDouble() / period
            val amplitude = if (phase < 0.5) {
                ((phase * 4 - 1) * Short.MAX_VALUE).toInt().toShort()
            } else {
                ((3 - phase * 4) * Short.MAX_VALUE).toInt().toShort()
            }
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generateWhiteNoise(durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        for (i in 0 until numSamples) {
            val amplitude = ((Math.random() * 2 - 1) * Short.MAX_VALUE).toInt().toShort()
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generatePinkNoise(durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        var b0 = 0.0; var b1 = 0.0; var b2 = 0.0; var b3 = 0.0; var b4 = 0.0; var b5 = 0.0; var b6 = 0.0
        for (i in 0 until numSamples) {
            val white = Math.random() * 2 - 1
            b0 = 0.99886 * b0 + white * 0.0555179
            b1 = 0.99332 * b1 + white * 0.0750759
            b2 = 0.96900 * b2 + white * 0.1538520
            b3 = 0.86650 * b3 + white * 0.3104856
            b4 = 0.55000 * b4 + white * 0.5329522
            b5 = -0.7616 * b5 - white * 0.0168980
            val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
            b6 = white * 0.115926
            val amplitude = (pink * 0.11 * Short.MAX_VALUE).toInt().toShort()
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun generateBrownNoise(durationSeconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val numSamples = (durationSeconds * sampleRate).toInt()
        val data = ByteArray(numSamples * 2)
        var lastOut = 0.0
        for (i in 0 until numSamples) {
            val white = Math.random() * 2 - 1
            lastOut = (lastOut + 0.02 * white) / 1.02
            val amplitude = (lastOut * 3.5 * Short.MAX_VALUE).toInt().toShort()
            data[i * 2] = (amplitude.toInt() and 0xFF).toByte()
            data[i * 2 + 1] = ((amplitude.toInt() shr 8) and 0xFF).toByte()
        }
        return data
    }

    fun mixAudio(vararg audioData: ByteArray): ByteArray {
        val minLength = audioData.minOfOrNull { it.size } ?: return ByteArray(0)
        val result = ByteArray(minLength)
        for (i in 0 until minLength) {
            var sum = 0
            for (data in audioData) {
                sum += data[i].toInt()
            }
            result[i] = (sum / audioData.size).toByte()
        }
        return result
    }

    fun concatenateAudio(vararg audioData: ByteArray): ByteArray {
        val totalLength = audioData.sumOf { it.size }
        val result = ByteArray(totalLength)
        var offset = 0
        for (data in audioData) {
            System.arraycopy(data, 0, result, offset, data.size)
            offset += data.size
        }
        return result
    }

    fun adjustVolume(audioData: ByteArray, volume: Float): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val adjusted = (sample * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun fadeIn(audioData: ByteArray, durationMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val fadeSamples = (durationMs * sampleRate / 1000).coerceAtMost(audioData.size / 2)
        for (i in 0 until fadeSamples) {
            val factor = i.toFloat() / fadeSamples
            val sample = ((result[i * 2 + 1].toInt() shl 8) or (result[i * 2].toInt() and 0xFF)).toShort()
            val adjusted = (sample * factor).toInt().toShort()
            result[i * 2] = (adjusted.toInt() and 0xFF).toByte()
            result[i * 2 + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun fadeOut(audioData: ByteArray, durationMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val fadeSamples = (durationMs * sampleRate / 1000).coerceAtMost(audioData.size / 2)
        val startSample = audioData.size / 2 - fadeSamples
        for (i in 0 until fadeSamples) {
            val factor = (fadeSamples - i).toFloat() / fadeSamples
            val idx = (startSample + i) * 2
            val sample = ((result[idx + 1].toInt() shl 8) or (result[idx].toInt() and 0xFF)).toShort()
            val adjusted = (sample * factor).toInt().toShort()
            result[idx] = (adjusted.toInt() and 0xFF).toByte()
            result[idx + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun reverse(audioData: ByteArray): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in audioData.indices step 2) {
            val j = audioData.size - 2 - i
            result[i] = audioData[j]
            result[i + 1] = audioData[j + 1]
        }
        return result
    }

    fun changeSpeed(audioData: ByteArray, speedFactor: Float): ByteArray {
        val newLength = (audioData.size / speedFactor).toInt()
        val result = ByteArray(newLength)
        for (i in 0 until newLength step 2) {
            val srcIdx = (i * speedFactor).toInt()
            if (srcIdx + 1 < audioData.size) {
                result[i] = audioData[srcIdx]
                result[i + 1] = audioData[srcIdx + 1]
            }
        }
        return result
    }

    fun changePitch(audioData: ByteArray, semitones: Int): ByteArray {
        val pitchFactor = kotlin.math.pow(2.0, semitones / 12.0).toFloat()
        return changeSpeed(audioData, pitchFactor)
    }

    fun lowPassFilter(audioData: ByteArray, cutoffFreq: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        val dt = 1.0 / sampleRate
        val rc = 1.0 / (2 * Math.PI * cutoffFreq)
        val alpha = dt / (rc + dt)
        var prevSample = 0.0
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            prevSample = prevSample + alpha * (sample - prevSample)
            val filtered = prevSample.toInt().toShort()
            result[i] = (filtered.toInt() and 0xFF).toByte()
            result[i + 1] = ((filtered.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun highPassFilter(audioData: ByteArray, cutoffFreq: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        val dt = 1.0 / sampleRate
        val rc = 1.0 / (2 * Math.PI * cutoffFreq)
        val alpha = rc / (rc + dt)
        var prevSample = 0.0
        var prevInput = 0.0
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val filtered = alpha * (prevSample + sample - prevInput)
            prevSample = filtered
            prevInput = sample.toDouble()
            val resultSample = filtered.toInt().toShort()
            result[i] = (resultSample.toInt() and 0xFF).toByte()
            result[i + 1] = ((resultSample.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun bandPassFilter(audioData: ByteArray, lowFreq: Double, highFreq: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val lowPassed = lowPassFilter(audioData, highFreq, sampleRate)
        return highPassFilter(lowPassed, lowFreq, sampleRate)
    }

    fun echo(audioData: ByteArray, delayMs: Int, decay: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val delaySamples = delayMs * sampleRate / 1000 * 2
        for (i in delaySamples until audioData.size step 2) {
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
            val echoSample = ((result[i - delaySamples + 1].toInt() shl 8) or (result[i - delaySamples].toInt() and 0xFF)).toShort()
            val mixed = (sample + echoSample * decay).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (mixed.toInt() and 0xFF).toByte()
            result[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun reverb(audioData: ByteArray, roomSize: Float, damping: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val delaySamples = (roomSize * sampleRate / 1000).toInt() * 2
        for (i in delaySamples until audioData.size step 2) {
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
            val reverbSample = ((result[i - delaySamples + 1].toInt() shl 8) or (result[i - delaySamples].toInt() and 0xFF)).toShort()
            val mixed = (sample + reverbSample * (1 - damping)).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (mixed.toInt() and 0xFF).toByte()
            result[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun distortion(audioData: ByteArray, gain: Float): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val distorted = kotlin.math.tanh(sample * gain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (distorted.toInt() and 0xFF).toByte()
            result[i + 1] = ((distorted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun bitcrusher(audioData: ByteArray, bitDepth: Int): ByteArray {
        val result = ByteArray(audioData.size)
        val levels = (1 shl bitDepth) - 1
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val crushed = ((sample.toFloat() / Short.MAX_VALUE * levels).toInt() / levels.toFloat() * Short.MAX_VALUE).toInt().toShort()
            result[i] = (crushed.toInt() and 0xFF).toByte()
            result[i + 1] = ((crushed.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun normalize(audioData: ByteArray): ByteArray {
        var maxSample = 0
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            maxSample = maxOf(maxSample, kotlin.math.abs(sample.toInt()))
        }
        if (maxSample == 0) return audioData
        val factor = Short.MAX_VALUE.toFloat() / maxSample
        return adjustVolume(audioData, factor)
    }

    fun getRMS(audioData: ByteArray): Double {
        var sum = 0.0
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            sum += sample * sample
        }
        return kotlin.math.sqrt(sum / (audioData.size / 2))
    }

    fun getPeak(audioData: ByteArray): Short {
        var peak = 0
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            peak = maxOf(peak, kotlin.math.abs(sample.toInt()))
        }
        return peak.toShort()
    }

    fun getDurationMs(audioData: ByteArray, sampleRate: Int = SAMPLE_RATE): Long {
        return (audioData.size / 2 * 1000L) / sampleRate
    }

    fun getFrequencySpectrum(audioData: ByteArray, fftSize: Int = 1024): DoubleArray {
        val real = DoubleArray(fftSize)
        val imag = DoubleArray(fftSize)
        val numSamples = minOf(fftSize, audioData.size / 2)
        for (i in 0 until numSamples) {
            real[i] = ((audioData[i * 2 + 1].toInt() shl 8) or (audioData[i * 2].toInt() and 0xFF)).toShort().toDouble()
        }
        fft(real, imag)
        val magnitudes = DoubleArray(fftSize / 2)
        for (i in 0 until fftSize / 2) {
            magnitudes[i] = kotlin.math.sqrt(real[i] * real[i] + imag[i] * imag[i])
        }
        return magnitudes
    }

    private fun fft(real: DoubleArray, imag: DoubleArray) {
        val n = real.size
        if (n <= 1) return
        val evenReal = DoubleArray(n / 2)
        val evenImag = DoubleArray(n / 2)
        val oddReal = DoubleArray(n / 2)
        val oddImag = DoubleArray(n / 2)
        for (i in 0 until n / 2) {
            evenReal[i] = real[i * 2]
            evenImag[i] = imag[i * 2]
            oddReal[i] = real[i * 2 + 1]
            oddImag[i] = imag[i * 2 + 1]
        }
        fft(evenReal, evenImag)
        fft(oddReal, oddImag)
        for (k in 0 until n / 2) {
            val angle = -2 * Math.PI * k / n
            val cos = kotlin.math.cos(angle)
            val sin = kotlin.math.sin(angle)
            val tReal = cos * oddReal[k] - sin * oddImag[k]
            val tImag = cos * oddImag[k] + sin * oddReal[k]
            real[k] = evenReal[k] + tReal
            imag[k] = evenImag[k] + tImag
            real[k + n / 2] = evenReal[k] - tReal
            imag[k + n / 2] = evenImag[k] - tImag
        }
    }

    fun getDominantFrequency(audioData: ByteArray, sampleRate: Int = SAMPLE_RATE): Double {
        val spectrum = getFrequencySpectrum(audioData)
        var maxIndex = 0
        var maxValue = 0.0
        for (i in spectrum.indices) {
            if (spectrum[i] > maxValue) {
                maxValue = spectrum[i]
                maxIndex = i
            }
        }
        return maxIndex.toDouble() * sampleRate / spectrum.size / 2
    }

    fun applyEqualizer(audioData: ByteArray, gains: DoubleArray, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val fftSize = 1024
        val real = DoubleArray(fftSize)
        val imag = DoubleArray(fftSize)
        val numSamples = minOf(fftSize, audioData.size / 2)
        for (i in 0 until numSamples) {
            real[i] = ((audioData[i * 2 + 1].toInt() shl 8) or (audioData[i * 2].toInt() and 0xFF)).toShort().toDouble()
        }
        fft(real, imag)
        val numBands = gains.size
        for (i in 0 until fftSize / 2) {
            val band = (i * numBands / (fftSize / 2)).coerceIn(0, numBands - 1)
            real[i] *= gains[band]
            imag[i] *= gains[band]
        }
        ifft(real, imag)
        val result = ByteArray(audioData.size)
        for (i in 0 until numSamples) {
            val sample = real[i].toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i * 2] = (sample.toInt() and 0xFF).toByte()
            result[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    private fun ifft(real: DoubleArray, imag: DoubleArray) {
        val n = real.size
        for (i in imag.indices) imag[i] = -imag[i]
        fft(real, imag)
        for (i in imag.indices) imag[i] = -imag[i] / n
        for (i in real.indices) real[i] /= n
    }

    fun createWavHeader(dataSize: Int, sampleRate: Int = SAMPLE_RATE, channels: Int = CHANNELS, bitsPerSample: Int = BITS_PER_SAMPLE): ByteArray {
        val header = ByteArray(44)
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        writeIntLE(header, 4, 36 + dataSize)
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        writeIntLE(header, 16, 16)
        writeShortLE(header, 20, 1)
        writeShortLE(header, 22, channels.toShort())
        writeIntLE(header, 24, sampleRate)
        writeIntLE(header, 28, byteRate)
        writeShortLE(header, 32, blockAlign.toShort())
        writeShortLE(header, 34, bitsPerSample.toShort())
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        writeIntLE(header, 40, dataSize)
        return header
    }

    private fun writeIntLE(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xFF).toByte()
        data[offset + 1] = ((value shr 8) and 0xFF).toByte()
        data[offset + 2] = ((value shr 16) and 0xFF).toByte()
        data[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun writeShortLE(data: ByteArray, offset: Int, value: Short) {
        data[offset] = (value.toInt() and 0xFF).toByte()
        data[offset + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
    }

    fun saveToWav(audioData: ByteArray, file: File, sampleRate: Int = SAMPLE_RATE, channels: Int = CHANNELS, bitsPerSample: Int = BITS_PER_SAMPLE) {
        val header = createWavHeader(audioData.size, sampleRate, channels, bitsPerSample)
        FileOutputStream(file).use { fos ->
            fos.write(header)
            fos.write(audioData)
        }
    }

    fun loadFromWav(file: File): ByteArray? {
        return try {
            val fis = FileInputStream(file)
            val header = ByteArray(44)
            fis.read(header)
            val dataSize = ((header[40].toInt() and 0xFF) or
                    ((header[41].toInt() and 0xFF) shl 8) or
                    ((header[42].toInt() and 0xFF) shl 16) or
                    ((header[43].toInt() and 0xFF) shl 24))
            val data = ByteArray(dataSize)
            fis.read(data)
            fis.close()
            data
        } catch (e: Exception) {
            null
        }
    }

    fun getWavInfo(file: File): Map<String, Any>? {
        return try {
            val fis = FileInputStream(file)
            val header = ByteArray(44)
            fis.read(header)
            fis.close()
            val channels = (header[22].toInt() and 0xFF) or ((header[23].toInt() and 0xFF) shl 8)
            val sampleRate = (header[24].toInt() and 0xFF) or
                    ((header[25].toInt() and 0xFF) shl 8) or
                    ((header[26].toInt() and 0xFF) shl 16) or
                    ((header[27].toInt() and 0xFF) shl 24)
            val bitsPerSample = (header[34].toInt() and 0xFF) or ((header[35].toInt() and 0xFF) shl 8)
            val dataSize = (header[40].toInt() and 0xFF) or
                    ((header[41].toInt() and 0xFF) shl 8) or
                    ((header[42].toInt() and 0xFF) shl 16) or
                    ((header[43].toInt() and 0xFF) shl 24)
            val durationMs = (dataSize * 1000L) / (sampleRate * channels * bitsPerSample / 8)
            mapOf(
                "channels" to channels,
                "sampleRate" to sampleRate,
                "bitsPerSample" to bitsPerSample,
                "dataSize" to dataSize,
                "durationMs" to durationMs
            )
        } catch (e: Exception) {
            null
        }
    }

    fun convertSampleRate(audioData: ByteArray, originalRate: Int, targetRate: Int): ByteArray {
        val ratio = targetRate.toDouble() / originalRate.toDouble()
        val newLength = (audioData.size * ratio).toInt()
        val result = ByteArray(newLength)
        for (i in 0 until newLength step 2) {
            val srcIdx = (i / ratio).toInt()
            if (srcIdx + 1 < audioData.size) {
                result[i] = audioData[srcIdx]
                result[i + 1] = audioData[srcIdx + 1]
            }
        }
        return result
    }

    fun convertChannels(audioData: ByteArray, originalChannels: Int, targetChannels: Int): ByteArray {
        if (originalChannels == targetChannels) return audioData
        val bytesPerSample = 2
        val originalFrameSize = originalChannels * bytesPerSample
        val targetFrameSize = targetChannels * bytesPerSample
        val numFrames = audioData.size / originalFrameSize
        val result = ByteArray(numFrames * targetFrameSize)
        for (frame in 0 until numFrames) {
            for (channel in 0 until targetChannels) {
                val srcChannel = channel % originalChannels
                val srcIdx = frame * originalFrameSize + srcChannel * bytesPerSample
                val dstIdx = frame * targetFrameSize + channel * bytesPerSample
                result[dstIdx] = audioData[srcIdx]
                result[dstIdx + 1] = audioData[srcIdx + 1]
            }
        }
        return result
    }

    fun convertBitDepth(audioData: ByteArray, originalBits: Int, targetBits: Int): ByteArray {
        if (originalBits == targetBits) return audioData
        val originalBytes = originalBits / 8
        val targetBytes = targetBits / 8
        val numSamples = audioData.size / originalBytes
        val result = ByteArray(numSamples * targetBytes)
        for (i in 0 until numSamples) {
            val srcIdx = i * originalBytes
            val dstIdx = i * targetBytes
            if (originalBits == 16 && targetBits == 8) {
                val sample = ((audioData[srcIdx + 1].toInt() shl 8) or (audioData[srcIdx].toInt() and 0xFF)).toShort()
                result[dstIdx] = (sample / 256).toByte()
            } else if (originalBits == 8 && targetBits == 16) {
                val sample = audioData[srcIdx].toInt()
                result[dstIdx] = (sample - 128).toByte()
                result[dstIdx + 1] = 0
            }
        }
        return result
    }

    fun applyEnvelope(audioData: ByteArray, attackMs: Int, decayMs: Int, sustainLevel: Float, releaseMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val attackSamples = attackMs * sampleRate / 1000 * 2
        val decaySamples = decayMs * sampleRate / 1000 * 2
        val releaseSamples = releaseMs * sampleRate / 1000 * 2
        val totalSamples = audioData.size
        val sustainStart = attackSamples + decaySamples
        val releaseStart = totalSamples - releaseSamples
        for (i in 0 until totalSamples step 2) {
            val envelope = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i < sustainStart -> 1.0f - (1.0f - sustainLevel) * (i - attackSamples).toFloat() / decaySamples
                i < releaseStart -> sustainLevel
                else -> sustainLevel * (totalSamples - i).toFloat() / releaseSamples
            }
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
            val adjusted = (sample * envelope).toInt().toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyCompressor(audioData: ByteArray, threshold: Float, ratio: Float, attackMs: Int, releaseMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val attackSamples = attackMs * sampleRate / 1000 * 2
        val releaseSamples = releaseMs * sampleRate / 1000 * 2
        var envelope = 0.0
        for (i in 0 until result.size step 2) {
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
            val absSample = kotlin.math.abs(sample.toInt()).toDouble() / Short.MAX_VALUE
            val targetEnvelope = if (absSample > envelope) {
                envelope + (absSample - envelope) / attackSamples
            } else {
                envelope + (absSample - envelope) / releaseSamples
            }
            envelope = targetEnvelope
            val gain = if (envelope > threshold) {
                kotlin.math.pow(threshold / envelope, 1.0 - 1.0 / ratio).toFloat()
            } else {
                1.0f
            }
            val adjusted = (sample * gain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyGate(audioData: ByteArray, threshold: Float, attackMs: Int, holdMs: Int, releaseMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val attackSamples = attackMs * sampleRate / 1000 * 2
        val holdSamples = holdMs * sampleRate / 1000 * 2
        val releaseSamples = releaseMs * sampleRate / 1000 * 2
        var envelope = 0.0
        var holdCounter = 0
        for (i in 0 until result.size step 2) {
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
            val absSample = kotlin.math.abs(sample.toInt()).toDouble() / Short.MAX_VALUE
            envelope = if (absSample > envelope) {
                absSample
            } else {
                envelope * 0.999
            }
            val gain = when {
                envelope > threshold -> {
                    holdCounter = holdSamples
                    1.0f
                }
                holdCounter > 0 -> {
                    holdCounter--
                    1.0f
                }
                else -> {
                    (envelope / threshold).toFloat().coerceIn(0f, 1f)
                }
            }
            val adjusted = (sample * gain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyLimiter(audioData: ByteArray, threshold: Float): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val limited = sample.toInt().coerceIn((Short.MIN_VALUE * threshold).toInt(), (Short.MAX_VALUE * threshold).toInt()).toShort()
            result[i] = (limited.toInt() and 0xFF).toByte()
            result[i + 1] = ((limited.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyExpander(audioData: ByteArray, threshold: Float, ratio: Float): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in audioData.indices step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val absSample = kotlin.math.abs(sample.toInt()).toFloat() / Short.MAX_VALUE
            val gain = if (absSample < threshold) {
                kotlin.math.pow(absSample / threshold, ratio)
            } else {
                1.0f
            }
            val adjusted = (sample * gain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyChorus(audioData: ByteArray, rate: Float, depth: Float, delayMs: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val delaySamples = delayMs * sampleRate / 1000 * 2
        val maxDepthSamples = (depth * sampleRate / 1000).toInt() * 2
        for (i in delaySamples until audioData.size step 2) {
            val lfo = kotlin.math.sin(2 * Math.PI * rate * i / sampleRate)
            val offset = (delaySamples + lfo * maxDepthSamples).toInt()
            if (offset + 1 < audioData.size) {
                val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
                val chorusSample = ((result[offset + 1].toInt() shl 8) or (result[offset].toInt() and 0xFF)).toShort()
                val mixed = (sample + chorusSample * 0.5f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                result[i] = (mixed.toInt() and 0xFF).toByte()
                result[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()
            }
        }
        return result
    }

    fun applyFlanger(audioData: ByteArray, rate: Float, depth: Float, delayMs: Int, feedback: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val delaySamples = delayMs * sampleRate / 1000 * 2
        val maxDepthSamples = (depth * sampleRate / 1000).toInt() * 2
        for (i in delaySamples until audioData.size step 2) {
            val lfo = kotlin.math.sin(2 * Math.PI * rate * i / sampleRate)
            val offset = (delaySamples + lfo * maxDepthSamples).toInt()
            if (offset + 1 < audioData.size) {
                val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort()
                val flangerSample = ((result[offset + 1].toInt() shl 8) or (result[offset].toInt() and 0xFF)).toShort()
                val mixed = (sample + flangerSample * feedback).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                result[i] = (mixed.toInt() and 0xFF).toByte()
                result[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()
            }
        }
        return result
    }

    fun applyPhaser(audioData: ByteArray, rate: Float, depth: Float, stages: Int, feedback: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = audioData.copyOf()
        val allpassFilters = Array(stages) { doubleArrayOf(0.0, 0.0) }
        for (i in 0 until result.size step 2) {
            val sample = ((result[i + 1].toInt() shl 8) or (result[i].toInt() and 0xFF)).toShort().toDouble()
            val lfo = kotlin.math.sin(2 * Math.PI * rate * i / sampleRate)
            val coefficient = (kotlin.math.tan(Math.PI * 1000 / sampleRate) - 1) / (kotlin.math.tan(Math.PI * 1000 / sampleRate) + 1)
            var output = sample
            for (stage in 0 until stages) {
                val filtered = coefficient * output + allpassFilters[stage][0] - coefficient * allpassFilters[stage][1]
                allpassFilters[stage][0] = output
                allpassFilters[stage][1] = filtered
                output = filtered
            }
            val mixed = (sample + output * depth).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (mixed.toInt() and 0xFF).toByte()
            result[i + 1] = ((mixed.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyTremolo(audioData: ByteArray, rate: Float, depth: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val lfo = (1 - depth / 2) + depth / 2 * kotlin.math.sin(2 * Math.PI * rate * i / sampleRate).toFloat()
            val adjusted = (sample * lfo).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (adjusted.toInt() and 0xFF).toByte()
            result[i + 1] = ((adjusted.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyVibrato(audioData: ByteArray, rate: Float, depth: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        val maxDelaySamples = (depth * sampleRate / 1000).toInt() * 2
        for (i in 0 until result.size step 2) {
            val lfo = kotlin.math.sin(2 * Math.PI * rate * i / sampleRate)
            val delay = (maxDelaySamples / 2 + lfo * maxDelaySamples / 2).toInt()
            val srcIdx = i - delay
            if (srcIdx >= 0 && srcIdx + 1 < audioData.size) {
                result[i] = audioData[srcIdx]
                result[i + 1] = audioData[srcIdx + 1]
            }
        }
        return result
    }

    fun applyWahWah(audioData: ByteArray, rate: Float, depth: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val lfo = kotlin.math.sin(2 * Math.PI * rate * i / sampleRate)
            val cutoff = 500 + lfo * depth * 2000
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val filtered = lowPassFilter(byteArrayOf(audioData[i], audioData[i + 1]), cutoff, sampleRate)
            result[i] = filtered[0]
            result[i + 1] = filtered[1]
        }
        return result
    }

    fun applyAutoWah(audioData: ByteArray, sensitivity: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        var envelope = 0.0
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val absSample = kotlin.math.abs(sample.toInt()).toDouble() / Short.MAX_VALUE
            envelope = if (absSample > envelope) {
                envelope + (absSample - envelope) * sensitivity
            } else {
                envelope * 0.999
            }
            val cutoff = 500 + envelope * 4000
            val filtered = lowPassFilter(byteArrayOf(audioData[i], audioData[i + 1]), cutoff, sampleRate)
            result[i] = filtered[0]
            result[i + 1] = filtered[1]
        }
        return result
    }

    fun applyRingModulation(audioData: ByteArray, modFreq: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val modulator = kotlin.math.sin(2 * Math.PI * modFreq * i / sampleRate).toFloat()
            val modulated = (sample * modulator).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (modulated.toInt() and 0xFF).toByte()
            result[i + 1] = ((modulated.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyFrequencyModulation(audioData: ByteArray, modFreq: Float, modulationIndex: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        var phase = 0.0
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val modulator = kotlin.math.sin(2 * Math.PI * modFreq * i / sampleRate).toFloat()
            phase += 2 * Math.PI * (modFreq + modulationIndex * modulator) / sampleRate
            val modulated = (sample * kotlin.math.sin(phase)).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (modulated.toInt() and 0xFF).toByte()
            result[i + 1] = ((modulated.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyAmplitudeModulation(audioData: ByteArray, modFreq: Float, modulationDepth: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val modulator = 1 - modulationDepth / 2 + modulationDepth / 2 * kotlin.math.sin(2 * Math.PI * modFreq * i / sampleRate).toFloat()
            val modulated = (sample * modulator).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (modulated.toInt() and 0xFF).toByte()
            result[i + 1] = ((modulated.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyPulseWidthModulation(audioData: ByteArray, pwmFreq: Float, dutyCycle: Float, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val phase = (i * pwmFreq / sampleRate) % 1.0
            val pwm = if (phase < dutyCycle) 1.0f else -1.0f
            val modulated = (sample * pwm).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            result[i] = (modulated.toInt() and 0xFF).toByte()
            result[i + 1] = ((modulated.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applyWaveShaping(audioData: ByteArray, shape: (Float) -> Float): ByteArray {
        val result = ByteArray(audioData.size)
        for (i in 0 until result.size step 2) {
            val sample = ((audioData[i + 1].toInt() shl 8) or (audioData[i].toInt() and 0xFF)).toShort()
            val normalized = sample.toFloat() / Short.MAX_VALUE
            val shaped = shape(normalized).coerceIn(-1f, 1f)
            val modulated = (shaped * Short.MAX_VALUE).toInt().toShort()
            result[i] = (modulated.toInt() and 0xFF).toByte()
            result[i + 1] = ((modulated.toInt() shr 8) and 0xFF).toByte()
        }
        return result
    }

    fun applySoftClipping(audioData: ByteArray, threshold: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            if (sample > threshold) {
                threshold + (1 - threshold) * kotlin.math.tanh((sample - threshold) / (1 - threshold))
            } else if (sample < -threshold) {
                -threshold + (1 - threshold) * kotlin.math.tanh((sample + threshold) / (1 - threshold))
            } else {
                sample
            }
        }
    }

    fun applyHardClipping(audioData: ByteArray, threshold: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            sample.coerceIn(-threshold, threshold)
        }
    }

    fun applyTubeSaturation(audioData: ByteArray, drive: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * drive
            (kotlin.math.exp(driven) - kotlin.math.exp(-driven)) / (kotlin.math.exp(driven) + kotlin.math.exp(-driven))
        }
    }

    fun applyTapeSaturation(audioData: ByteArray, drive: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * drive
            driven / (1 + kotlin.math.abs(driven))
        }
    }

    fun applyTransistorSaturation(audioData: ByteArray, drive: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * drive
            if (driven > 0) {
                1 - kotlin.math.exp(-driven)
            } else {
                -1 + kotlin.math.exp(driven)
            }
        }
    }

    fun applyDiodeSaturation(audioData: ByteArray, drive: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * drive
            if (driven > 0) {
                1 - kotlin.math.exp(-driven)
            } else {
                0f
            }
        }
    }

    fun applyFuzz(audioData: ByteArray, gain: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * gain
            if (driven > 0) {
                1 - kotlin.math.exp(-driven)
            } else {
                -1 + kotlin.math.exp(driven)
            }
        }
    }

    fun applyOverdrive(audioData: ByteArray, drive: Float, tone: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * drive
            val shaped = if (driven > 0) {
                1 - kotlin.math.exp(-driven)
            } else {
                -1 + kotlin.math.exp(driven)
            }
            shaped * tone + sample * (1 - tone)
        }
    }

    fun applyDistortion2(audioData: ByteArray, gain: Float, tone: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * gain
            val shaped = kotlin.math.tanh(driven)
            shaped * tone + sample * (1 - tone)
        }
    }

    fun applyCrunch(audioData: ByteArray, gain: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val driven = sample * gain
            if (driven > 1) {
                1f
            } else if (driven < -1) {
                -1f
            } else {
                driven
            }
        }
    }

    fun applyRectifier(audioData: ByteArray, fullWave: Boolean = true): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            if (fullWave) {
                kotlin.math.abs(sample)
            } else {
                if (sample > 0) sample else 0f
            }
        }
    }

    fun applyHalfWaveRectifier(audioData: ByteArray): ByteArray {
        return applyRectifier(audioData, false)
    }

    fun applyFullWaveRectifier(audioData: ByteArray): ByteArray {
        return applyRectifier(audioData, true)
    }

    fun applySquarer(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            sample * sample * if (sample > 0) 1 else -1
        }
    }

    fun applyCuber(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            sample * sample * sample
        }
    }

    fun applySquareRooter(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            if (sample > 0) {
                kotlin.math.sqrt(sample)
            } else {
                -kotlin.math.sqrt(-sample)
            }
        }
    }

    fun applyLogarithmic(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            if (sample > 0) {
                kotlin.math.log1p(sample * 9) / kotlin.math.log(10f)
            } else {
                -kotlin.math.log1p(-sample * 9) / kotlin.math.log(10f)
            }
        }
    }

    fun applyExponential(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            if (sample > 0) {
                (kotlin.math.exp(sample) - 1) / (kotlin.math.E - 1)
            } else {
                -(kotlin.math.exp(-sample) - 1) / (kotlin.math.E - 1)
            }
        }
    }

    fun applySineShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.sin(sample * Math.PI / 2).toFloat()
        }
    }

    fun applyCosineShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            (1 - kotlin.math.cos(sample * Math.PI)) / 2
        }
    }

    fun applyArcsineShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            (2 / Math.PI) * kotlin.math.asin(sample).toFloat()
        }
    }

    fun applyArccosineShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            1 - (2 / Math.PI) * kotlin.math.acos(sample).toFloat()
        }
    }

    fun applyTangentShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.tan(sample * Math.PI / 4).toFloat()
        }
    }

    fun applyHyperbolicTangentShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.tanh(sample).toFloat()
        }
    }

    fun applyInverseTangentShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            (2 / Math.PI) * kotlin.math.atan(sample).toFloat()
        }
    }

    fun applyInverseHyperbolicTangentShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.atanh(sample).toFloat()
        }
    }

    fun applyGudermannianShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            (2 / Math.PI) * kotlin.math.atan(kotlin.math.sinh(sample)).toFloat()
        }
    }

    fun applyInverseGudermannianShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.atanh(kotlin.math.tan(sample * Math.PI / 2)).toFloat()
        }
    }

    fun applyErrorFunctionShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val absSample = kotlin.math.abs(sample)
            val sign = if (sample > 0) 1 else -1
            val t = 1 / (1 + 0.5 * absSample)
            val y = 1 - t * kotlin.math.exp(-absSample * absSample - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * 0.17087277)))))))))
            sign * y
        }
    }

    fun applyInverseErrorFunctionShaper(audioData: ByteArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val a = 0.147
            val ln = kotlin.math.log(1 - sample * sample)
            val x = 2 / (Math.PI * a) + ln / 2
            val y = kotlin.math.sqrt(x * x - ln / a) - x
            (if (sample > 0) 1 else -1) * kotlin.math.sqrt(y).toFloat()
        }
    }

    fun applyBetaShaper(audioData: ByteArray, alpha: Float, beta: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val absSample = kotlin.math.abs(sample)
            val sign = if (sample > 0) 1 else -1
            val result = kotlin.math.pow(absSample, alpha) / (kotlin.math.pow(absSample, alpha) + kotlin.math.pow(1 - absSample, beta))
            sign * result.toFloat()
        }
    }

    fun applyGammaShaper(audioData: ByteArray, gamma: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.pow(kotlin.math.abs(sample), gamma) * if (sample > 0) 1 else -1
        }
    }

    fun applyPowerShaper(audioData: ByteArray, power: Float): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            kotlin.math.pow(sample, power)
        }
    }

    fun applyPolynomialShaper(audioData: ByteArray, coefficients: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            var result = 0f
            for (i in coefficients.indices) {
                result += coefficients[i] * kotlin.math.pow(sample, i.toFloat())
            }
            result
        }
    }

    fun applySplineShaper(audioData: ByteArray, points: List<Pair<Float, Float>>): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            for (i in 0 until points.size - 1) {
                val (x0, y0) = points[i]
                val (x1, y1) = points[i + 1]
                if (sample >= x0 && sample <= x1) {
                    val t = (sample - x0) / (x1 - x0)
                    return@applyWaveShaping y0 + t * (y1 - y0)
                }
            }
            sample
        }
    }

    fun applyLookupTableShaper(audioData: ByteArray, lookupTable: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val index = ((sample + 1) / 2 * (lookupTable.size - 1)).toInt().coerceIn(0, lookupTable.size - 1)
            lookupTable[index]
        }
    }

    fun applyWaveformShaper(audioData: ByteArray, waveform: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            val index = ((sample + 1) / 2 * (waveform.size - 1)).toInt().coerceIn(0, waveform.size - 1)
            waveform[index]
        }
    }

    fun applyHarmonicShaper(audioData: ByteArray, harmonics: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            var result = 0f
            for (i in harmonics.indices) {
                result += harmonics[i] * kotlin.math.sin(sample * Math.PI * 2 * (i + 1)).toFloat()
            }
            result
        }
    }

    fun applyInharmonicShaper(audioData: ByteArray, partials: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            var result = 0f
            for (i in partials.indices) {
                result += partials[i] * kotlin.math.sin(sample * Math.PI * 2 * (i + 1) * 1.01).toFloat()
            }
            result
        }
    }

    fun applyFormantShaper(audioData: ByteArray, frequencies: FloatArray, amplitudes: FloatArray, bandwidths: FloatArray): ByteArray {
        return applyWaveShaping(audioData) { sample ->
            var result = 0f
            for (i in frequencies.indices) {
                val resonance = kotlin.math.exp(-((sample - frequencies[i]) / bandwidths[i]) * ((sample - frequencies[i]) / bandwidths[i]))
                result += amplitudes[i] * resonance
            }
            result
        }
    }

    fun applyVocalShaper(audioData: ByteArray, vowel: String): ByteArray {
        val formants = when (vowel.lowercase()) {
            "a" to floatArrayOf(800f, 1150f, 2900f)
            "e" to floatArrayOf(400f, 1900f, 2500f)
            "i" to floatArrayOf(240f, 2400f, 3000f)
            "o" to floatArrayOf(400f, 800f, 2830f)
            "u" to floatArrayOf(250f, 600f, 2400f)
            else -> floatArrayOf(800f, 1150f, 2900f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyConsonantShaper(audioData: ByteArray, consonant: String): ByteArray {
        val formants = when (consonant.lowercase()) {
            "s" to floatArrayOf(4000f, 8000f)
            "sh" to floatArrayOf(2000f, 4000f)
            "f" to floatArrayOf(3000f, 6000f)
            "th" to floatArrayOf(1500f, 3000f)
            "z" to floatArrayOf(3000f, 6000f)
            "zh" to floatArrayOf(2500f, 5000f)
            "v" to floatArrayOf(2000f, 4000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(4000f, 8000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyPlosiveShaper(audioData: ByteArray, plosive: String): ByteArray {
        val formants = when (plosive.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            "k" to floatArrayOf(2500f, 5000f)
            "g" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyNasalShaper(audioData: ByteArray, nasal: String): ByteArray {
        val formants = when (nasal.lowercase()) {
            "m" to floatArrayOf(250f, 1000f, 2000f)
            "n" to floatArrayOf(200f, 1500f, 2500f)
            "ng" to floatArrayOf(300f, 1000f, 2000f)
            else -> floatArrayOf(250f, 1000f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(50f, 50f, 50f))
    }

    fun applyApproximantShaper(audioData: ByteArray, approximant: String): ByteArray {
        val formants = when (approximant.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "w" to floatArrayOf(200f, 600f, 2000f)
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyAffricateShaper(audioData: ByteArray, affricate: String): ByteArray {
        val formants = when (affricate.lowercase()) {
            "ch" to floatArrayOf(2000f, 4000f, 6000f)
            "j" to floatArrayOf(1500f, 3000f, 5000f)
            "ts" to floatArrayOf(3000f, 6000f, 8000f)
            "dz" to floatArrayOf(2500f, 5000f, 7000f)
            else -> floatArrayOf(2000f, 4000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(150f, 150f, 150f))
    }

    fun applyDiphthongShaper(audioData: ByteArray, diphthong: String): ByteArray {
        val formants = when (diphthong.lowercase()) {
            "ai" to floatArrayOf(400f, 1000f, 2500f)
            "au" to floatArrayOf(300f, 700f, 2000f)
            "oi" to floatArrayOf(400f, 800f, 2500f)
            "ou" to floatArrayOf(300f, 600f, 2000f)
            "ei" to floatArrayOf(400f, 2000f, 3000f)
            else -> floatArrayOf(400f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyTriphthongShaper(audioData: ByteArray, triphthong: String): ByteArray {
        val formants = when (triphthong.lowercase()) {
            "iau" to floatArrayOf(250f, 600f, 2000f, 3000f)
            "uau" to floatArrayOf(200f, 500f, 1500f, 2500f)
            "iai" to floatArrayOf(250f, 2000f, 3000f, 4000f)
            else -> floatArrayOf(250f, 600f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f, 0.125f), floatArrayOf(50f, 50f, 50f, 50f))
    }

    fun applySemivowelShaper(audioData: ByteArray, semivowel: String): ByteArray {
        val formants = when (semivowel.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            "y" to floatArrayOf(250f, 2000f, 3000f)
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyGlideShaper(audioData: ByteArray, glide: String): ByteArray {
        val formants = when (glide.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLiquidShaper(audioData: ByteArray, liquid: String): ByteArray {
        val formants = when (liquid.lowercase()) {
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(400f, 1200f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyRhoticShaper(audioData: ByteArray, rhotic: String): ByteArray {
        val formants = when (rhotic.lowercase()) {
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "rr" to floatArrayOf(300f, 1000f, 1800f)
            else -> floatArrayOf(400f, 1200f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLateralShaper(audioData: ByteArray, lateral: String): ByteArray {
        val formants = when (lateral.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            "ll" to floatArrayOf(250f, 900f, 2200f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyTrillShaper(audioData: ByteArray, trill: String): ByteArray {
        val formants = when (trill.lowercase()) {
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "rr" to floatArrayOf(300f, 1000f, 1800f)
            else -> floatArrayOf(400f, 1200f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyTapShaper(audioData: ByteArray, tap: String): ByteArray {
        val formants = when (tap.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyFlapShaper(audioData: ByteArray, flap: String): ByteArray {
        val formants = when (flap.lowercase()) {
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(400f, 1200f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyClickShaper(audioData: ByteArray, click: String): ByteArray {
        val formants = when (click.lowercase()) {
            "!" to floatArrayOf(3000f, 6000f)
            "?" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyImplosiveShaper(audioData: ByteArray, implosive: String): ByteArray {
        val formants = when (implosive.lowercase()) {
            "b" to floatArrayOf(400f, 1200f)
            "d" to floatArrayOf(1500f, 3000f)
            "g" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(400f, 1200f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyEjectiveShaper(audioData: ByteArray, ejective: String): ByteArray {
        val formants = when (ejective.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "t" to floatArrayOf(2000f, 4000f)
            "k" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyClickConsonantShaper(audioData: ByteArray, clickConsonant: String): ByteArray {
        val formants = when (clickConsonant.lowercase()) {
            "!" to floatArrayOf(3000f, 6000f)
            "?" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLateralClickShaper(audioData: ByteArray, lateralClick: String): ByteArray {
        val formants = when (lateralClick.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLateralFricativeShaper(audioData: ByteArray, lateralFricative: String): ByteArray {
        val formants = when (lateralFricative.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLateralApproximantShaper(audioData: ByteArray, lateralApproximant: String): ByteArray {
        val formants = when (lateralApproximant.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLateralFlapShaper(audioData: ByteArray, lateralFlap: String): ByteArray {
        val formants = when (lateralFlap.lowercase()) {
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(300f, 1000f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyRetroflexShaper(audioData: ByteArray, retroflex: String): ByteArray {
        val formants = when (retroflex.lowercase()) {
            "r" to floatArrayOf(400f, 1200f, 2000f)
            "l" to floatArrayOf(300f, 1000f, 2500f)
            else -> floatArrayOf(400f, 1200f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyPalatalShaper(audioData: ByteArray, palatal: String): ByteArray {
        val formants = when (palatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            "j" to floatArrayOf(1500f, 3000f, 5000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyVelarShaper(audioData: ByteArray, velar: String): ByteArray {
        val formants = when (velar.lowercase()) {
            "k" to floatArrayOf(2500f, 5000f)
            "g" to floatArrayOf(2000f, 4000f)
            "ng" to floatArrayOf(300f, 1000f, 2000f)
            else -> floatArrayOf(2500f, 5000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyUvularShaper(audioData: ByteArray, uvular: String): ByteArray {
        val formants = when (uvular.lowercase()) {
            "q" to floatArrayOf(3000f, 6000f)
            "gh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyPharyngealShaper(audioData: ByteArray, pharyngeal: String): ByteArray {
        val formants = when (pharyngeal.lowercase()) {
            "h" to floatArrayOf(500f, 1500f, 2500f)
            "kh" to floatArrayOf(400f, 1200f, 2200f)
            else -> floatArrayOf(500f, 1500f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyGlottalShaper(audioData: ByteArray, glottal: String): ByteArray {
        val formants = when (glottal.lowercase()) {
            "h" to floatArrayOf(500f, 1500f, 2500f)
            else -> floatArrayOf(500f, 1500f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyEpiglottalShaper(audioData: ByteArray, epiglottal: String): ByteArray {
        val formants = when (epiglottal.lowercase()) {
            "h" to floatArrayOf(500f, 1500f, 2500f)
            else -> floatArrayOf(500f, 1500f, 2500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyAlveolarShaper(audioData: ByteArray, alveolar: String): ByteArray {
        val formants = when (alveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            "s" to floatArrayOf(4000f, 8000f)
            "z" to floatArrayOf(3000f, 6000f)
            "n" to floatArrayOf(200f, 1500f, 2500f)
            "l" to floatArrayOf(300f, 1000f, 2500f)
            "r" to floatArrayOf(400f, 1200f, 2000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyPostAlveolarShaper(audioData: ByteArray, postAlveolar: String): ByteArray {
        val formants = when (postAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            "ch" to floatArrayOf(2000f, 4000f, 6000f)
            "j" to floatArrayOf(1500f, 3000f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(150f, 150f, 150f))
    }

    fun applyDentalShaper(audioData: ByteArray, dental: String): ByteArray {
        val formants = when (dental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalShaper(audioData: ByteArray, labiodental: String): ByteArray {
        val formants = when (labiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyBilabialShaper(audioData: ByteArray, bilabial: String): ByteArray {
        val formants = when (bilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiovelarShaper(audioData: ByteArray, labiovelar: String): ByteArray {
        val formants = when (labiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiopalatalShaper(audioData: ByteArray, labiopalatal: String): ByteArray {
        val formants = when (labiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalVelarShaper(audioData: ByteArray, labiodentalVelar: String): ByteArray {
        val formants = when (labiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalPalatalShaper(audioData: ByteArray, labiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalDentalShaper(audioData: ByteArray, labiodentalDental: String): ByteArray {
        val formants = when (labiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalBilabialShaper(audioData: ByteArray, labiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiopalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalVelar.lowercase()) {
            "w" to floatArrayOf(200f, 600f, 2000f)
            else -> floatArrayOf(200f, 600f, 2000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPalatal.lowercase()) {
            "y" to floatArrayOf(250f, 2000f, 3000f)
            else -> floatArrayOf(250f, 2000f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalAlveolar.lowercase()) {
            "t" to floatArrayOf(2000f, 4000f)
            "d" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalPostAlveolar.lowercase()) {
            "sh" to floatArrayOf(2000f, 4000f)
            "zh" to floatArrayOf(2500f, 5000f)
            else -> floatArrayOf(2000f, 4000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(150f, 150f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalDental.lowercase()) {
            "th" to floatArrayOf(1500f, 3000f)
            "dh" to floatArrayOf(1500f, 3000f)
            else -> floatArrayOf(1500f, 3000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodental.lowercase()) {
            "f" to floatArrayOf(3000f, 6000f)
            "v" to floatArrayOf(2000f, 4000f)
            else -> floatArrayOf(3000f, 6000f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f), floatArrayOf(200f, 200f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabialShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial: String): ByteArray {
        val formants = when (labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalBilabial.lowercase()) {
            "p" to floatArrayOf(500f, 1500f)
            "b" to floatArrayOf(400f, 1200f)
            "m" to floatArrayOf(250f, 1000f, 2000f)
            else -> floatArrayOf(500f, 1500f)
        }
        return applyFormantShaper(audioData, formants.first, floatArrayOf(1f, 0.5f, 0.25f), floatArrayOf(100f, 100f, 100f))
    }

    fun applyLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelarShaper(audioData: ByteArray, labiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiodentalLabiovelar: