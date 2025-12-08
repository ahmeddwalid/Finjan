package com.example.finjan.utils

import com.example.finjan.utils.security.InputValidator
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
        assertEquals("test@example.com", result.sanitizedValue)
    }
    
    @Test
    fun `validateEmail trims whitespace`() {
        val result = InputValidator.validateEmail("  test@example.com  ")
        assertTrue(result.isValid)
        assertEquals("test@example.com", result.sanitizedValue)
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
        assertEquals("John Doe", result.sanitizedValue)
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
    fun `validateName fails for too short name`() {
        val result = InputValidator.validateName("A")
        assertFalse(result.isValid)
    }
    
    // ==================== Sanitization Tests ====================
    
    @Test
    fun `sanitizeForDisplay removes control characters`() {
        val result = InputValidator.sanitizeForDisplay("Hello\u0000World")
        assertFalse(result.contains("\u0000"))
    }
    
    @Test
    fun `sanitizeForDisplay truncates long strings`() {
        val longString = "A".repeat(1000)
        val result = InputValidator.sanitizeForDisplay(longString, maxLength = 100)
        assertEquals(100, result.length)
    }
    
    // ==================== SQL Injection Prevention Tests ====================
    
    @Test
    fun `sanitizeForDatabase removes SQL injection patterns`() {
        val malicious = "1; DROP TABLE users;--"
        val result = InputValidator.sanitizeForDatabase(malicious)
        assertFalse(result.contains(";"))
        assertFalse(result.contains("--"))
    }
    
    @Test
    fun `containsSqlInjectionPatterns detects basic SQL injection`() {
        assertTrue(InputValidator.containsSqlInjectionPatterns("1 OR 1=1"))
        assertTrue(InputValidator.containsSqlInjectionPatterns("1; DROP TABLE"))
        assertTrue(InputValidator.containsSqlInjectionPatterns("' OR ''='"))
    }
    
    @Test
    fun `containsSqlInjectionPatterns returns false for normal input`() {
        assertFalse(InputValidator.containsSqlInjectionPatterns("John Doe"))
        assertFalse(InputValidator.containsSqlInjectionPatterns("test@example.com"))
    }
    
    // ==================== Phone Number Validation Tests ====================
    
    @Test
    fun `validatePhoneNumber returns success for valid number`() {
        val result = InputValidator.validatePhoneNumber("+1234567890")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validatePhoneNumber fails for too short number`() {
        val result = InputValidator.validatePhoneNumber("12345")
        assertFalse(result.isValid)
    }
    
    // ==================== Search Query Validation Tests ====================
    
    @Test
    fun `validateSearchQuery returns success for valid query`() {
        val result = InputValidator.validateSearchQuery("coffee")
        assertTrue(result.isValid)
    }
    
    @Test
    fun `validateSearchQuery truncates long queries`() {
        val longQuery = "A".repeat(200)
        val result = InputValidator.validateSearchQuery(longQuery)
        assertTrue(result.sanitizedValue.length <= 100)
    }
}
