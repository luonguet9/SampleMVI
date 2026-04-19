package com.example.feature.auth.presentation.forgot_password

import com.example.core.base.mvi.MviEffect
import com.example.core.base.mvi.MviIntent
import com.example.core.base.mvi.MviState

sealed interface ForgotPasswordIntent : MviIntent {
    data class EmailChanged(val email: String) : ForgotPasswordIntent
    object Submit : ForgotPasswordIntent
    object GoBackToLogin : ForgotPasswordIntent
}

data class ForgotPasswordState(
    val emailInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) : MviState

sealed interface ForgotPasswordEffect : MviEffect {
    object NavigateBackToLogin : ForgotPasswordEffect
    data class ShowToast(val message: String) : ForgotPasswordEffect
}
