package com.example.feature.auth.domain.usecase

import com.example.core.base.usecase.UseCase
import com.example.core.domain.repository.AuthRepository
import com.example.core.domain.result.DomainResult
import javax.inject.Inject

class CheckUserLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<Unit, DomainResult<Boolean>>() {

    override suspend fun execute(params: Unit): DomainResult<Boolean> {
        val isLoggedIn = authRepository.getCurrentUserId() != null
        return DomainResult.Success(isLoggedIn)
    }
}
