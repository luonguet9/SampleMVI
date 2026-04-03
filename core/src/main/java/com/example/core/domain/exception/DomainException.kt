package com.example.core.domain.exception

/**
 * Sealed hierarchy of all domain-level exceptions.
 *
 * These are thrown from the Domain layer (UseCases, Repositories) and
 * caught by the ViewModel to produce appropriate [com.example.core.domain.result.DomainResult.Error] states.
 *
 * HOW TO USE IN NEW PROJECT:
 * - Add new subclasses for domain-specific errors (e.g., PaymentDeclinedException)
 * - Map low-level exceptions (IOException, HttpException) to these in your Repository
 *
 * ```kotlin
 * private fun mapException(throwable: Throwable): DomainException = when (throwable) {
 *     is DomainException -> throwable
 *     is IOException     -> DomainException.NetworkException("No internet: ${throwable.message}")
 *     is HttpException   -> when (throwable.code()) {
 *         401  -> DomainException.UnauthorizedException()
 *         404  -> DomainException.NotFoundException()
 *         else -> DomainException.NetworkException("HTTP ${throwable.code()}")
 *     }
 *     else -> DomainException.UnknownException(cause = throwable)
 * }
 * ```
 */
sealed class DomainException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    /** No internet connection or network timeout. */
    data class NetworkException(
        override val message: String = "Network error occurred"
    ) : DomainException(message)

    /** Local database read/write failure. */
    data class DatabaseException(
        override val message: String = "Database error occurred"
    ) : DomainException(message)

    /** Input validation failed (e.g., invalid email format). */
    data class ValidationException(
        override val message: String = "Validation failed"
    ) : DomainException(message)

    /** Requested resource does not exist (HTTP 404 equivalent). */
    data class NotFoundException(
        override val message: String = "Resource not found"
    ) : DomainException(message)

    /** User is not authenticated or session expired (HTTP 401 equivalent). */
    data class UnauthorizedException(
        override val message: String = "Unauthorized access"
    ) : DomainException(message)

    /** Catch-all for unexpected errors. Always include [cause] for debugging. */
    data class UnknownException(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)
}
