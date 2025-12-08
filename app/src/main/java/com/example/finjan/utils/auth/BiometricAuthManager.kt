package com.example.finjan.utils.auth

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manager for biometric authentication.
 * Supports fingerprint, face recognition, and device credentials.
 */
class BiometricAuthManager(private val context: Context) {

    companion object {
        private const val TAG = "BiometricAuthManager"
    }

    /**
     * Result of biometric authentication attempt.
     */
    sealed class BiometricResult {
        data object Success : BiometricResult()
        data class Error(val errorCode: Int, val message: String) : BiometricResult()
        data object Cancelled : BiometricResult()
        data object NotAvailable : BiometricResult()
        data object NotEnrolled : BiometricResult()
    }

    /**
     * Check if biometric authentication is available on this device.
     */
    fun canAuthenticate(): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SecurityUpdateRequired
            else -> BiometricAvailability.Unknown
        }
    }

    /**
     * Show biometric prompt and authenticate.
     * 
     * @param activity FragmentActivity context for showing the prompt
     * @param title Title of the prompt
     * @param subtitle Subtitle of the prompt
     * @param negativeButtonText Text for the negative button (e.g., "Cancel" or "Use Password")
     * @param allowDeviceCredential Whether to allow device PIN/password as fallback
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Authenticate",
        subtitle: String = "Confirm your identity",
        negativeButtonText: String = "Cancel",
        allowDeviceCredential: Boolean = true
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        
        val availability = canAuthenticate()
        if (availability != BiometricAvailability.Available) {
            continuation.resume(
                when (availability) {
                    BiometricAvailability.NotEnrolled -> BiometricResult.NotEnrolled
                    else -> BiometricResult.NotAvailable
                }
            )
            return@suspendCancellableCoroutine
        }

        val executor = ContextCompat.getMainExecutor(context)
        
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Log.d(TAG, "Authentication succeeded")
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Success)
                }
            }

            override fun onAuthenticationFailed() {
                Log.d(TAG, "Authentication failed (wrong biometric)")
                // Don't resume here - user can retry
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e(TAG, "Authentication error: $errorCode - $errString")
                if (continuation.isActive) {
                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.Cancelled
                        else -> BiometricResult.Error(errorCode, errString.toString())
                    }
                    continuation.resume(result)
                }
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setConfirmationRequired(false)

        if (allowDeviceCredential) {
            promptInfoBuilder.setAllowedAuthenticators(
                BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
            )
        } else {
            promptInfoBuilder
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
        }

        val promptInfo = promptInfoBuilder.build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show biometric prompt", e)
            if (continuation.isActive) {
                continuation.resume(BiometricResult.Error(-1, e.message ?: "Unknown error"))
            }
        }

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }
    }

    /**
     * Availability status of biometric authentication.
     */
    enum class BiometricAvailability {
        Available,
        NoHardware,
        HardwareUnavailable,
        NotEnrolled,
        SecurityUpdateRequired,
        Unknown
    }
}
