package com.example.core.base.mvi

/**
 * Marker interface for all MVI States.
 *
 * HOW TO USE IN NEW PROJECT:
 * Implement as a data class so you can use 'copy()' in reducers:
 *
 * ```kotlin
 * data class LoginState(
 *     val isLoading: Boolean = false,
 *     val error: String? = null,
 *     val isLoggedIn: Boolean = false
 * ) : MviState
 * ```
 */
interface MviState
