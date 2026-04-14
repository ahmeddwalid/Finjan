package com.example.finjan.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.utils.auth.GoogleAuthManager
import com.example.finjan.utils.security.InputValidator
import com.example.finjan.utils.security.RateLimitResult
import com.example.finjan.utils.security.RateLimiter
import com.example.finjan.utils.security.ValidationResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GoogleAuthState {
    data object Idle : GoogleAuthState
    data object Loading : GoogleAuthState
    data object Success : GoogleAuthState
    data class Error(val message: String) : GoogleAuthState
}

/**
 * Production-ready ViewModel for authentication operations.
 * Features: Input validation, rate limiting, secure error handling, Google SSO.
 */
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val rateLimiter: RateLimiter,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    // UI State
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // Validation states
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    // Rate limiting state
    var isLockedOut by mutableStateOf(false)
    var lockoutRemainingSeconds by mutableStateOf(0)

    // Google SSO state
    private val _googleAuthState = MutableStateFlow<GoogleAuthState>(GoogleAuthState.Idle)
    val googleAuthState: StateFlow<GoogleAuthState> = _googleAuthState.asStateFlow()

    /**
     * Check if user is currently logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Validate email on input change.
     */
    fun onEmailChange(value: String) {
        email = InputValidator.sanitizeInput(value)
        val result = InputValidator.validateEmail(email)
        emailError = result.errorMessage
    }

    /**
     * Update password (no sanitization for passwords).
     */
    fun onPasswordChange(value: String) {
        password = value
        val result = InputValidator.validatePassword(password)
        passwordError = result.errorMessage
    }

    /**
     * Firebase Authentication for sign-in with security features.
     */
    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Check rate limit
            when (val rateLimitResult = rateLimiter.checkLimit(email)) {
                is RateLimitResult.Locked -> {
                    isLockedOut = true
                    lockoutRemainingSeconds = rateLimitResult.remainingSeconds
                    errorMessage = "Too many attempts. Please try again in ${rateLimitResult.remainingMinutes + 1} minutes."
                    return@launch
                }
                RateLimitResult.Allowed -> isLockedOut = false
            }

            // Validate inputs
            val emailValidation = InputValidator.validateEmail(email)
            if (!emailValidation.isValid) {
                errorMessage = emailValidation.errorMessage ?: "Invalid email"
                rateLimiter.recordAttempt(email, false)
                return@launch
            }

            if (password.isEmpty()) {
                errorMessage = "Password is required"
                return@launch
            }

            isLoading = true
            clearMessages()

            // Apply backoff delay
            rateLimiter.applyBackoff(email)

            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    isLoading = false
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            rateLimiter.recordAttempt(email, true)
                            clearMessages()
                            clearInputs()
                            onSuccess()
                        } else {
                            rateLimiter.recordAttempt(email, false)
                            val remainingAttempts = rateLimiter.getRemainingAttempts(email)
                            
                            errorMessage = when (task.exception) {
                                is FirebaseAuthInvalidUserException -> 
                                    "No account found with this email"
                                is FirebaseAuthInvalidCredentialsException -> {
                                    password = ""
                                    if (remainingAttempts > 0) {
                                        "Incorrect password. $remainingAttempts attempts remaining."
                                    } else {
                                        "Incorrect password. Account locked."
                                    }
                                }
                                else -> "Sign-in failed. Please try again."
                            }
                        }
                    }
                }
        }
    }

    /**
     * Firebase Authentication for sign-up with validation.
     */
    fun signUp(username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Validate inputs
            val nameValidation = InputValidator.validateName(username)
            if (!nameValidation.isValid) {
                errorMessage = nameValidation.errorMessage ?: "Invalid name"
                return@launch
            }

            val emailValidation = InputValidator.validateEmail(email)
            if (!emailValidation.isValid) {
                errorMessage = emailValidation.errorMessage ?: "Invalid email"
                return@launch
            }

            val passwordValidation = InputValidator.validatePassword(password)
            if (!passwordValidation.isValid) {
                errorMessage = passwordValidation.errorMessage ?: "Invalid password"
                return@launch
            }

            isLoading = true
            clearMessages()

            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val sanitizedName = InputValidator.sanitizeInput(username)
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(sanitizedName)
                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                            isLoading = false
                            if (updateTask.isSuccessful) {
                                clearMessages()
                                clearInputs()
                                onSuccess()
                            } else {
                                errorMessage = "Account created but profile update failed"
                            }
                        }
                    } else {
                        isLoading = false
                        errorMessage = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> 
                                "An account with this email already exists"
                            is FirebaseAuthWeakPasswordException -> 
                                "Password is too weak"
                            else -> "Sign-up failed. Please try again."
                        }
                    }
                }
        }
    }

    /**
     * Send password reset email with rate limiting.
     */
    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            // Check rate limit
            when (val result = rateLimiter.checkLimit("reset_$email")) {
                is RateLimitResult.Locked -> {
                    errorMessage = "Please wait before requesting another reset email"
                    return@launch
                }
                RateLimitResult.Allowed -> {}
            }

            val emailValidation = InputValidator.validateEmail(email)
            if (!emailValidation.isValid) {
                errorMessage = emailValidation.errorMessage ?: "Invalid email"
                return@launch
            }

            isLoading = true
            clearMessages()

            auth.sendPasswordResetEmail(email.trim())
                .addOnCompleteListener { task ->
                    isLoading = false
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            rateLimiter.recordAttempt("reset_$email", false) // Track to prevent spam
                            successMessage = "Password reset email sent! Check your inbox."
                        } else {
                            // Don't reveal if email exists or not
                            successMessage = "If an account exists with this email, you will receive a reset link."
                        }
                    }
                }
        }
    }

    /**
     * Change password with re-authentication.
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        val user = auth.currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            errorMessage = "Session expired. Please sign in again."
            return
        }

        val passwordValidation = InputValidator.validatePassword(newPassword)
        if (!passwordValidation.isValid) {
            errorMessage = passwordValidation.errorMessage ?: "Invalid password"
            return
        }

        if (currentPassword == newPassword) {
            errorMessage = "New password must be different from current password"
            return
        }

        isLoading = true
        clearMessages()

        val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)
        
        user.reauthenticate(credential)
            .addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            isLoading = false
                            if (updateTask.isSuccessful) {
                                successMessage = "Password changed successfully!"
                                onSuccess()
                            } else {
                                errorMessage = "Failed to update password. Please try again."
                            }
                        }
                } else {
                    isLoading = false
                    errorMessage = "Current password is incorrect"
                }
            }
    }

    /**
     * Update user display name with validation.
     */
    fun updateDisplayName(newName: String, onSuccess: () -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            errorMessage = "Session expired. Please sign in again."
            return
        }

        val nameValidation = InputValidator.validateName(newName)
        if (!nameValidation.isValid) {
            errorMessage = nameValidation.errorMessage ?: "Invalid name"
            return
        }

        isLoading = true
        clearMessages()

        val sanitizedName = InputValidator.sanitizeInput(newName)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(sanitizedName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    successMessage = "Profile updated successfully!"
                    onSuccess()
                } else {
                    errorMessage = "Failed to update profile. Please try again."
                }
            }
    }

    /**
     * Initiate Google Sign-In via CredentialManager.
     * @param activityContext Activity context required for credential picker UI
     */
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _googleAuthState.value = GoogleAuthState.Loading
            isLoading = true

            when (val result = googleAuthManager.signIn(activityContext)) {
                is GoogleAuthManager.GoogleSignInResult.Success -> {
                    _googleAuthState.value = GoogleAuthState.Success
                    isLoading = false
                }
                is GoogleAuthManager.GoogleSignInResult.Error -> {
                    _googleAuthState.value = GoogleAuthState.Error(result.message)
                    errorMessage = result.message
                    isLoading = false
                }
                is GoogleAuthManager.GoogleSignInResult.Cancelled -> {
                    _googleAuthState.value = GoogleAuthState.Idle
                    isLoading = false
                }
            }
        }
    }

    /**
     * Reset the Google auth state after navigation.
     */
    fun resetGoogleAuthState() {
        _googleAuthState.value = GoogleAuthState.Idle
    }

    /**
     * Clear error and success messages.
     */
    fun clearMessages() {
        errorMessage = ""
        successMessage = ""
    }

    /**
     * Clear all input fields.
     */
    private fun clearInputs() {
        email = ""
        password = ""
        emailError = null
        passwordError = null
    }

    /**
     * Legacy validation methods for backward compatibility.
     */
    @Deprecated("Use InputValidator.validateEmail instead")
    fun isEmailValid(email: String): Boolean {
        return InputValidator.validateEmail(email).isValid
    }

    @Deprecated("Use InputValidator.validatePassword instead")
    fun isPasswordValid(password: String): Boolean {
        return InputValidator.validatePassword(password).isValid
    }

    // Keep these for backward compatibility with existing screens
    var isEmailValid by mutableStateOf(true)
    var isPasswordValid by mutableStateOf(true)
}