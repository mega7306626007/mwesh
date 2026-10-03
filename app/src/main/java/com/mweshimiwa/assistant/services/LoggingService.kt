package com.mweshimiwa.assistant.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class LoggingService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var loggingJob: Job? = null

    private val _logState = MutableStateFlow<LogState>(LogState.Idle)
    val logState: StateFlow<LogState> = _logState.asStateFlow()

    private val _logCount = MutableStateFlow(0L)
    val logCount: StateFlow<Long> = _logCount.asStateFlow()

    private val logQueue = ConcurrentLinkedQueue<LogEntry>()
    private val logHistory = mutableListOf<LogEntry>()
    private val logFilters = ConcurrentHashMap<String, Boolean>()
    private val isWriting = AtomicBoolean(false)
    private val logCounter = AtomicInteger(0)
    private val errorCounter = AtomicInteger(0)
    private val totalBytesLogged = AtomicLong(0L)

    private val logListeners = mutableListOf<LogListener>()
    private val maxHistorySize = 5000
    private val maxQueueSize = 10000
    private val flushIntervalMs = 5000L
    private val maxLogFileSize = 5 * 1024 * 1024L
    private val maxLogFiles = 5
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var logFile: File? = null
    private var fileWriter: FileWriter? = null

    inner class LocalBinder : Binder() {
        fun getService(): LoggingService = this@LoggingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "LoggingService created")
        initializeLogging()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_LOG -> log(
                intent.getIntExtra(EXTRA_LEVEL, Log.INFO),
                intent.getStringExtra(EXTRA_TAG) ?: TAG,
                intent.getStringExtra(EXTRA_MESSAGE) ?: "",
                intent.getStringExtra(EXTRA_THROWABLE)
            )
            ACTION_FLUSH -> flushLogs()
            ACTION_CLEAR -> clearLogs()
            ACTION_SET_FILTER -> setFilter(
                intent.getStringExtra(EXTRA_FILTER_TAG) ?: "",
                intent.getBooleanExtra(EXTRA_FILTER_ENABLED, true)
            )
            ACTION_EXPORT -> exportLogs(
                intent.getStringExtra(EXTRA_EXPORT_PATH) ?: ""
            )
            ACTION_ROTATE -> rotateLogFiles()
        }
        return START_STICKY
    }

    private fun initializeLogging() {
        createLogFile()
        loadLogHistory()
        startPeriodicFlush()
    }

    private fun createLogFile() {
        val logDir = File(filesDir, "logs")
        if (!logDir.exists()) logDir.mkdirs()
        logFile = File(logDir, "app_${fileDateFormat.format(Date())}.log")
    }

    private fun startPeriodicFlush() {
        loggingJob = serviceScope.launch {
            while (isActive) {
                delay(flushIntervalMs)
                if (isActive) flushLogs()
            }
        }
    }

    private fun log(level: Int, tag: String, message: String, throwable: String?) {
        val entry = LogEntry(
            id = logCounter.incrementAndGet().toLong(),
            level = level,
            tag = tag,
            message = message,
            throwable = throwable,
            timestamp = System.currentTimeMillis(),
            threadName = Thread.currentThread().name
        )

        if (logQueue.size >= maxQueueSize) {
            logQueue.poll()
        }
        logQueue.offer(entry)
        _logCount.value = logCounter.get().toLong()
        totalBytesLogged.addAndGet(message.length.toLong())

        if (level >= Log.ERROR) {
            errorCounter.incrementAndGet()
        }

        addToHistory(entry)
        notifyListeners { onLogAdded(entry) }
    }

    private fun flushLogs() {
        if (isWriting.get()) return
        serviceScope.launch {
            isWriting.set(true)
            _logState.value = LogState.Writing
            try {
                if (logFile == null || logFile!!.length() > maxLogFileSize) {
                    rotateLogFiles()
                }

                fileWriter = FileWriter(logFile, true)
                while (logQueue.isNotEmpty()) {
                    val entry = logQueue.poll() ?: break
                    val line = formatLogEntry(entry)
                    fileWriter?.write(line)
                    fileWriter?.write("\n")
                }
                fileWriter?.flush()
                fileWriter?.close()
                fileWriter = null

                _logState.value = LogState.Flushed
                notifyListeners { onLogsFlushed() }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to flush logs", e)
                _logState.value = LogState.Error(e.message ?: "Flush failed")
            } finally {
                isWriting.set(false)
            }
        }
    }

    private fun clearLogs() {
        logQueue.clear()
        logHistory.clear()
        logCounter.set(0)
        errorCounter.set(0)
        _logCount.value = 0
        notifyListeners { onLogsCleared() }
    }

    private fun setFilter(tag: String, enabled: Boolean) {
        logFilters[tag] = enabled
        notifyListeners { onFilterChanged(tag, enabled) }
    }

    private fun exportLogs(exportPath: String) {
        serviceScope.launch {
            _logState.value = LogState.Exporting
            try {
                val exportFile = File(exportPath)
                FileWriter(exportFile).use { writer ->
                    logHistory.forEach { entry ->
                        writer.write(formatLogEntry(entry))
                        writer.write("\n")
                    }
                }
                _logState.value = LogState.Exported(exportPath)
                notifyListeners { onLogsExported(exportPath) }
            } catch (e: Exception) {
                _logState.value = LogState.Error(e.message ?: "Export failed")
            }
        }
    }

    private fun rotateLogFiles() {
        fileWriter?.close()
        fileWriter = null

        val logDir = File(filesDir, "logs")
        val existingLogs = logDir.listFiles { f -> f.name.endsWith(".log") }
            ?.sortedBy { it.lastModified() }
            ?: emptyList()

        if (existingLogs.size >= maxLogFiles) {
            existingLogs.first().delete()
        }

        createLogFile()
    }

    private fun formatLogEntry(entry: LogEntry): String {
        val levelChar = when (entry.level) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }
        val throwableStr = entry.throwable?.let { "\n$it" } ?: ""
        return "${dateFormat.format(Date(entry.timestamp))} $levelChar/${entry.tag}: ${entry.message}$throwableStr"
    }

    private fun addToHistory(entry: LogEntry) {
        logHistory.add(entry)
        if (logHistory.size > maxHistorySize) {
            logHistory.removeAt(0)
        }
    }

    private fun loadLogHistory() {
        // Load from persistent storage
    }

    fun getLogHistory(): List<LogEntry> = logHistory.toList()

    fun getLogCount(): Int = logCounter.get()

    fun getErrorCount(): Int = errorCounter.get()

    fun getTotalBytesLogged(): Long = totalBytesLogged.get()

    fun getQueueSize(): Int = logQueue.size

    fun addLogListener(listener: LogListener) {
        logListeners.add(listener)
    }

    fun removeLogListener(listener: LogListener) {
        logListeners.remove(listener)
    }

    private fun notifyListeners(action: LogListener.() -> Unit) {
        logListeners.forEach { it.action() }
    }

    fun getLogStatus(): LogStatus {
        return LogStatus(
            isWriting = isWriting.get(),
            logCount = logCounter.get(),
            errorCount = errorCounter.get(),
            queueSize = logQueue.size,
            historySize = logHistory.size,
            totalBytes = totalBytesLogged.get()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        flushLogs()
        loggingJob?.cancel()
        serviceScope.cancel()
        fileWriter?.close()
        Log.d(TAG, "LoggingService destroyed")
    }

    companion object {
        private const val TAG = "LoggingService"
        const val ACTION_LOG = "com.mweshimiwa.assistant.action.LOG"
        const val ACTION_FLUSH = "com.mweshimiwa.assistant.action.FLUSH"
        const val ACTION_CLEAR = "com.mweshimiwa.assistant.action.CLEAR"
        const val ACTION_SET_FILTER = "com.mweshimiwa.assistant.action.SET_FILTER"
        const val ACTION_EXPORT = "com.mweshimiwa.assistant.action.EXPORT"
        const val ACTION_ROTATE = "com.mweshimiwa.assistant.action.ROTATE"
        const val EXTRA_LEVEL = "level"
        const val EXTRA_TAG = "tag"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_THROWABLE = "throwable"
        const val EXTRA_FILTER_TAG = "filter_tag"
        const val EXTRA_FILTER_ENABLED = "filter_enabled"
        const val EXTRA_EXPORT_PATH = "export_path"
    }
}

data class LogEntry(
    val id: Long,
    val level: Int,
    val tag: String,
    val message: String,
    val throwable: String?,
    val timestamp: Long,
    val threadName: String
)

data class LogStatus(
    val isWriting: Boolean,
    val logCount: Int,
    val errorCount: Int,
    val queueSize: Int,
    val historySize: Int,
    val totalBytes: Long
)

sealed class LogState {
    object Idle : LogState()
    object Writing : LogState()
    object Flushed : LogState()
    object Exporting : LogState()
    data class Exported(val path: String) : LogState()
    data class Error(val message: String) : LogState()
}

interface LogListener {
    fun onLogAdded(entry: LogEntry) {}
    fun onLogsFlushed() {}
    fun onLogsCleared() {}
    fun onFilterChanged(tag: String, enabled: Boolean) {}
    fun onLogsExported(path: String) {}
}
