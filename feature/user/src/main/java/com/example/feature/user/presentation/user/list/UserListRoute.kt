package com.example.feature.user.presentation.user.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.ui.LocalSnackbarHostState
import com.example.feature.user.presentation.user.UserListViewModel
import kotlinx.coroutines.launch

@Composable
fun UserListRoute(
    onNavigateToUserDetail: (Long) -> Unit,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    // Dispatch initial load when the screen first appears
    LaunchedEffect(Unit) {
        viewModel.processIntent(UserListIntent.LoadUsers)
    }

    // Listen for side effects from ViewModel (e.g., Navigation, Toast)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UserListEffect.ShowToast -> {
                    // TODO: Handle Toast if needed
                }
                is UserListEffect.NavigateToUserDetail -> {
                    onNavigateToUserDetail(effect.userId)
                }
                is UserListEffect.ShowDeleteConfirmation -> {
                    // TODO: Show delete confirmation dialog
                }
                is UserListEffect.ShowNetworkError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Network error! Please check your connection.")
                    }
                }
            }
        }
    }

    UserListScreen(
        state = state,
        onRefresh = { viewModel.processIntent(UserListIntent.RefreshUsers) },
        onSearch = { query -> viewModel.processIntent(UserListIntent.SearchUsers(query)) },
        onClearSearch = { viewModel.processIntent(UserListIntent.ClearSearch) },
        onItemClick = { userId -> viewModel.processIntent(UserListIntent.SelectUser(userId)) },
        onDeleteUser = { userId -> viewModel.processIntent(UserListIntent.DeleteUser(userId)) },
        onRetry = { viewModel.processIntent(UserListIntent.RetryLoad) }
    )
}
