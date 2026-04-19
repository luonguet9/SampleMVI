package com.example.feature.auth.domain.usecase

import com.example.core.base.usecase.UseCase
import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignInEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<SignInEmailUseCase.Params, DomainResult<Unit>>() {

    data class Params(val email: String, val password: String)

    override suspend fun execute(params: Params): DomainResult<Unit> {
        if (params.email.isBlank()) {
            return DomainResult.Error(DomainException.ValidationException("Email cannot be empty"))
        }
        if (params.password.isBlank()) {
            return DomainResult.Error(DomainException.ValidationException("Password cannot be empty"))
        }

        return authRepository.signInWithEmail(params.email.trim(), params.password)
    }
}
