package com.example.finjan.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Production-ready logging utility with structured logging support.
 * Provides consistent logging across the app with log levels and timestamps.
 */
object AppLogger {

    private const val MAX_LOG_BUFFER = 100
    private const val TAG_PREFIX = "Finjan"
    
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    /**
     * Log levels for filtering and categorization.
     */
    enum class Level {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }

    /**
     * Log entry data class for structured logging.
     */
    data class LogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val level: Level,
        val tag: String,
        val message: String,
        val throwable: Throwable? = null
    ) {
        fun format(): String {
            val time = dateFormat.format(Date(timestamp))
            val error = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""
            return "[$time] [${level.name}] [$tag] $message$error"
        }
    }

    /**
     * Log verbose message.
     */
    fun v(tag: String, message: String) {
        log(Level.VERBOSE, tag, message)
        Log.v("$TAG_PREFIX/$tag", message)
    }

    /**
     * Log debug message.
     */
    fun d(tag: String, message: String) {
        log(Level.DEBUG, tag, message)
        Log.d("$TAG_PREFIX/$tag", message)
    }

    /**
     * Log info message.
     */
    fun i(tag: String, message: String) {
        log(Level.INFO, tag, message)
        Log.i("$TAG_PREFIX/$tag", message)
    }

    /**
     * Log warning message.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.WARN, tag, message, throwable)
        if (throwable != null) {
            Log.w("$TAG_PREFIX/$tag", message, throwable)
        } else {
            Log.w("$TAG_PREFIX/$tag", message)
        }
    }

    /**
     * Log error message.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, message, throwable)
        if (throwable != null) {
            Log.e("$TAG_PREFIX/$tag", message, throwable)
        } else {
            Log.e("$TAG_PREFIX/$tag", message)
        }
    }

    /**
     * Log authentication event.
     */
    fun logAuth(event: String, success: Boolean, userId: String? = null) {
        val status = if (success) "SUCCESS" else "FAILED"
        val userInfo = userId?.let { " [User: ${it.take(8)}...]" } ?: ""
        i("Auth", "$event - $status$userInfo")
    }

    /**
     * Log security event.
     */
    fun logSecurity(event: String, details: String? = null) {
        val detailsInfo = details?.let { " - $it" } ?: ""
        w("Security", "$event$detailsInfo")
    }

    /**
     * Log navigation event.
     */
    fun logNavigation(from: String, to: String) {
        d("Navigation", "$from -> $to")
    }

    /**
     * Log API/network event.
     */
    fun logNetwork(operation: String, success: Boolean, durationMs: Long? = null) {
        val status = if (success) "SUCCESS" else "FAILED"
        val duration = durationMs?.let { " (${it}ms)" } ?: ""
        i("Network", "$operation - $status$duration")
    }

    /**
     * Add entry to log buffer.
     */
    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        val entry = LogEntry(
            level = level,
            tag = tag,
            message = message,
            throwable = throwable
        )
        
        logBuffer.add(entry)
        
        // Keep buffer size limited
        while (logBuffer.size > MAX_LOG_BUFFER) {
            logBuffer.poll()
        }
    }

    /**
     * Get recent log entries.
     */
    fun getRecentLogs(count: Int = MAX_LOG_BUFFER): List<LogEntry> {
        return logBuffer.toList().takeLast(count)
    }

    /**
     * Get logs filtered by level.
     */
    fun getLogsByLevel(level: Level): List<LogEntry> {
        return logBuffer.filter { it.level == level }
    }

    /**
     * Get formatted log output.
     */
    fun getFormattedLogs(): String {
        return logBuffer.joinToString("\n") { it.format() }
    }

    /**
     * Clear log buffer.
     */
    fun clearLogs() {
        logBuffer.clear()
    }

    /**
     * Export logs for debugging/crash reports.
     */
    fun exportLogs(): String {
        val header = """
            |=== Finjan App Logs ===
            |Exported: ${dateFormat.format(Date())}
            |Entries: ${logBuffer.size}
            |========================
            |
        """.trimMargin()
        
        return header + getFormattedLogs()
    }
}

/**
 * Extension function for easy logging from any class.
 */
inline fun <reified T> T.logD(message: String) {
    AppLogger.d(T::class.java.simpleName, message)
}

inline fun <reified T> T.logE(message: String, throwable: Throwable? = null) {
    AppLogger.e(T::class.java.simpleName, message, throwable)
}
