package com.example.finjan.utils.security

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Session manager for handling user authentication state.
 * Provides centralized session management with timeout handling.
 */
class SessionManager(context: Context) {

    companion object {
        private const val SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000L // 24 hours
        private const val KEY_SESSION_START = "session_start_time"
        private const val KEY_SESSION_TOKEN = "session_token"
    }

    private val securePrefs = SecurePreferencesManager(context)

    private val _isSessionValid = MutableStateFlow(false)
    val isSessionValid: StateFlow<Boolean> = _isSessionValid.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        validateSession()
    }

    /**
     * Start a new session for the user.
     */
    fun startSession(userId: String, token: String? = null) {
        securePrefs.saveSession(userId, token)
        securePrefs.putLong(KEY_SESSION_START, System.currentTimeMillis())
        _currentUserId.value = userId
        _isSessionValid.value = true
    }

    /**
     * End the current session.
     */
    fun endSession() {
        securePrefs.clearSession()
        securePrefs.remove(KEY_SESSION_START)
        _currentUserId.value = null
        _isSessionValid.value = false
    }

    /**
     * Check if the current session is still valid.
     */
    fun validateSession(): Boolean {
        val userId = securePrefs.getString(SecurePreferencesManager.KEY_USER_ID)
        val sessionStart = securePrefs.getLong(KEY_SESSION_START, 0L)

        if (userId == null || sessionStart == 0L) {
            _isSessionValid.value = false
            _currentUserId.value = null
            return false
        }

        val isExpired = System.currentTimeMillis() - sessionStart > SESSION_TIMEOUT_MS
        if (isExpired) {
            endSession()
            return false
        }

        _currentUserId.value = userId
        _isSessionValid.value = true
        return true
    }

    /**
     * Refresh the session timestamp.
     */
    fun refreshSession() {
        if (_isSessionValid.value) {
            securePrefs.putLong(KEY_SESSION_START, System.currentTimeMillis())
        }
    }

    /**
     * Get remaining session time in milliseconds.
     */
    fun getRemainingSessionTime(): Long {
        val sessionStart = securePrefs.getLong(KEY_SESSION_START, 0L)
        if (sessionStart == 0L) return 0L

        val elapsed = System.currentTimeMillis() - sessionStart
        return (SESSION_TIMEOUT_MS - elapsed).coerceAtLeast(0L)
    }

    /**
     * Check if user has an active session.
     */
    fun hasActiveSession(): Boolean = _isSessionValid.value

    /**
     * Get session info.
     */
    fun getSessionInfo(): SessionInfo? {
        val userId = _currentUserId.value ?: return null
        val sessionStart = securePrefs.getLong(KEY_SESSION_START, 0L)
        
        return SessionInfo(
            userId = userId,
            sessionStartTime = sessionStart,
            remainingTime = getRemainingSessionTime()
        )
    }
}

/**
 * Session information data class.
 */
data class SessionInfo(
    val userId: String,
    val sessionStartTime: Long,
    val remainingTime: Long
) {
    val isExpiringSoon: Boolean
        get() = remainingTime < 60 * 60 * 1000L // Less than 1 hour
}
