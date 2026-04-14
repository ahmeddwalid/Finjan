package com.example.finjan.utils.security

import java.util.regex.Pattern

/**
 * Input validation utilities for secure user input handling.
 * All validation methods sanitize input and check for malicious patterns.
 */
object InputValidator {

    // Password requirements
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_PASSWORD_LENGTH = 128
    private const val MAX_EMAIL_LENGTH = 254
    private const val MAX_NAME_LENGTH = 100
    private const val MAX_SEARCH_LENGTH = 200

    // Regex patterns
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#^()\\-_=+\\[\\]{}|;:'\",.<>/`~])[A-Za-z\\d@\$!%*?&#^()\\-_=+\\[\\]{}|;:'\",.<>/`~]{8,128}$"
    )
    
    private val NAME_PATTERN = Pattern.compile(
        "^[\\p{L}\\p{M}\\s'-]{1,100}$"
    )

    // SQL Injection patterns to block
    private val SQL_INJECTION_PATTERNS = listOf(
        Pattern.compile(".*([';]+|(--)+).*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*(union|select|insert|update|delete|drop|exec|execute).*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*(<|>|&|\\||\\$).*")
    )

    /**
     * Validate email format.
     * @return ValidationResult with status and optional error message
     */
    fun validateEmail(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.Error("Email is required")
        }
        
        val trimmedEmail = email.trim()
        
        if (trimmedEmail.length > MAX_EMAIL_LENGTH) {
            return ValidationResult.Error("Email is too long")
        }
        
        if (containsSqlInjection(trimmedEmail)) {
            return ValidationResult.Error("Invalid characters detected")
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return ValidationResult.Error("Please enter a valid email address")
        }
        
        return ValidationResult.Success
    }

    /**
     * Validate password strength.
     * Requirements: 8+ chars, uppercase, lowercase, digit, special char.
     */
    fun validatePassword(password: String?): ValidationResult {
        if (password.isNullOrEmpty()) {
            return ValidationResult.Error("Password is required")
        }
        
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters")
        }
        
        if (password.length > MAX_PASSWORD_LENGTH) {
            return ValidationResult.Error("Password is too long")
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ValidationResult.Error(
                "Password must contain uppercase, lowercase, number, and special character"
            )
        }
        
        return ValidationResult.Success
    }

    /**
     * Validate display name.
     * Allows Unicode letters, spaces, hyphens, and apostrophes.
     */
    fun validateName(name: String?): ValidationResult {
        if (name.isNullOrBlank()) {
            return ValidationResult.Error("Name is required")
        }
        
        val trimmedName = name.trim()
        
        if (trimmedName.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Name is too long")
        }
        
        if (containsSqlInjection(trimmedName)) {
            return ValidationResult.Error("Invalid characters detected")
        }
        
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            return ValidationResult.Error("Name contains invalid characters")
        }
        
        return ValidationResult.Success
    }

    /**
     * Validate and sanitize search input.
     */
    fun validateSearchQuery(query: String?): ValidationResult {
        if (query.isNullOrBlank()) {
            return ValidationResult.Success // Empty search is valid
        }
        
        if (query.length > MAX_SEARCH_LENGTH) {
            return ValidationResult.Error("Search query is too long")
        }
        
        if (containsSqlInjection(query)) {
            return ValidationResult.Error("Invalid search query")
        }
        
        return ValidationResult.Success
    }

    /**
     * Sanitize user input by removing potentially dangerous characters.
     */
    fun sanitizeInput(input: String): String {
        return input
            .replace(Regex("[<>\"';&|`\$]"), "")
            .trim()
            .take(MAX_SEARCH_LENGTH)
    }

    /**
     * Check for SQL injection patterns.
     */
    private fun containsSqlInjection(input: String): Boolean {
        return SQL_INJECTION_PATTERNS.any { pattern ->
            pattern.matcher(input).matches()
        }
    }

    /**
     * Validate passwords match for confirmation.
     */
    fun validatePasswordsMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password == confirmPassword) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Passwords do not match")
        }
    }
}

/**
 * Result type for validation operations.
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
    
    val isValid: Boolean get() = this is Success
    val errorMessage: String? get() = (this as? Error)?.message
}
