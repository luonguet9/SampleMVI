package com.example.feature.user.presentation.user

import androidx.lifecycle.viewModelScope
import com.example.core.domain.exception.DomainException
import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.usecase.DeleteUserUseCase
import com.example.feature.user.domain.usecase.GetUsersUseCase
import com.example.feature.user.domain.usecase.SearchUserUseCase
import com.example.core.base.mvi.BaseViewModel
import com.example.feature.user.presentation.user.list.UserListEffect
import com.example.feature.user.presentation.user.list.UserListIntent
import com.example.feature.user.presentation.user.list.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
	private val getUsersUseCase: GetUsersUseCase,
	private val searchUserUseCase: SearchUserUseCase,
	private val deleteUserUseCase: DeleteUserUseCase
) : BaseViewModel<UserListIntent, UserListState, UserListEffect>(UserListState()) {
	
	private val searchDebouncer = MutableSharedFlow<String>(replay = 1)
	
	init {
		setupSearchDebouncer()
	}
	
	override fun handleIntent(intent: UserListIntent) {
		when (intent) {
			is UserListIntent.LoadUsers -> loadUsers(forceRefresh = false)
			is UserListIntent.RefreshUsers -> loadUsers(forceRefresh = true)
			is UserListIntent.SearchUsers -> searchUsers(intent.query)
			is UserListIntent.ClearSearch -> clearSearch()
			is UserListIntent.SelectUser -> navigateToUserDetail(intent.userId)
			is UserListIntent.UpdateUser -> navigateToUpdateUser(intent.userId)
			is UserListIntent.DeleteUser -> showDeleteConfirmation(intent.userId)
			is UserListIntent.ConfirmDelete -> confirmDelete(intent.userId)
			is UserListIntent.RetryLoad -> loadUsers(forceRefresh = true)
		}
	}
	
	private fun loadUsers(forceRefresh: Boolean) {
		getUsersUseCase(GetUsersUseCase.Params(forceRefresh))
			.onEach { result ->
				when (result) {
					is DomainResult.Loading -> {
						updateState {
							copy(
								isLoading = !forceRefresh,
								isRefreshing = forceRefresh,
								error = null
							)
						}
					}
					
					is DomainResult.Success -> {
						updateState {
							copy(
								isLoading = false,
								isRefreshing = false,
								users = result.data,
								error = null
							)
						}
					}
					
					is DomainResult.Error -> {
						updateState {
							copy(
								isLoading = false,
								isRefreshing = false,
								error = result.exception
							)
						}
						if (result.exception is DomainException.NetworkException) {
							sendEffect(UserListEffect.ShowNetworkError)
						}
					}
				}
			}
			.launchIn()
	}
	
	private fun searchUsers(query: String) {
		updateState {
			copy(
				searchQuery = query,
				isSearching = query.isNotEmpty()
			)
		}
		viewModelScope.launch {
			searchDebouncer.emit(query)
		}
	}
	
	private fun clearSearch() {
		updateState {
			copy(
				searchQuery = "",
				filteredUsers = emptyList(),
				isSearching = false
			)
		}
	}
	
	private fun navigateToUserDetail(userId: Long) {
		sendEffect(UserListEffect.NavigateToUserDetail(userId))
	}
	
	private fun navigateToUpdateUser(userId: Long) {
		sendEffect(UserListEffect.NavigateToUserDetail(userId))
	}
	
	private fun showDeleteConfirmation(userId: Long) {
		val user = currentState.displayUsers.find {
			it.id == userId
		}
		user?.let {
			sendEffect(UserListEffect.ShowDeleteConfirmation(userId, it.username))
		}
	}
	
	private fun confirmDelete(userId: Long) {
		viewModelScope.launch {
			when (val result = deleteUserUseCase(DeleteUserUseCase.Params(userId))) {
				is DomainResult.Success -> {
					sendEffect(UserListEffect.ShowToast("User deleted successfully"))
					loadUsers(forceRefresh = true)
				}
				is DomainResult.Error -> {
					sendEffect(UserListEffect.ShowToast("Failed to delete user: ${result.exception.message}"))
				}
				is DomainResult.Loading -> { /* No-op */ }
			}
		}
	}
	
	private fun setupSearchDebouncer() {
		searchDebouncer
			.debounce(300)
			.distinctUntilChanged()
			.onEach { query ->
				if (query.isEmpty()) {
					updateState { copy(filteredUsers = emptyList(), isSearching = false) }
					return@onEach
				}
				
				updateState { copy(isSearching = true) }
				
				when (val result = searchUserUseCase(SearchUserUseCase.Params(query))) {
					is DomainResult.Success -> {
						updateState {
							copy(
								filteredUsers = result.data,
								isSearching = false
							)
						}
					}
					
					is DomainResult.Error -> {
						updateState { copy(isSearching = false) }
						sendEffect(UserListEffect.ShowToast("Search failed: ${result.exception.message}"))
					}
					
					is DomainResult.Loading -> { /* No-op */
					}
				}
			}
			.launchIn()
	}
	
}
