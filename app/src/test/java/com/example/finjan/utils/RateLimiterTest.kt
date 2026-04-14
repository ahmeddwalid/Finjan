package com.example.finjan.utils

import com.example.finjan.utils.security.RateLimiter
import com.example.finjan.utils.security.RateLimitResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RateLimiterTest {
    
    private lateinit var rateLimiter: RateLimiter
    
    @Before
    fun setup() {
        rateLimiter = RateLimiter(
            maxAttempts = 3,
            windowMs = 60_000, // 1 minute
            lockoutMs = 300_000 // 5 minutes
        )
    }
    
    @Test
    fun `checkLimit returns Allowed for first attempt`() = runTest {
        val result = rateLimiter.checkLimit("user@example.com")
        assertTrue(result is RateLimitResult.Allowed)
    }
    
    @Test
    fun `checkLimit returns Allowed within max attempts`() = runTest {
        val key = "user@example.com"
        
        // Record 2 failed attempts
        rateLimiter.recordAttempt(key, false)
        rateLimiter.recordAttempt(key, false)
        
        // Third attempt should still be allowed
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Allowed)
    }
    
    @Test
    fun `checkLimit returns Locked after max attempts exceeded`() = runTest {
        val key = "user@example.com"
        
        // Record max failed attempts
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        
        // Next attempt should be blocked
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Locked)
    }
    
    @Test
    fun `successful attempt resets counter`() = runTest {
        val key = "user@example.com"
        
        // Record some failed attempts
        rateLimiter.recordAttempt(key, false)
        rateLimiter.recordAttempt(key, false)
        
        // Record successful attempt
        rateLimiter.recordAttempt(key, true)
        
        // Should be allowed again after success
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Allowed)
    }
    
    @Test
    fun `different keys are tracked independently`() = runTest {
        val key1 = "user1@example.com"
        val key2 = "user2@example.com"
        
        // Exhaust attempts for key1
        repeat(3) {
            rateLimiter.recordAttempt(key1, false)
        }
        
        // key1 should be blocked
        assertTrue(rateLimiter.checkLimit(key1) is RateLimitResult.Locked)
        
        // key2 should still be allowed
        assertTrue(rateLimiter.checkLimit(key2) is RateLimitResult.Allowed)
    }
    
    @Test
    fun `resetAll clears rate limiter state`() = runTest {
        val key = "user@example.com"
        
        // Exhaust attempts
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Locked)
        
        // Reset all
        rateLimiter.resetAll()
        
        // Should be allowed again
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Allowed)
    }
    
    @Test
    fun `reset clears state for specific identifier`() = runTest {
        val key = "user@example.com"
        
        // Exhaust attempts
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Locked)
        
        // Reset specific key
        rateLimiter.reset(key)
        
        // Should be allowed again
        assertTrue(rateLimiter.checkLimit(key) is RateLimitResult.Allowed)
    }
    
    @Test
    fun `getRemainingAttempts returns correct count`() = runTest {
        val key = "user@example.com"
        
        // Initially should have all attempts
        assertEquals(3, rateLimiter.getRemainingAttempts(key))
        
        // After one failed attempt
        rateLimiter.recordAttempt(key, false)
        assertEquals(2, rateLimiter.getRemainingAttempts(key))
    }
    
    @Test
    fun `Locked result provides remaining time`() = runTest {
        val key = "user@example.com"
        
        // Exhaust attempts to trigger lockout
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        
        val result = rateLimiter.checkLimit(key)
        assertTrue(result is RateLimitResult.Locked)
        assertTrue((result as RateLimitResult.Locked).remainingMs > 0)
    }
}
