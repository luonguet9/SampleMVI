package com.example.feature.auth.domain.usecase

import com.example.core.base.usecase.UseCase
import com.example.core.domain.repository.AuthRepository
import com.example.core.domain.result.DomainResult
import javax.inject.Inject

class SignInGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<SignInGoogleUseCase.Params, DomainResult<Unit>>() {

    data class Params(val idToken: String)

    override suspend fun execute(params: Params): DomainResult<Unit> {
        return authRepository.signInWithGoogle(params.idToken)
    }
}
