package com.example.feature.auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.example.core.base.mvi.BaseViewModel
import com.example.core.domain.result.DomainResult
import com.example.feature.auth.domain.usecase.SignUpEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpEmailUseCase: SignUpEmailUseCase
) : BaseViewModel<RegisterIntent, RegisterState, RegisterEffect>(RegisterState()) {

    override fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EmailChanged -> updateState { copy(emailInput = intent.email, error = null) }
            is RegisterIntent.PasswordChanged -> updateState { copy(passwordInput = intent.password, error = null) }
            is RegisterIntent.SubmitRegister -> signUp()
            is RegisterIntent.GoBackToLogin -> sendEffect(RegisterEffect.NavigateBackToLogin)
        }
    }

    private fun signUp() {
        val email = currentState.emailInput
        val password = currentState.passwordInput

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = signUpEmailUseCase(SignUpEmailUseCase.Params(email, password))
            if (result.isSuccess) {
                updateState { copy(isLoading = false, isSuccess = true) }
                // After successful sign up, Firebase automatically logs the user in
                sendEffect(RegisterEffect.NavigateToMain)
            } else if (result is DomainResult.Error) {
                val errorMsg = result.exception.message ?: "Unknown error"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(RegisterEffect.ShowToast(errorMsg))
            }
        }
    }
}
