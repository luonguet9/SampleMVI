package com.example.feature.user.presentation.user.detail

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserDetailRoute(
    userId: Long,
    onNavigateBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Dispatch Intent to load data when the screen is initialized
    LaunchedEffect(userId) {
        viewModel.processIntent(UserDetailIntent.LoadUser(userId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UserDetailEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    UserDetailScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onRetry = { viewModel.processIntent(UserDetailIntent.Retry) }
    )
}
