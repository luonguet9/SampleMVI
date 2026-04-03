package com.example.feature.user.data.data_source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
	@PrimaryKey val id: Long,
	@ColumnInfo(name = "username") val username: String,
	@ColumnInfo(name = "email") val email: String,
	@ColumnInfo(name = "avatar_url") val avatarUrl: String?,
	@ColumnInfo(name = "created_at") val createdAt: Long,
	@ColumnInfo(name = "is_active") val isActive: Boolean,
	@ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)


