package com.example.finjan

import android.app.Application
import android.util.Log
import com.example.finjan.ui.MainActivity
import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.security.SecurePreferencesManager
import com.example.finjan.utils.security.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.example.finjan.data.repository.FirestoreRepository

/**
 * Application class for app-wide initialization and dependency management.
 * Provides centralized access to singletons and handles app lifecycle.
 */
class FinjanApplication : Application() {

    companion object {
        private const val TAG = "FinjanApplication"
        
        @Volatile
        private var instance: FinjanApplication? = null
        
        /**
         * Get the application instance.
         */
        fun getInstance(): FinjanApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    // Lazy-initialized singletons
    val securePreferences: SecurePreferencesManager by lazy {
        SecurePreferencesManager(this)
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    val firestoreRepository: FirestoreRepository by lazy {
        com.example.finjan.data.repository.FirestoreRepository()
    }

    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Firebase
        initializeFirebase()

        // Setup crash handling
        setupCrashHandler()

        // Validate existing session
        validateSession()

        AppLogger.i(TAG, "Application initialized successfully")
    }

    /**
     * Initialize Firebase services.
     */
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            AppLogger.d(TAG, "Firebase initialized")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to initialize Firebase", e)
        }
    }

    /**
     * Setup global crash handler.
     */
    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Log the crash
            AppLogger.e(TAG, "FATAL CRASH in thread ${thread.name}", throwable)
            
            // Export logs for debugging
            try {
                val logs = AppLogger.exportLogs()
                Log.e(TAG, "=== CRASH LOGS ===\n$logs")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to export logs", e)
            }

            // Call default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Validate existing user session on app start.
     */
    private fun validateSession() {
        if (sessionManager.hasActiveSession()) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                // Firebase session expired, clear local session
                sessionManager.endSession()
                AppLogger.logSecurity("Session invalidated", "Firebase user null")
            } else {
                // Refresh session
                sessionManager.refreshSession()
                AppLogger.logAuth("Session restored", true, user.uid)
            }
        }
    }

    /**
     * Log analytics event.
     */
    fun logAnalyticsEvent(event: String, params: Map<String, Any>? = null) {
        try {
            firebaseAnalytics?.logEvent(event, params?.let { map ->
                android.os.Bundle().apply {
                    map.forEach { (key, value) ->
                        when (value) {
                            is String -> putString(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Double -> putDouble(key, value)
                            is Boolean -> putBoolean(key, value)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to log analytics event", e)
        }
    }

    /**
     * Set user properties for analytics.
     */
    fun setAnalyticsUserProperty(property: String, value: String?) {
        try {
            firebaseAnalytics?.setUserProperty(property, value)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to set user property", e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        AppLogger.i(TAG, "Application terminated")
    }
}
