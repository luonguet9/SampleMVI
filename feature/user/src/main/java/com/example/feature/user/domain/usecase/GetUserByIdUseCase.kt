package com.example.feature.user.domain.usecase

import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.model.User
import com.example.feature.user.domain.repository.UserRepository
import com.example.core.base.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

import com.example.core.util.AppLogger

class GetUserByIdUseCase(
	private val userRepository: UserRepository
) : FlowUseCase<GetUserByIdUseCase.Params, DomainResult<User>>() {
	
	data class Params(val userId: Long)
	
	override fun execute(params: Params): Flow<DomainResult<User>> {
		AppLogger.logUseCase(params)
		return userRepository.getUserById(params.userId)
	}
}
