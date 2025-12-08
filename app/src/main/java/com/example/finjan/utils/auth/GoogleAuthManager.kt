package com.example.finjan.utils.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

/**
 * Manager for Google Sign-In using Credential Manager API.
 * 
 * Prerequisites:
 * 1. Configure Firebase project with SHA-1 fingerprint
 * 2. Enable Google Sign-In in Firebase Console
 * 3. Add google-services.json to app directory
 * 4. Set WEB_CLIENT_ID from Firebase Console (OAuth 2.0 Web Client ID)
 */
class GoogleAuthManager(private val context: Context) {
    
    companion object {
        private const val TAG = "GoogleAuthManager"
        
        // Get this from Firebase Console -> Project Settings -> General -> Web Client ID
        // This is the OAuth 2.0 Client ID with type "Web application"  
        const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }
    
    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()
    
    /**
     * Result of Google Sign-In attempt.
     */
    sealed class GoogleSignInResult {
        data class Success(val userId: String, val email: String?, val displayName: String?) : GoogleSignInResult()
        data class Error(val message: String, val exception: Exception? = null) : GoogleSignInResult()
        data object Cancelled : GoogleSignInResult()
    }
    
    /**
     * Initiate Google Sign-In flow.
     * 
     * @return GoogleSignInResult indicating success, error, or cancellation
     */
    suspend fun signIn(): GoogleSignInResult {
        return try {
            // Generate nonce for security
            val nonce = generateNonce()
            val hashedNonce = hashNonce(nonce)
            
            // Build Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show all accounts
                .setServerClientId(WEB_CLIENT_ID)
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(true) // Auto-select if only one account
                .build()
            
            // Build credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            // Get credential
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            
            handleSignInResult(result, nonce)
            
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException: ${e.message}", e)
            when (e.type) {
                "android.credentials.GetCredentialException.TYPE_USER_CANCELED" -> {
                    GoogleSignInResult.Cancelled
                }
                "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL" -> {
                    GoogleSignInResult.Error("No Google accounts available. Please add a Google account to your device.")
                }
                else -> {
                    GoogleSignInResult.Error(e.message ?: "Google Sign-In failed", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during sign-in: ${e.message}", e)
            GoogleSignInResult.Error(e.message ?: "An unexpected error occurred", e)
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
        
        return when {
            credential is CustomCredential && 
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    // Parse Google ID token
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    
                    // Authenticate with Firebase
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    
                    val user = authResult.user
                    if (user != null) {
                        Log.i(TAG, "Successfully signed in: ${user.uid}")
                        GoogleSignInResult.Success(
                            userId = user.uid,
                            email = user.email,
                            displayName = user.displayName
                        )
                    } else {
                        GoogleSignInResult.Error("Failed to get user after sign-in")
                    }
                    
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "GoogleIdTokenParsingException: ${e.message}", e)
                    GoogleSignInResult.Error("Failed to parse Google ID token", e)
                }
            }
            else -> {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                GoogleSignInResult.Error("Unexpected credential type received")
            }
        }
    }
    
    /**
     * Generate a random nonce for security.
     */
    private fun generateNonce(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * Hash the nonce using SHA-256.
     */
    private fun hashNonce(nonce: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(nonce.toByteArray())
        return hash.fold("") { str, byte -> str + "%02x".format(byte) }
    }
    
    /**
     * Sign out from Firebase.
     */
    suspend fun signOut() {
        auth.signOut()
        Log.i(TAG, "User signed out")
    }
    
    /**
     * Check if user is currently signed in.
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Get current user ID if signed in.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
