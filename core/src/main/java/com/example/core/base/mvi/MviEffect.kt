package com.example.core.base.mvi

/**
 * Marker interface for one-shot MVI Effects (side effects).
 *
 * Effects are consumed exactly once (via Channel) and are NOT replayed.
 * Use them for: navigation, toasts, dialogs, analytics events.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * sealed class LoginEffect : MviEffect {
 *     object NavigateToHome : LoginEffect()
 *     data class ShowError(val message: String) : LoginEffect()
 * }
 * ```
 */
interface MviEffect
