package com.example.finjan.utils.config

import android.content.Context
import com.example.finjan.R
import com.example.finjan.utils.AppLogger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

/**
 * Singleton manager for Firebase Remote Config.
 * Provides centralized access to remote configuration values and A/B testing.
 */
object RemoteConfigManager {
    
    private const val TAG = "RemoteConfigManager"
    
    // Fetch interval: 1 hour for production, 0 for debug
    private const val FETCH_INTERVAL_PRODUCTION = 3600L // 1 hour
    private const val FETCH_INTERVAL_DEBUG = 0L
    
    private var isInitialized = false
    private val remoteConfig: FirebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }
    
    /**
     * Initialize Remote Config with default values.
     * Should be called once in Application.onCreate().
     */
    fun initialize(context: Context, isDebug: Boolean = false) {
        if (isInitialized) return
        
        val fetchInterval = if (isDebug) FETCH_INTERVAL_DEBUG else FETCH_INTERVAL_PRODUCTION
        
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(fetchInterval)
            .build()
        
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        
        isInitialized = true
        AppLogger.d(TAG, "Remote Config initialized with fetch interval: $fetchInterval seconds")
    }
    
    /**
     * Fetch and activate the latest Remote Config values.
     * Returns true if new values were activated.
     */
    suspend fun fetchAndActivate(): Boolean {
        return try {
            val activated = remoteConfig.fetchAndActivate().await()
            AppLogger.d(TAG, "Remote Config fetch completed. New values activated: $activated")
            activated
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch Remote Config", e)
            false
        }
    }
    
    /**
     * Get a string value from Remote Config.
     */
    fun getString(key: String): String = remoteConfig.getString(key)
    
    /**
     * Get a boolean value from Remote Config.
     */
    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
    
    /**
     * Get a long value from Remote Config.
     */
    fun getLong(key: String): Long = remoteConfig.getLong(key)
    
    /**
     * Get a double value from Remote Config.
     */
    fun getDouble(key: String): Double = remoteConfig.getDouble(key)
    
    /**
     * Get all Remote Config values as a map for debugging.
     */
    fun getAllValues(): Map<String, String> {
        return remoteConfig.all.mapValues { it.value.asString() }
    }
}
