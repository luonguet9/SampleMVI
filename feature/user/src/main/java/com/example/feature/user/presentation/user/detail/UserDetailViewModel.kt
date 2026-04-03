package com.example.feature.user.presentation.user.detail

import com.example.core.domain.result.DomainResult
import com.example.feature.user.domain.usecase.GetUserByIdUseCase
import com.example.core.base.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
	private val getUserByIdUseCase: GetUserByIdUseCase
) : BaseViewModel<UserDetailIntent, UserDetailState, UserDetailEffect>(
	UserDetailState()
) {
	
	private var currentUserId: Long = -1
	
	override fun handleIntent(intent: UserDetailIntent) {
		when (intent) {
			is UserDetailIntent.LoadUser -> {
				currentUserId = intent.userId
				loadUser(intent.userId)
			}
			
			UserDetailIntent.Retry -> {
				if (currentUserId != -1L) {
					loadUser(currentUserId)
				}
			}
		}
	}
	
	private fun loadUser(userId: Long) {
		getUserByIdUseCase(GetUserByIdUseCase.Params(userId))
			.onEach { result ->
				when (result) {
					is DomainResult.Loading -> {
						updateState { copy(isLoading = true, error = null) }
					}
					
					is DomainResult.Success -> {
						updateState {
							copy(
								isLoading = false,
								user = result.data,
								error = null
							)
						}
					}
					
					is DomainResult.Error -> {
						updateState {
							copy(
								isLoading = false,
								error = result.exception
							)
						}
					}
				}
			}
			.launchIn()
	}
}
