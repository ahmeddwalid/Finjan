package com.example.finjan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.UserProfileChangeRequest

class AuthenticationViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    var isEmailValid by mutableStateOf(true)
    var isPasswordValid by mutableStateOf(true)

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    // Firebase Authentication for sign-in
    fun signIn(onSuccess: () -> Unit) {
        isLoading = true
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    errorMessage = ""
                    onSuccess()
                } else {
                    val exception = task.exception
                    errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "No account found with this email."
                        is FirebaseAuthInvalidCredentialsException -> {
                            password = "" // Clear the password field
                            "Incorrect password. Please try again."
                        }
                        else -> exception?.localizedMessage ?: "Sign-in failed. Please check your credentials."
                    }
                }
            }

    }

    // Firebase Authentication for sign-up
    fun signUp(username: String, onSuccess: () -> Unit) {
        isLoading = true
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update user profile with username
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        isLoading = false
                        if (updateTask.isSuccessful) {
                            errorMessage = ""
                            onSuccess()
                        } else {
                            errorMessage = updateTask.exception?.localizedMessage ?: "Profile update failed."
                        }
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.localizedMessage ?: "Sign-up failed."
                }
            }
    }
}
