package com.example.feature.user.data.mapper

import com.example.core.base.mapper.Mapper
import com.example.feature.user.data.data_source.local.entity.UserEntity
import com.example.feature.user.domain.model.User
import javax.inject.Inject

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
