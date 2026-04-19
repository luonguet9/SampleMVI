package com.example.feature.auth.presentation.register

import com.example.core.base.mvi.MviEffect
import com.example.core.base.mvi.MviIntent
import com.example.core.base.mvi.MviState

sealed interface RegisterIntent : MviIntent {
    data class EmailChanged(val email: String) : RegisterIntent
    data class PasswordChanged(val password: String) : RegisterIntent
    object SubmitRegister : RegisterIntent
    object GoBackToLogin : RegisterIntent
}

data class RegisterState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) : MviState

sealed interface RegisterEffect : MviEffect {
    object NavigateBackToLogin : RegisterEffect
    object NavigateToMain : RegisterEffect
    data class ShowToast(val message: String) : RegisterEffect
}
