package com.example.finjan.utils

import com.example.finjan.utils.security.InputValidator
import com.example.finjan.utils.security.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidatorTest {
    
    // ==================== Email Validation Tests ====================
    
    @Test
    fun `validateEmail returns success for valid email`() {
        val result = InputValidator.validateEmail("test@example.com")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateEmail trims whitespace`() {
        val result = InputValidator.validateEmail("  test@example.com  ")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateEmail fails for empty email`() {
        val result = InputValidator.validateEmail("")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validateEmail fails for invalid format`() {
        val result = InputValidator.validateEmail("invalid-email")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validateEmail fails for email without domain`() {
        val result = InputValidator.validateEmail("test@")
        assertFalse(result.isValid)
    }
    
    // ==================== Password Validation Tests ====================
    
    @Test
    fun `validatePassword returns success for strong password`() {
        val result = InputValidator.validatePassword("StrongP@ss123")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validatePassword fails for short password`() {
        val result = InputValidator.validatePassword("Short1!")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validatePassword fails for password without uppercase`() {
        val result = InputValidator.validatePassword("weakpassword123!")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validatePassword fails for password without lowercase`() {
        val result = InputValidator.validatePassword("WEAKPASSWORD123!")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validatePassword fails for password without number`() {
        val result = InputValidator.validatePassword("WeakPassword!")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validatePassword fails for password without special character`() {
        val result = InputValidator.validatePassword("WeakPassword123")
        assertFalse(result.isValid)
    }
    
    // ==================== Name Validation Tests ====================
    
    @Test
    fun `validateName returns success for valid name`() {
        val result = InputValidator.validateName("John Doe")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateName allows arabic names`() {
        val result = InputValidator.validateName("أحمد محمد")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateName fails for empty name`() {
        val result = InputValidator.validateName("")
        assertFalse(result.isValid)
    }
    
    @Test
    fun `validateName fails for too long name`() {
        val longName = "A".repeat(101)
        val result = InputValidator.validateName(longName)
        assertFalse(result.isValid)
    }
    
    // ==================== Sanitization Tests ====================
    
    @Test
    fun `sanitizeInput removes dangerous characters`() {
        val result = InputValidator.sanitizeInput("Hello<script>alert('xss')</script>")
        assertFalse(result.contains("<"))
        assertFalse(result.contains(">"))
        assertFalse(result.contains("'"))
    }
    
    @Test
    fun `sanitizeInput truncates long strings`() {
        val longString = "A".repeat(500)
        val result = InputValidator.sanitizeInput(longString)
        assertTrue(result.length <= 200)
    }
    
    // ==================== Password Match Tests ====================
    
    @Test
    fun `validatePasswordsMatch returns success when passwords match`() {
        val result = InputValidator.validatePasswordsMatch("StrongP@ss123", "StrongP@ss123")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validatePasswordsMatch fails when passwords differ`() {
        val result = InputValidator.validatePasswordsMatch("StrongP@ss123", "DifferentP@ss456")
        assertFalse(result.isValid)
    }
    
    // ==================== Search Query Validation Tests ====================
    
    @Test
    fun `validateSearchQuery returns success for valid query`() {
        val result = InputValidator.validateSearchQuery("coffee")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateSearchQuery returns success for empty query`() {
        val result = InputValidator.validateSearchQuery("")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateSearchQuery fails for too long query`() {
        val longQuery = "A".repeat(201)
        val result = InputValidator.validateSearchQuery(longQuery)
        assertFalse(result.isValid)
    }
    
    // ==================== ValidationResult Tests ====================
    
    @Test
    fun `ValidationResult Success has no error message`() {
        val result = ValidationResult.Success
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }
    
    @Test
    fun `ValidationResult Error has error message`() {
        val result = ValidationResult.Error("test error")
        assertFalse(result.isValid)
        assertEquals("test error", result.errorMessage)
    }
}
