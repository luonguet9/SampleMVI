package com.example.feature.user.presentation.user.detail

import com.example.core.domain.exception.DomainException
import com.example.feature.user.domain.model.User
import com.example.core.base.mvi.MviEffect
import com.example.core.base.mvi.MviIntent
import com.example.core.base.mvi.MviState

sealed class UserDetailIntent : MviIntent {
	data class LoadUser(val userId: Long) : UserDetailIntent()
	object Retry : UserDetailIntent()
}

data class UserDetailState(
	val isLoading: Boolean = false,
	val user: User? = null,
	val error: DomainException? = null
) : MviState {
	val hasError: Boolean get() = error != null
}

sealed class UserDetailEffect : MviEffect {
	data class ShowToast(val message: String) : UserDetailEffect()
}
