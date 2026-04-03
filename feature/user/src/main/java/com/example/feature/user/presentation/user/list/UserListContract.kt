package com.example.feature.user.presentation.user.list

import com.example.core.domain.exception.DomainException
import com.example.feature.user.domain.model.User
import com.example.core.base.mvi.MviEffect
import com.example.core.base.mvi.MviIntent
import com.example.core.base.mvi.MviState

sealed class UserListIntent: MviIntent {
	object LoadUsers : UserListIntent()
	object RefreshUsers : UserListIntent()
	data class SearchUsers(val query: String) : UserListIntent()
	object ClearSearch : UserListIntent()
	data class SelectUser(val userId: Long) : UserListIntent()
	data class UpdateUser(val userId: Long) : UserListIntent()
	data class DeleteUser(val userId: Long) : UserListIntent()
	data class ConfirmDelete(val userId: Long) : UserListIntent()
	object RetryLoad : UserListIntent()
}

data class UserListState(
	val isLoading: Boolean = false,
	val isRefreshing: Boolean = false,
	val users: List<User> = emptyList(),
	val filteredUsers: List<User> = emptyList(),
	val searchQuery: String = "",
	val error: DomainException? = null,
	val isSearching: Boolean = false
) : MviState {
	val isEmpty: Boolean get() = users.isEmpty() && !isLoading
	val hasError: Boolean get() = error != null && users.isEmpty()
	val displayUsers: List<User> get() = if (searchQuery.isNotEmpty()) filteredUsers else users
}

sealed class UserListEffect : MviEffect {
	data class ShowToast(val message: String) : UserListEffect()
	data class NavigateToUserDetail(val userId: Long) : UserListEffect()
	data class ShowDeleteConfirmation(val userId: Long, val username: String) : UserListEffect()
	object ShowNetworkError : UserListEffect()
}


