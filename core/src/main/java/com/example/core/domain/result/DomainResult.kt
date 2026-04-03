package com.example.core.domain.result

import com.example.core.domain.exception.DomainException

/**
 * Sealed class representing the three possible states of an async operation.
 *
 * Named [DomainResult] (not 'Result') to avoid clash with [kotlin.Result].
 *
 * Typical flow from Repository → UseCase → ViewModel:
 * ```
 * Repository emits:  Loading → Success(data)  OR  Loading → Error(exception)
 * ViewModel handles: updateState { copy(isLoading = true) }
 *                    updateState { copy(users = result.data) }
 *                    updateState { copy(error = result.exception) }
 * ```
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * // In Repository:
 * fun getUsers(): Flow<DomainResult<List<User>>> = flow {
 *     emit(DomainResult.Loading)
 *     val users = api.getUsers()
 *     emit(DomainResult.Success(users.map { mapper.map(it) }))
 * }
 *
 * // In ViewModel:
 * getUsersUseCase(Unit).onEach { result ->
 *     when (result) {
 *         is DomainResult.Loading  -> updateState { copy(isLoading = true) }
 *         is DomainResult.Success  -> updateState { copy(users = result.data, isLoading = false) }
 *         is DomainResult.Error    -> updateState { copy(error = result.exception, isLoading = false) }
 *     }
 * }.launchIn()
 * ```
 */
sealed class DomainResult<out T> {

    /** Operation succeeded with [data]. */
    data class Success<T>(val data: T) : DomainResult<T>()

    /** Operation failed with a [DomainException]. */
    data class Error(val exception: DomainException) : DomainResult<Nothing>()

    /** Operation is in progress (loading indicator). */
    object Loading : DomainResult<Nothing>()

    // ─── Convenience properties ───────────────────────────────────────────────

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    // ─── Chainable operators ──────────────────────────────────────────────────

    inline fun onSuccess(action: (T) -> Unit): DomainResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (DomainException) -> Unit): DomainResult<T> {
        if (this is Error) action(exception)
        return this
    }

    inline fun onLoading(action: () -> Unit): DomainResult<T> {
        if (this is Loading) action()
        return this
    }

    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error   -> Error(exception)
        is Loading -> Loading
    }
}
