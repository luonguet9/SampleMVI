package com.example.feature.user.domain.model

data class User(
	val id: Long,
	val username: String,
	val email: String,
	val avatarUrl: String?,
	val createdAt: Long,
	val isActive: Boolean
)


