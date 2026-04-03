package com.example.feature.user.domain.usecase

import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.repository.UserRepository
import com.example.core.base.usecase.UseCase

import com.example.core.util.AppLogger

class DeleteUserUseCase(
	private val userRepository: UserRepository
) : UseCase<DeleteUserUseCase.Params, DomainResult<Unit>>() {
	
	data class Params(val userId: Long)
	
	override suspend fun execute(params: Params): DomainResult<Unit> {
		AppLogger.logUseCase(params)
		return userRepository.deleteUser(params.userId)
	}
}
