package com.example.feature.auth.domain.usecase

import com.example.core.base.usecase.UseCase
import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.core.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<SendPasswordResetEmailUseCase.Params, DomainResult<Unit>>() {

    data class Params(val email: String)

    override suspend fun execute(params: Params): DomainResult<Unit> {
        val trimmedEmail = params.email.trim()
        if (trimmedEmail.isBlank()) {
            return DomainResult.Error(DomainException.ValidationException("Email cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return DomainResult.Error(DomainException.ValidationException("Invalid email format"))
        }

        return authRepository.sendPasswordResetEmail(trimmedEmail)
    }
}
