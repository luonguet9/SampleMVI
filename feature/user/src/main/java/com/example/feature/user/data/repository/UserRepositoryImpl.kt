package com.example.feature.user.data.repository

import com.example.feature.user.data.data_source.local.dao.UserDao
import com.example.feature.user.data.data_source.local.entity.UserEntity
import com.example.feature.user.data.data_source.remote.api.UserApi
import com.example.feature.user.data.data_source.remote.dto.UserDto
import com.example.feature.user.data.mapper.UserDtoMapper
import com.example.feature.user.data.mapper.UserEntityMapper
import com.example.feature.user.data.mapper.UserToEntityMapper
import com.example.core.network.NetworkConnectivity
import com.example.core.di.IoDispatcher
import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.model.User
import com.example.feature.user.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import com.example.core.util.AppLogger
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
	private val userApi: UserApi,
	private val userDao: UserDao,
	private val userDtoMapper: UserDtoMapper,
	private val userEntityMapper: UserEntityMapper,
	private val userToEntityMapper: UserToEntityMapper,
	private val networkConnectivity: NetworkConnectivity,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
	override fun getUsers(forceRefresh: Boolean): Flow<DomainResult<List<User>>> = flow {
		val cachedUsers = userDao.getUsers()
		val shouldFetchFromNetwork = forceRefresh || cachedUsers.isEmpty() || isCacheExpired(cachedUsers)
		
		if (!shouldFetchFromNetwork) {
			AppLogger.logCache("Serving ${cachedUsers.size} users from local DB")
			emit(DomainResult.Success(cachedUsers.map { userEntityMapper.map(it) }))
			return@flow
		}
		
		try {
			emit(DomainResult.Loading)
			AppLogger.logNetwork("Fetching users from API")
			val response = userApi.getUsers()
			val apiUsers = response.map { userDtoMapper.map(it) }
			val newEntities = apiUsers.map { userToEntityMapper.map(it) }
			
			userDao.clearAll()
			userDao.insertUsers(newEntities)
			
			val freshUsers = userDao.getUsers()
			AppLogger.logCache("Saved and retrieved ${freshUsers.size} fresh users to/from local DB")
			emit(DomainResult.Success(freshUsers.map { userEntityMapper.map(it) }))
			
		} catch (e: Exception) {
			AppLogger.logError("Failed to fetch users", e)
			emit(DomainResult.Error(mapException(e)))
			
			if (cachedUsers.isNotEmpty()) {
				AppLogger.logCache("Serving stale cache of ${cachedUsers.size} users as fallback")
				emit(DomainResult.Success(cachedUsers.map { userEntityMapper.map(it) }))
			}
		}
	}.flowOn(ioDispatcher)
	
	override fun getUserById(userId: Long): Flow<DomainResult<User>> = flow {
		emit(DomainResult.Loading)
		
		userDao.getUserById(userId)?.let { entity ->
			AppLogger.logCache("Serving user $userId from local DB")
			emit(DomainResult.Success(userEntityMapper.map(entity)))
			return@flow
		}
		
		try {
			AppLogger.logNetwork("Fetching user $userId from API")
			val response = userApi.getUserById(userId)
			val user = userDtoMapper.map(response)
			val entity = userToEntityMapper.map(user)
			userDao.insertUser(entity)
			emit(DomainResult.Success(user))
		} catch (e: Exception) {
			AppLogger.logError("Failed to fetch user $userId", e)
			emit(DomainResult.Error(mapException(e)))
		}
	}.flowOn(ioDispatcher)
	
	override suspend fun searchUsers(query: String): DomainResult<List<User>> =
		withContext(ioDispatcher) {
			try {
				AppLogger.logNetwork("Searching users from API for query: $query")
				val response = userApi.searchUsers(query)
				val users = response.users.map { entity -> userDtoMapper.map(entity) }
				DomainResult.Success(users)
			} catch (e: Exception) {
				val shouldFallbackToLocal = e is IOException ||
					(e is HttpException && e.code() == 404)

				if (shouldFallbackToLocal) {
					// Fallback to Room: offline mode OR API endpoint not found
					AppLogger.logCache("Search API unavailable, falling back to local DB for query: $query")
					try {
						val localResults = userDao.searchUser(query)
						AppLogger.logCache("Found ${localResults.size} users for '$query' in local DB")
						DomainResult.Success(localResults.map { userEntityMapper.map(it) })
					} catch (localDbException: Exception) {
						AppLogger.logError("Local DB search also failed", localDbException)
						DomainResult.Error(mapException(localDbException))
					}
				} else {
					AppLogger.logError("Failed to search users", e)
					DomainResult.Error(mapException(e))
				}
			}
		}
	
	override suspend fun updateUser(user: User): DomainResult<User> = withContext(ioDispatcher) {
		try {
			val dto = UserDto(
				id = user.id,
				username = user.username,
				email = user.email,
				avatarUrl = user.avatarUrl,
				createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
					.format(Date(user.createdAt)),
				isActive = user.isActive
			)
			AppLogger.logNetwork("Updating user ${user.id} to API")
			val updatedDto = userApi.updateUser(user.id, dto)
			val updatedUser = userDtoMapper.map(updatedDto)
			val entity = userToEntityMapper.map(updatedUser)
			
			userDao.updateUser(entity)
			DomainResult.Success(updatedUser)
		} catch (e: Exception) {
			DomainResult.Error(mapException(e))
			
		}
	}
	
	override suspend fun deleteUser(userId: Long): DomainResult<Unit> = withContext(ioDispatcher) {
		try {
			AppLogger.logNetwork("Deleting user $userId from API")
			val response = userApi.deleteUser(userId)
			if (response.isSuccessful) {
				userDao.deleteUser(userId)
				DomainResult.Success(Unit)
			} else {
				DomainResult.Error(DomainException.UnknownException("Failed to delete user"))
			}
		} catch (e: Exception) {
			DomainResult.Error(mapException(e))
		}
	}
	
	override fun observeUser(userId: Long): Flow<User?> {
		return userDao.observeUserById(userId)
			.map { user ->
				user?.let { entity ->
					userEntityMapper.map(entity)
				}
			}
			.flowOn(ioDispatcher)
	}
	
	private fun isCacheExpired(users: List<UserEntity>): Boolean {
		return users.firstOrNull()?.let {
			System.currentTimeMillis() - it.cachedAt > CACHE_EXPIRY_TIME
		} ?: true
	}
	
	private fun mapException(throwable: Throwable): DomainException {
		return when (throwable) {
			is DomainException -> throwable
			is IOException -> DomainException.NetworkException("Network error: ${throwable.message}")
			is HttpException -> when (throwable.code()) {
				401 -> DomainException.UnauthorizedException()
				404 -> DomainException.NotFoundException()
				else -> DomainException.NetworkException("HTTP error: ${throwable.code()}")
			}
			
			else -> DomainException.UnknownException(cause = throwable)
		}
	}
	
	companion object {
		private const val CACHE_EXPIRY_TIME = 5 * 60 * 1000L // 5 minutes
	}
}


