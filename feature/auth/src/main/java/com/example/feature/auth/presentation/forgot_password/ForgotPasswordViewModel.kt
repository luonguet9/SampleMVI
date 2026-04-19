package com.example.feature.auth.presentation.forgot_password

import androidx.lifecycle.viewModelScope
import com.example.core.base.mvi.BaseViewModel
import com.example.core.domain.result.DomainResult
import com.example.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : BaseViewModel<ForgotPasswordIntent, ForgotPasswordState, ForgotPasswordEffect>(ForgotPasswordState()) {

    override fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> updateState { copy(emailInput = intent.email, error = null) }
            is ForgotPasswordIntent.Submit -> sendResetEmail()
            is ForgotPasswordIntent.GoBackToLogin -> sendEffect(ForgotPasswordEffect.NavigateBackToLogin)
        }
    }

    private fun sendResetEmail() {
        val email = currentState.emailInput
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = sendPasswordResetEmailUseCase(SendPasswordResetEmailUseCase.Params(email))
            
            if (result.isSuccess) {
                updateState { copy(isLoading = false, isSuccess = true) }
                sendEffect(ForgotPasswordEffect.ShowToast("Password reset email sent! Check your inbox."))
                sendEffect(ForgotPasswordEffect.NavigateBackToLogin)
            } else if (result is DomainResult.Error) {
                val errorMsg = result.exception.message ?: "Unknown error"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(ForgotPasswordEffect.ShowToast(errorMsg))
            }
        }
    }
}
