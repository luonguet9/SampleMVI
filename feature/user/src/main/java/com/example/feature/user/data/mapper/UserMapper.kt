package com.example.feature.user.data.mapper

import com.example.feature.user.data.data_source.local.entity.UserEntity
import com.example.feature.user.data.data_source.remote.dto.UserDto
import com.example.core.base.mapper.Mapper
import com.example.feature.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class UserDtoMapper @Inject constructor() : Mapper<UserDto, User> {
	override fun map(input: UserDto): User {
		return User(
			id = input.id,
			username = input.username,
			email = input.email,
			avatarUrl = input.avatarUrl,
			createdAt = parseTimestamp(input.createdAt),
			isActive = input.isActive
		)
	}
	
	private fun parseTimestamp(timestamp: String): Long {
		return try {
			SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
				.apply { timeZone = TimeZone.getTimeZone("UTC") }
				.parse(timestamp)?.time ?: 0L
		} catch (e: Exception) {
			0L
		}
	}
}

class UserEntityMapper @Inject constructor() : Mapper<UserEntity, User> {
	override fun map(input: UserEntity): User {
		return User(
			id = input.id,
			username = input.username,
			email = input.email,
			avatarUrl = input.avatarUrl,
			createdAt = input.createdAt,
			isActive = input.isActive
		)
	}
}

class UserToEntityMapper @Inject constructor() : Mapper<User, UserEntity> {
	override fun map(input: User): UserEntity {
		return UserEntity(
			id = input.id,
			username = input.username,
			email = input.email,
			avatarUrl = input.avatarUrl,
			createdAt = input.createdAt,
			isActive = input.isActive
		)
	}
}
