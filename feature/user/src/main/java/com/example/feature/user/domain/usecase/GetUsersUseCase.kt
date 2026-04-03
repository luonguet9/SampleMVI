package com.example.feature.user.domain.usecase

import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.model.User
import com.example.feature.user.domain.repository.UserRepository
import com.example.core.base.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

import com.example.core.util.AppLogger

class GetUsersUseCase(
	private val userRepository: UserRepository
) : FlowUseCase<GetUsersUseCase.Params, DomainResult<List<User>>>() {
	
	data class Params(val forceRefresh: Boolean = false)
	
	override fun execute(params: Params): Flow<DomainResult<List<User>>> {
		AppLogger.logUseCase(params)
		return userRepository.getUsers(params.forceRefresh)
	}
}
