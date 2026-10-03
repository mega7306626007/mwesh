package com.mweshimiwa.assistant.data.error

sealed class AppError(
    val message: String,
    val code: String? = null,
    val cause: Throwable? = null
) {
    class NetworkError(
        message: String = "Network error occurred",
        code: String = "NETWORK_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class ServerError(
        message: String = "Server error occurred",
        code: String = "SERVER_ERROR",
        val statusCode: Int = 500,
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class AuthenticationError(
        message: String = "Authentication failed",
        code: String = "AUTH_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class AuthorizationError(
        message: String = "Not authorized",
        code: String = "FORBIDDEN",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class NotFoundError(
        message: String = "Resource not found",
        code: String = "NOT_FOUND",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class ValidationError(
        message: String = "Validation failed",
        code: String = "VALIDATION_ERROR",
        val errors: Map<String, String> = emptyMap(),
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class ConflictError(
        message: String = "Conflict occurred",
        code: String = "CONFLICT",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class RateLimitError(
        message: String = "Rate limit exceeded",
        code: String = "RATE_LIMIT",
        val retryAfter: Long = 0L,
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class TimeoutError(
        message: String = "Request timed out",
        code: String = "TIMEOUT",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class CacheError(
        message: String = "Cache error occurred",
        code: String = "CACHE_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class DatabaseError(
        message: String = "Database error occurred",
        code: String = "DATABASE_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class FileSystemError(
        message: String = "File system error occurred",
        code: String = "FILE_SYSTEM_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class PermissionError(
        message: String = "Permission denied",
        code: String = "PERMISSION_DENIED",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class ConfigurationError(
        message: String = "Configuration error",
        code: String = "CONFIG_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class InitializationError(
        message: String = "Initialization failed",
        code: String = "INIT_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class SyncError(
        message: String = "Sync failed",
        code: String = "SYNC_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class UploadError(
        message: String = "Upload failed",
        code: String = "UPLOAD_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class DownloadError(
        message: String = "Download failed",
        code: String = "DOWNLOAD_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class RegistrationError(
        message: String = "Registration failed",
        code: String = "REGISTRATION_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class InteractionError(
        message: String = "Interaction failed",
        code: String = "INTERACTION_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    class UnknownError(
        message: String = "Unknown error occurred",
        code: String = "UNKNOWN_ERROR",
        cause: Throwable? = null
    ) : AppError(message, code, cause)

    fun isNetworkError(): Boolean = this is NetworkError
    fun isServerError(): Boolean = this is ServerError
    fun isAuthError(): Boolean = this is AuthenticationError || this is AuthorizationError
    fun isNotFoundError(): Boolean = this is NotFoundError
    fun isValidationError(): Boolean = this is ValidationError
    fun isCacheError(): Boolean = this is CacheError
    fun isDatabaseError(): Boolean = this is DatabaseError
    fun isPermissionError(): Boolean = this is PermissionError
    fun isTimeoutError(): Boolean = this is TimeoutError
    fun isRateLimitError(): Boolean = this is RateLimitError
    fun isSyncError(): Boolean = this is SyncError
    fun isUploadError(): Boolean = this is UploadError
    fun isDownloadError(): Boolean = this is DownloadError
    fun isUnknownError(): Boolean = this is UnknownError

    fun toMap(): Map<String, Any> {
        return mapOf(
            "message" to message,
            "code" to (code ?: ""),
            "cause" to (cause?.message ?: "")
        )
    }

    companion object {
        fun fromThrowable(throwable: Throwable): AppError {
            return when (throwable) {
                is java.net.UnknownHostException -> NetworkError(cause = throwable)
                is java.net.SocketTimeoutException -> TimeoutError(cause = throwable)
                is java.io.IOException -> NetworkError(cause = throwable)
                is SecurityException -> PermissionError(cause = throwable)
                else -> UnknownError(
                    message = throwable.message ?: "Unknown error",
                    cause = throwable
                )
            }
        }

        fun fromStatusCode(statusCode: Int, message: String? = null): AppError {
            return when (statusCode) {
                400 -> ValidationError(message ?: "Bad request")
                401 -> AuthenticationError(message ?: "Unauthorized")
                403 -> AuthorizationError(message ?: "Forbidden")
                404 -> NotFoundError(message ?: "Not found")
                409 -> ConflictError(message ?: "Conflict")
                422 -> ValidationError(message ?: "Unprocessable entity")
                429 -> RateLimitError(message ?: "Too many requests")
                500 -> ServerError(message ?: "Internal server error", statusCode = 500)
                502 -> ServerError(message ?: "Bad gateway", statusCode = 502)
                503 -> ServerError(message ?: "Service unavailable", statusCode = 503)
                504 -> TimeoutError(message ?: "Gateway timeout")
                else -> ServerError(message ?: "Server error", statusCode = statusCode)
            }
        }
    }
}

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val code: String? = null,
    val details: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val requestId: String? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "success" to success,
            "message" to message,
            "code" to (code ?: ""),
            "details" to (details ?: ""),
            "timestamp" to timestamp,
            "path" to (path ?: ""),
            "requestId" to (requestId ?: "")
        )
    }

    companion object {
        fun create(
            message: String,
            code: String? = null,
            details: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                message = message,
                code = code,
                details = details
            )
        }

        fun fromAppError(error: AppError): ErrorResponse {
            return ErrorResponse(
                message = error.message,
                code = error.code,
                details = error.cause?.message
            )
        }
    }
}

data class ValidationErrorDetail(
    val field: String,
    val message: String,
    val code: String? = null,
    val value: Any? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "field" to field,
            "message" to message,
            "code" to (code ?: ""),
            "value" to (value ?: "")
        )
    }
}

data class ApiError(
    val status: Int,
    val message: String,
    val code: String? = null,
    val errors: List<ValidationErrorDetail> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "status" to status,
            "message" to message,
            "code" to (code ?: ""),
            "errors" to errors.map { it.toMap() },
            "timestamp" to timestamp
        )
    }
}
