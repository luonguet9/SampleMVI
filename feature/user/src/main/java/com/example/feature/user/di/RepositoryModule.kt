package com.example.feature.user.di

import com.example.feature.user.data.repository.UserRepositoryImpl
import com.example.feature.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
	
	@Binds
	@Singleton
	abstract fun bindUserRepository(
		userRepositoryImpl: UserRepositoryImpl
	): UserRepository
}


