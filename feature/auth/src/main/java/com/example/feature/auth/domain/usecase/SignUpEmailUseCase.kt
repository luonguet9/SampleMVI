package com.example.feature.auth.domain.usecase

import com.example.core.base.usecase.UseCase
import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<SignUpEmailUseCase.Params, DomainResult<Unit>>() {

    data class Params(val email: String, val password: String)

    override suspend fun execute(params: Params): DomainResult<Unit> {
        if (params.email.isBlank()) {
            return DomainResult.Error(DomainException.ValidationException("Email cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(params.email).matches()) {
            return DomainResult.Error(DomainException.ValidationException("Invalid email format"))
        }
        if (params.password.length < 6) {
            return DomainResult.Error(DomainException.ValidationException("Password must be at least 6 characters"))
        }

        return authRepository.signUpWithEmail(params.email.trim(), params.password)
    }
}
