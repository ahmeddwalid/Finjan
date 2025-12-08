package com.example.finjan.utils

/**
 * Result wrapper for handling success and error states.
 * Useful for ViewModel operations and API calls.
 */
sealed class Result<out T> {
    
    data class Success<T>(val data: T) : Result<T>()
    
    data class Error(
        val message: String,
        val exception: Throwable? = null,
        val code: ErrorCode = ErrorCode.UNKNOWN
    ) : Result<Nothing>()
    
    data object Loading : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Get data if Success, otherwise null.
     */
    fun getOrNull(): T? = (this as? Success)?.data
    
    /**
     * Get data if Success, otherwise throw.
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: Exception(message)
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    /**
     * Get data if Success, otherwise return default.
     */
    fun getOrDefault(default: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> default
    }
    
    /**
     * Transform success data.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }
    
    /**
     * Execute action on success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Execute action on error.
     */
    inline fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }
    
    /**
     * Execute action on loading.
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Error codes for categorized error handling.
 */
enum class ErrorCode {
    UNKNOWN,
    NETWORK_ERROR,
    AUTHENTICATION_ERROR,
    VALIDATION_ERROR,
    NOT_FOUND,
    PERMISSION_DENIED,
    SERVER_ERROR,
    TIMEOUT,
    CANCELLED
}

/**
 * Extension to convert throwable to Result.Error.
 */
fun Throwable.toResultError(code: ErrorCode = ErrorCode.UNKNOWN): Result.Error {
    return Result.Error(
        message = this.localizedMessage ?: "An unexpected error occurred",
        exception = this,
        code = code
    )
}

/**
 * Run a block and catch exceptions as Result.Error.
 */
inline fun <T> runCatching(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        e.toResultError()
    }
}
