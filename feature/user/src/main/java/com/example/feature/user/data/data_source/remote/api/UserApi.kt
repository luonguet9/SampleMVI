package com.example.feature.user.data.data_source.remote.api

import com.example.feature.user.data.data_source.remote.dto.UserDto
import com.example.feature.user.data.data_source.remote.dto.UserListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
	@GET("users")
	suspend fun getUsers(
		@Query("page") page: Int = 1,
		@Query("limit") limit: Int = 20
	): List<UserDto>
	
	@GET("users/{id}")
	suspend fun getUserById(@Path("id") userId: Long): UserDto
	
	@GET("users/search")
	suspend fun searchUsers(@Query("q") query: String): UserListResponse
	
	@PUT("users/{id}")
	suspend fun updateUser(@Path("id") userId: Long, @Body user: UserDto): UserDto
	
	@DELETE("users/{id}")
	suspend fun deleteUser(@Path("id") userId: Long): Response<Unit>
}


