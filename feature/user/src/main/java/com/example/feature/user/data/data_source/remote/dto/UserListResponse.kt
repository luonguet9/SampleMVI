package com.example.feature.user.data.data_source.remote.dto

import com.google.gson.annotations.SerializedName

data class UserListResponse(
	@SerializedName("data")
	val users: List<UserDto>,
	@SerializedName("total")
	val total: Int,
)


