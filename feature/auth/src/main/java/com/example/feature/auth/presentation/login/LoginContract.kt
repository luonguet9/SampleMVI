package com.example.feature.auth.presentation.login

import com.example.core.base.mvi.MviEffect
import com.example.core.base.mvi.MviIntent
import com.example.core.base.mvi.MviState

sealed interface LoginIntent : MviIntent {
    data class SignInWithGoogle(val idToken: String) : LoginIntent
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    object SubmitEmailLogin : LoginIntent
    object GoToRegister : LoginIntent
    object GoToForgotPassword : LoginIntent
}

data class LoginState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) : MviState

sealed interface LoginEffect : MviEffect {
    object NavigateToMain : LoginEffect
    object NavigateToRegister : LoginEffect
    object NavigateToForgotPassword : LoginEffect
    data class ShowToast(val message: String) : LoginEffect
}
