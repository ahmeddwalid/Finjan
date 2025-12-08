package com.example.finjan.utils.security

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Rate limiter for authentication attempts to prevent brute force attacks.
 * Implements exponential backoff with configurable limits.
 */
class RateLimiter(
    private val maxAttempts: Int = 5,
    private val windowMs: Long = 60_000L, // 1 minute window
    private val lockoutMs: Long = 300_000L // 5 minute lockout
) {
    private val attempts = ConcurrentHashMap<String, AttemptRecord>()
    private val mutex = Mutex()

    data class AttemptRecord(
        val count: Int,
        val firstAttemptTime: Long,
        val lockedUntil: Long = 0
    )

    /**
     * Check if an action is allowed for the given identifier.
     * @param identifier Unique identifier (e.g., email, IP)
     * @return RateLimitResult indicating if action is allowed
     */
    suspend fun checkLimit(identifier: String): RateLimitResult = mutex.withLock {
        val now = System.currentTimeMillis()
        val record = attempts[identifier]

        // No previous attempts
        if (record == null) {
            return RateLimitResult.Allowed
        }

        // Check if locked out
        if (record.lockedUntil > now) {
            val remainingMs = record.lockedUntil - now
            return RateLimitResult.Locked(remainingMs)
        }

        // Check if window has expired
        if (now - record.firstAttemptTime > windowMs) {
            attempts.remove(identifier)
            return RateLimitResult.Allowed
        }

        // Check if max attempts reached
        if (record.count >= maxAttempts) {
            val lockoutEnd = now + lockoutMs
            attempts[identifier] = record.copy(lockedUntil = lockoutEnd)
            return RateLimitResult.Locked(lockoutMs)
        }

        return RateLimitResult.Allowed
    }

    /**
     * Record an authentication attempt.
     * @param identifier Unique identifier
     * @param success Whether the attempt was successful
     */
    suspend fun recordAttempt(identifier: String, success: Boolean) = mutex.withLock {
        val now = System.currentTimeMillis()

        if (success) {
            // Clear attempts on successful authentication
            attempts.remove(identifier)
            return@withLock
        }

        val record = attempts[identifier]
        if (record == null || now - record.firstAttemptTime > windowMs) {
            // Start new window
            attempts[identifier] = AttemptRecord(1, now)
        } else {
            // Increment attempt count
            attempts[identifier] = record.copy(count = record.count + 1)
        }
    }

    /**
     * Get remaining attempts for an identifier.
     */
    suspend fun getRemainingAttempts(identifier: String): Int = mutex.withLock {
        val record = attempts[identifier] ?: return@withLock maxAttempts
        val now = System.currentTimeMillis()
        
        if (now - record.firstAttemptTime > windowMs) {
            return@withLock maxAttempts
        }
        
        return@withLock (maxAttempts - record.count).coerceAtLeast(0)
    }

    /**
     * Clear rate limit data for an identifier.
     */
    suspend fun reset(identifier: String) = mutex.withLock {
        attempts.remove(identifier)
    }

    /**
     * Clear all rate limit data.
     */
    suspend fun resetAll() = mutex.withLock {
        attempts.clear()
    }

    /**
     * Add delay based on failed attempts (exponential backoff).
     */
    suspend fun applyBackoff(identifier: String) {
        val record = attempts[identifier] ?: return
        val delayMs = calculateBackoff(record.count)
        if (delayMs > 0) {
            delay(delayMs)
        }
    }

    private fun calculateBackoff(attemptCount: Int): Long {
        return when {
            attemptCount <= 1 -> 0
            attemptCount == 2 -> 1_000 // 1 second
            attemptCount == 3 -> 2_000 // 2 seconds
            attemptCount == 4 -> 5_000 // 5 seconds
            else -> 10_000 // 10 seconds
        }
    }
}

/**
 * Result type for rate limit checks.
 */
sealed class RateLimitResult {
    data object Allowed : RateLimitResult()
    data class Locked(val remainingMs: Long) : RateLimitResult() {
        val remainingSeconds: Int get() = (remainingMs / 1000).toInt()
        val remainingMinutes: Int get() = (remainingMs / 60_000).toInt()
    }
}
