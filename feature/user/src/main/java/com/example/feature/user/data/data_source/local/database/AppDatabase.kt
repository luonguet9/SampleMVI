package com.example.feature.user.data.data_source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.feature.user.data.data_source.local.dao.UserDao
import com.example.feature.user.data.data_source.local.entity.UserEntity

@Database(
	entities = [UserEntity::class],
	version = 1,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
}


