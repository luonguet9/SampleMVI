package com.example.feature.user.di

import android.content.Context
import androidx.room.Room
import com.example.feature.user.data.data_source.local.dao.UserDao
import com.example.feature.user.data.data_source.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	
	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"app_database"
		)
			.fallbackToDestructiveMigration()
			.build()
	}
	
	@Provides
	@Singleton
	fun provideUserDao(database: AppDatabase): UserDao {
		return database.userDao()
	}
}


