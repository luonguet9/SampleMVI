package com.example.feature.user.domain.repository

import com.example.feature.user.domain.model.User
import com.example.core.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
	fun getUsers(forceRefresh: Boolean = false): Flow<DomainResult<List<User>>>
	fun getUserById(userId: Long): Flow<DomainResult<User>>
	suspend fun searchUsers(query: String): DomainResult<List<User>>
	suspend fun updateUser(user: User): DomainResult<User>
	suspend fun deleteUser(userId: Long): DomainResult<Unit>
	fun observeUser(userId: Long): Flow<User?>
}


