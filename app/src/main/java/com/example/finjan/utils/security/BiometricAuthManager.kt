package com.example.finjan.utils.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.finjan.utils.AppLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Biometric authentication manager for secure fingerprint/face authentication.
 * Provides a simplified API for biometric authentication flows.
 */
class BiometricAuthManager(private val context: Context) {

    companion object {
        private const val TAG = "BiometricAuthManager"
    }

    private val biometricManager = BiometricManager.from(context)

    /**
     * Check biometric availability.
     */
    fun getBiometricStatus(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NotEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SecurityUpdateRequired
            else -> BiometricStatus.Unsupported
        }
    }

    /**
     * Check if biometric authentication is available.
     */
    fun isBiometricAvailable(): Boolean {
        return getBiometricStatus() == BiometricStatus.Available
    }

    /**
     * Show biometric authentication prompt.
     * @param activity FragmentActivity for prompt display
     * @param title Title of the prompt
     * @param subtitle Subtitle/description
     * @param negativeButtonText Text for cancel button
     * @return BiometricResult with success/failure status
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Authenticate",
        subtitle: String = "Use your fingerprint or face to authenticate",
        negativeButtonText: String = "Cancel"
    ): BiometricResult = suspendCancellableCoroutine { continuation ->

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                AppLogger.logAuth("Biometric authentication", true)
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Success)
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val isCancelled = errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_CANCELED

                AppLogger.logAuth("Biometric authentication", false)
                
                if (continuation.isActive) {
                    if (isCancelled) {
                        continuation.resume(BiometricResult.Cancelled)
                    } else {
                        continuation.resume(BiometricResult.Error(errorCode, errString.toString()))
                    }
                }
            }

            override fun onAuthenticationFailed() {
                AppLogger.w(TAG, "Biometric authentication failed - not recognized")
                // Don't resume here, the user can try again
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(true)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Get user-friendly message for biometric status.
     */
    fun getStatusMessage(): String {
        return when (getBiometricStatus()) {
            BiometricStatus.Available -> "Biometric authentication is available"
            BiometricStatus.NoHardware -> "This device doesn't support biometric authentication"
            BiometricStatus.HardwareUnavailable -> "Biometric hardware is currently unavailable"
            BiometricStatus.NotEnrolled -> "Please enroll a fingerprint or face in your device settings"
            BiometricStatus.SecurityUpdateRequired -> "Security update required for biometric authentication"
            BiometricStatus.Unsupported -> "Biometric authentication is not supported"
        }
    }
}

/**
 * Biometric hardware status.
 */
enum class BiometricStatus {
    Available,
    NoHardware,
    HardwareUnavailable,
    NotEnrolled,
    SecurityUpdateRequired,
    Unsupported
}

/**
 * Result of biometric authentication attempt.
 */
sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data class Error(val errorCode: Int, val message: String) : BiometricResult()
}
