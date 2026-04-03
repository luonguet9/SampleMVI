package com.example.feature.user.di

import com.example.feature.user.data.data_source.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	@Singleton
	@Provides
	fun provideUserApi(retrofit: Retrofit): UserApi {
		return retrofit.create(UserApi::class.java)
	}
}

