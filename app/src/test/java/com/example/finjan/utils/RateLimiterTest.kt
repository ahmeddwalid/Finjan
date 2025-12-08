package com.example.finjan.utils

import com.example.finjan.utils.security.RateLimiter
import kotlinx.coroutines.test.runTest
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
    fun `shouldAllow returns true for first attempt`() = runTest {
        val result = rateLimiter.shouldAllow("user@example.com")
        assertTrue(result)
    }
    
    @Test
    fun `shouldAllow returns true within max attempts`() = runTest {
        val key = "user@example.com"
        
        // Record 2 failed attempts
        rateLimiter.recordAttempt(key, false)
        rateLimiter.recordAttempt(key, false)
        
        // Third attempt should still be allowed
        assertTrue(rateLimiter.shouldAllow(key))
    }
    
    @Test
    fun `shouldAllow returns false after max attempts exceeded`() = runTest {
        val key = "user@example.com"
        
        // Record max failed attempts
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        
        // Next attempt should be blocked
        assertFalse(rateLimiter.shouldAllow(key))
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
        assertTrue(rateLimiter.shouldAllow(key))
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
        assertFalse(rateLimiter.shouldAllow(key1))
        
        // key2 should still be allowed
        assertTrue(rateLimiter.shouldAllow(key2))
    }
    
    @Test
    fun `reset clears rate limiter state`() = runTest {
        val key = "user@example.com"
        
        // Exhaust attempts
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        assertFalse(rateLimiter.shouldAllow(key))
        
        // Reset
        rateLimiter.reset()
        
        // Should be allowed again
        assertTrue(rateLimiter.shouldAllow(key))
    }
    
    @Test
    fun `getRemainingAttempts returns correct count`() = runTest {
        val key = "user@example.com"
        
        // Initially should have all attempts
        assertTrue(rateLimiter.getRemainingAttempts(key) == 3)
        
        // After one failed attempt
        rateLimiter.recordAttempt(key, false)
        assertTrue(rateLimiter.getRemainingAttempts(key) == 2)
    }
    
    @Test
    fun `isLocked returns false when not locked out`() = runTest {
        val key = "user@example.com"
        assertFalse(rateLimiter.isLocked(key))
    }
    
    @Test
    fun `isLocked returns true after lockout`() = runTest {
        val key = "user@example.com"
        
        // Exhaust attempts to trigger lockout
        repeat(3) {
            rateLimiter.recordAttempt(key, false)
        }
        
        assertTrue(rateLimiter.isLocked(key))
    }
}
