package com.example.finjan.utils.security

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

/**
 * Google Sign-In manager using Credential Manager API.
 * Provides secure OAuth authentication with Firebase integration.
 * 
 * IMPORTANT: Requires google-services.json with OAuth client ID configured
 * in Firebase Console > Authentication > Sign-in method > Google.
 */
class GoogleSignInManager(private val context: Context) {

    companion object {
        private const val TAG = "GoogleSignInManager"
        
        // Replace with your actual Web Client ID from Firebase Console
        // Found at: Firebase Console > Authentication > Sign-in method > Google > Web SDK configuration
        private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }

    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()

    /**
     * Initiate Google Sign-In flow.
     * @return GoogleSignInResult with success/failure information
     */
    suspend fun signIn(): GoogleSignInResult {
        return try {
            val nonce = generateNonce()
            val hashedNonce = hashNonce(nonce)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleSignInResult(result, nonce)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled sign-in")
            GoogleSignInResult.Cancelled
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No credentials available", e)
            GoogleSignInResult.Error("No Google accounts found on this device")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Failed to get credentials", e)
            GoogleSignInResult.Error("Sign-in failed: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sign-in", e)
            GoogleSignInResult.Error("An unexpected error occurred")
        }
    }

    /**
     * Handle the credential response and authenticate with Firebase.
     */
    private suspend fun handleSignInResult(
        result: GetCredentialResponse,
        nonce: String
    ): GoogleSignInResult {
        val credential = result.credential

        if (credential !is CustomCredential) {
            return GoogleSignInResult.Error("Invalid credential type")
        }

        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return GoogleSignInResult.Error("Unexpected credential type")
        }

        return try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Authenticate with Firebase
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            val user = authResult.user
            if (user != null) {
                Log.d(TAG, "Sign-in successful: ${user.email}")
                GoogleSignInResult.Success(
                    userId = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )
            } else {
                GoogleSignInResult.Error("Failed to get user after sign-in")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase authentication failed", e)
            GoogleSignInResult.Error("Authentication failed: ${e.message}")
        }
    }

    /**
     * Sign out from Google and Firebase.
     */
    suspend fun signOut() {
        try {
            auth.signOut()
            // Note: Credential Manager doesn't require explicit sign-out
            Log.d(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign-out", e)
        }
    }

    /**
     * Check if user is currently signed in.
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get current user info.
     */
    fun getCurrentUser(): GoogleUserInfo? {
        val user = auth.currentUser ?: return null
        return GoogleUserInfo(
            userId = user.uid,
            email = user.email,
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString()
        )
    }

    /**
     * Generate a random nonce for security.
     */
    private fun generateNonce(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Hash nonce for secure transmission.
     */
    private fun hashNonce(nonce: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(nonce.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * Result of Google Sign-In attempt.
 */
sealed class GoogleSignInResult {
    data class Success(
        val userId: String,
        val email: String?,
        val displayName: String?,
        val photoUrl: String?
    ) : GoogleSignInResult()

    data class Error(val message: String) : GoogleSignInResult()

    data object Cancelled : GoogleSignInResult()
}

/**
 * User information from Google Sign-In.
 */
data class GoogleUserInfo(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
