package com.example.finjan

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.config.RemoteConfigManager
import com.example.finjan.utils.security.SecurePreferencesManager
import com.example.finjan.utils.security.SessionManager
import com.example.finjan.worker.OrderSyncWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application class for app-wide initialization and dependency management.
 * Hilt handles dependency injection; this class focuses on Firebase and session setup.
 */
@HiltAndroidApp
class FinjanApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: ImageLoader

    companion object {
        private const val TAG = "FinjanApplication"
        
        @Volatile
        private var instance: FinjanApplication? = null
        
        /**
         * Get the application instance.
         * @deprecated Prefer constructor injection via Hilt instead of this singleton accessor.
         */
        @Deprecated("Use Hilt injection instead")
        fun getInstance(): FinjanApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    // Lazy-initialized for backward compatibility during migration
    val securePreferences: SecurePreferencesManager by lazy {
        SecurePreferencesManager(this)
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun newImageLoader(): ImageLoader = imageLoader

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Firebase
        initializeFirebase()
        
        // Initialize Crashlytics
        initializeCrashlytics()
        
        // Initialize Remote Config for A/B testing and force updates
        initializeRemoteConfig()

        // Setup crash handling
        setupCrashHandler()

        // Validate existing session
        validateSession()

        // Schedule periodic order sync
        scheduleOrderSync()

        AppLogger.i(TAG, "Application initialized successfully")
    }
    
    /**
     * Initialize Firebase Crashlytics.
     */
    private fun initializeCrashlytics() {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            AppLogger.d(TAG, "Crashlytics initialized (collection=${!BuildConfig.DEBUG})")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to initialize Crashlytics", e)
        }
    }
    
    /**
     * Initialize Firebase Remote Config.
     */
    private fun initializeRemoteConfig() {
        try {
            val isDebug = BuildConfig.DEBUG
            RemoteConfigManager.initialize(this, isDebug)
            AppLogger.d(TAG, "Remote Config initialized (debug=$isDebug)")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to initialize Remote Config", e)
        }
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
     * Setup global crash handler that logs to both AppLogger and Crashlytics.
     */
    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Log the crash
            AppLogger.e(TAG, "FATAL CRASH in thread ${thread.name}", throwable)
            
            // Record to Crashlytics
            try {
                FirebaseCrashlytics.getInstance().recordException(throwable)
            } catch (_: Exception) { }
            
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
     * Schedule periodic order sync with WorkManager.
     */
    private fun scheduleOrderSync() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<OrderSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                OrderSyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
            AppLogger.d(TAG, "Order sync scheduled")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to schedule order sync", e)
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
