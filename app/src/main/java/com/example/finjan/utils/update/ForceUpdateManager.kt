package com.example.finjan.utils.update

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.finjan.utils.AppLogger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

/**
 * Manages force update checks using Firebase Remote Config.
 * Compares the current app version against minimum required version from Remote Config.
 */
class ForceUpdateManager(private val context: Context) {
    
    companion object {
        private const val TAG = "ForceUpdateManager"
        
        // Remote Config keys
        const val KEY_MIN_VERSION_CODE = "min_version_code"
        const val KEY_MIN_VERSION_NAME = "min_version_name"
        const val KEY_UPDATE_MESSAGE = "force_update_message"
        const val KEY_UPDATE_URL = "update_url"
        const val KEY_FORCE_UPDATE_ENABLED = "force_update_enabled"
        
        // Default values
        private const val DEFAULT_MIN_VERSION_CODE = 1L
        private const val DEFAULT_UPDATE_MESSAGE = "A new version of Finjan is available. Please update to continue."
        private const val DEFAULT_UPDATE_URL = "https://play.google.com/store/apps/details?id=com.example.finjan"
    }
    
    /**
     * Update status result.
     */
    sealed class UpdateStatus {
        data object UpToDate : UpdateStatus()
        data class UpdateRequired(
            val currentVersion: String,
            val minVersion: String,
            val message: String,
            val updateUrl: String
        ) : UpdateStatus()
        data class Error(val exception: Exception) : UpdateStatus()
    }
    
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    
    init {
        // Configure Remote Config settings
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour in production
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Set default values
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_MIN_VERSION_CODE to DEFAULT_MIN_VERSION_CODE,
            KEY_MIN_VERSION_NAME to "1.0.0",
            KEY_UPDATE_MESSAGE to DEFAULT_UPDATE_MESSAGE,
            KEY_UPDATE_URL to DEFAULT_UPDATE_URL,
            KEY_FORCE_UPDATE_ENABLED to false
        ))
    }
    
    /**
     * Check if an update is required.
     * Fetches the latest Remote Config values and compares versions.
     */
    suspend fun checkForUpdate(): UpdateStatus {
        return try {
            // Fetch and activate remote config
            remoteConfig.fetchAndActivate().await()
            
            // Check if force update is enabled
            val forceUpdateEnabled = remoteConfig.getBoolean(KEY_FORCE_UPDATE_ENABLED)
            if (!forceUpdateEnabled) {
                AppLogger.d(TAG, "Force update is disabled")
                return UpdateStatus.UpToDate
            }
            
            // Get minimum required version
            val minVersionCode = remoteConfig.getLong(KEY_MIN_VERSION_CODE)
            val minVersionName = remoteConfig.getString(KEY_MIN_VERSION_NAME)
            val updateMessage = remoteConfig.getString(KEY_UPDATE_MESSAGE)
            val updateUrl = remoteConfig.getString(KEY_UPDATE_URL)
            
            // Get current app version
            val currentVersionCode = getCurrentVersionCode()
            val currentVersionName = getCurrentVersionName()
            
            AppLogger.d(TAG, "Current version: $currentVersionCode ($currentVersionName), Min version: $minVersionCode ($minVersionName)")
            
            // Compare versions
            if (currentVersionCode < minVersionCode) {
                UpdateStatus.UpdateRequired(
                    currentVersion = currentVersionName,
                    minVersion = minVersionName,
                    message = updateMessage.ifEmpty { DEFAULT_UPDATE_MESSAGE },
                    updateUrl = updateUrl.ifEmpty { DEFAULT_UPDATE_URL }
                )
            } else {
                UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to check for update", e)
            UpdateStatus.Error(e)
        }
    }
    
    /**
     * Get the current app version code.
     */
    private fun getCurrentVersionCode(): Long {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            AppLogger.e(TAG, "Package not found", e)
            0L
        }
    }
    
    /**
     * Get the current app version name.
     */
    private fun getCurrentVersionName(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            AppLogger.e(TAG, "Package not found", e)
            "1.0.0"
        }
    }
}
