package com.example.finjan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val validCredentials = mapOf(
        "ahmed@gmail.com" to "FinjannA",
        "noureen@gmail.com" to "FinjannN",
        "shahd@gmail.com" to "FinjannS"
    )

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isEmailValid by mutableStateOf(true) // Tracks email validation state
    var isPasswordValid by mutableStateOf(true) // Tracks password validation state

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    fun isPasswordValid(password: String): Boolean {
        // Password regex: at least 8 characters, one uppercase, one lowercase, one digit, one special character
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    fun authenticate(): Boolean {
        val username = email.trim().lowercase()
        val userPassword = password
        return if (validCredentials[username] == userPassword) {
            true
        } else {
            errorMessage = "Invalid input"
            false
        }
    }
}
