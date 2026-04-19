package com.example.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.example.core.base.mvi.BaseViewModel
import com.example.core.domain.result.DomainResult
import com.example.feature.auth.domain.usecase.CheckUserLoggedInUseCase
import com.example.feature.auth.domain.usecase.SignInEmailUseCase
import com.example.feature.auth.domain.usecase.SignInGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInEmailUseCase: SignInEmailUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase
) : BaseViewModel<LoginIntent, LoginState, LoginEffect>(LoginState()) {

    init {
        // Auto sign in if already logged in
        viewModelScope.launch {
            val result = checkUserLoggedInUseCase(Unit)
            if (result is DomainResult.Success && result.data) {
                updateState { copy(isSuccess = true) }
                sendEffect(LoginEffect.NavigateToMain)
            }
        }
    }

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignInWithGoogle -> signInGoogle(intent.idToken)
            is LoginIntent.EmailChanged -> updateState { copy(emailInput = intent.email, error = null) }
            is LoginIntent.PasswordChanged -> updateState { copy(passwordInput = intent.password, error = null) }
            is LoginIntent.SubmitEmailLogin -> signInEmail()
            is LoginIntent.GoToRegister -> sendEffect(LoginEffect.NavigateToRegister)
            is LoginIntent.GoToForgotPassword -> sendEffect(LoginEffect.NavigateToForgotPassword)
        }
    }

    private fun signInEmail() {
        val email = currentState.emailInput
        val password = currentState.passwordInput
        
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = signInEmailUseCase(SignInEmailUseCase.Params(email, password))
            if (result.isSuccess) {
                updateState { copy(isLoading = false, isSuccess = true) }
                sendEffect(LoginEffect.NavigateToMain)
            } else if (result is DomainResult.Error) {
                val errorMsg = result.exception.message ?: "Unknown error"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(LoginEffect.ShowToast(errorMsg))
            }
        }
    }

    private fun signInGoogle(idToken: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            val result = signInGoogleUseCase(SignInGoogleUseCase.Params(idToken))
            if (result.isSuccess) {
                updateState { copy(isLoading = false, isSuccess = true) }
                sendEffect(LoginEffect.NavigateToMain)
            } else if (result is DomainResult.Error) {
                val errorMsg = result.exception.message ?: "Unknown error"
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(LoginEffect.ShowToast(errorMsg))
            }
        }
    }
}
