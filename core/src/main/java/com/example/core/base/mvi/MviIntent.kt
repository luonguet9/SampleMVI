package com.example.core.base.mvi

/**
 * Marker interface for all MVI Intents.
 *
 * HOW TO USE IN NEW PROJECT:
 * Implement this in a sealed class for each feature screen:
 *
 * ```kotlin
 * sealed class LoginIntent : MviIntent {
 *     data class Submit(val email: String, val password: String) : LoginIntent()
 *     object ForgotPassword : LoginIntent()
 * }
 * ```
 */
interface MviIntent
