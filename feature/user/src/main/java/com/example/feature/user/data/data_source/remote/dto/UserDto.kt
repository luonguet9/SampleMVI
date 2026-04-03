package com.example.feature.user.data.data_source.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
	@SerializedName("id")
	val id: Long,
	@SerializedName("username")
	val username: String,
	@SerializedName("email")
	val email: String,
	@SerializedName("avatar_url")
	val avatarUrl: String?,
	@SerializedName("created_at")
	val createdAt: String,
	@SerializedName("is_active")
	val isActive: Boolean
)


