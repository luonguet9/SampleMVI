package com.example.core.base.usecase

import com.example.core.domain.exception.DomainException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

// ─── Suspend UseCase ──────────────────────────────────────────────────────────

/**
 * Base class for a use case that executes a single suspend operation.
 *
 * Use when the repository call is a one-shot suspend function (not a Flow).
 * Returns [DomainResult] so the ViewModel doesn't need to catch exceptions.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class LoginUseCase @Inject constructor(
 *     private val authRepository: AuthRepository
 * ) : UseCase<LoginUseCase.Params, DomainResult<User>>() {
 *
 *     override suspend fun execute(params: Params): DomainResult<User> {
 *         return authRepository.login(params.email, params.password)
 *     }
 *
 *     data class Params(val email: String, val password: String)
 * }
 * ```
 *
 * @param P Parameter type — use [Unit] if no params are needed
 * @param R Return type — typically [com.example.core.domain.result.DomainResult]<T>
 */
abstract class UseCase<in P, out R> {
    suspend operator fun invoke(params: P): R = execute(params)

    @Throws(DomainException::class)
    protected abstract suspend fun execute(params: P): R
}

// ─── No-Params Suspend UseCase ────────────────────────────────────────────────

/**
 * Shortcut for [UseCase] with no parameters.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class GetCurrentUserUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : NoParamsUseCase<DomainResult<User>>() {
 *
 *     override suspend fun execute(): DomainResult<User> =
 *         userRepository.getCurrentUser()
 * }
 * // Call site: val result = getCurrentUserUseCase()
 * ```
 */
abstract class NoParamsUseCase<out R> {
    suspend operator fun invoke(): R = execute()

    @Throws(DomainException::class)
    protected abstract suspend fun execute(): R
}

// ─── Flow UseCase ─────────────────────────────────────────────────────────────

/**
 * Base class for a use case that returns a [Flow] (reactive / streaming).
 *
 * Automatically wraps exceptions using [mapException], so the downstream
 * collector receives [DomainException] instead of raw exceptions.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class ObserveUsersUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : FlowUseCase<Unit, DomainResult<List<User>>>() {
 *
 *     override fun execute(params: Unit): Flow<DomainResult<List<User>>> =
 *         userRepository.observeUsers()
 * }
 * // Call site: observeUsersUseCase(Unit).onEach { result -> ... }.launchIn()
 * ```
 *
 * @param P Parameter type — use [Unit] if no params are needed
 * @param R Return type — typically [com.example.core.domain.result.DomainResult]<T>
 */
abstract class FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<R> = execute(params)
        .catch { e -> throw mapException(e) }

    protected abstract fun execute(params: P): Flow<R>

    /**
     * Override to add custom exception mapping (e.g., convert API errors to domain errors).
     * By default, any [DomainException] is re-thrown as-is; anything else becomes [DomainException.UnknownException].
     */
    protected open fun mapException(throwable: Throwable): DomainException {
        return when (throwable) {
            is DomainException -> throwable
            else -> DomainException.UnknownException(
                throwable.message ?: "Unknown error",
                throwable
            )
        }
    }
}

// ─── No-Params Flow UseCase ───────────────────────────────────────────────────

/**
 * Shortcut for [FlowUseCase] with no parameters.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class ObserveNetworkStatusUseCase @Inject constructor(
 *     private val networkConnectivity: NetworkConnectivity
 * ) : NoParamsFlowUseCase<NetworkStatus>() {
 *
 *     override fun execute(): Flow<NetworkStatus> =
 *         networkConnectivity.observeConnectivity()
 * }
 * ```
 */
abstract class NoParamsFlowUseCase<out R> {
    operator fun invoke(): Flow<R> = execute()
        .catch { e -> throw mapException(e) }

    protected abstract fun execute(): Flow<R>

    protected open fun mapException(throwable: Throwable): DomainException {
        return when (throwable) {
            is DomainException -> throwable
            else -> DomainException.UnknownException(throwable.message ?: "Unknown error", throwable)
        }
    }
}
