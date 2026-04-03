package com.example.feature.user.di

import com.example.feature.user.domain.repository.UserRepository
import com.example.feature.user.domain.usecase.DeleteUserUseCase
import com.example.feature.user.domain.usecase.GetUserByIdUseCase
import com.example.feature.user.domain.usecase.GetUsersUseCase
import com.example.feature.user.domain.usecase.SearchUserUseCase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
	
	@Provides
	@ViewModelScoped
	fun provideGetUsersUseCase(
		userRepository: UserRepository
	): GetUsersUseCase {
		return GetUsersUseCase(userRepository)
	}
	
	@Provides
	@ViewModelScoped
	fun provideSearchUserUseCase(
		userRepository: UserRepository
	): SearchUserUseCase {
		return SearchUserUseCase(userRepository)
	}
	
	@Provides
	@ViewModelScoped
	fun provideDeleteUserUseCase(
		userRepository: UserRepository
	): DeleteUserUseCase {
		return DeleteUserUseCase(userRepository)
	}
	
	@Provides
	@ViewModelScoped
	fun provideGetUserByIdUseCase(
		userRepository: UserRepository
	): GetUserByIdUseCase {
		return GetUserByIdUseCase(userRepository)
	}
}


