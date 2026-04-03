package com.example.feature.user.domain.usecase

import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.model.User
import com.example.feature.user.domain.repository.UserRepository
import com.example.core.base.usecase.UseCase

import com.example.core.util.AppLogger

class SearchUserUseCase(
	private val userRepository: UserRepository
) : UseCase<SearchUserUseCase.Params, DomainResult<List<User>>>() {
	
	data class Params(val query: String)
	
	override suspend fun execute(params: Params): DomainResult<List<User>> {
		AppLogger.logUseCase(params)
		return if (params.query.isBlank()) {
			DomainResult.Error(DomainException.ValidationException("Search query cannot be empty"))
		} else {
			userRepository.searchUsers(params.query)
		}
	}
	
}
