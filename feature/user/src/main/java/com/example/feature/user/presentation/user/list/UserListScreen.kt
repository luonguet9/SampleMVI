package com.example.feature.user.presentation.user.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.domain.exception.DomainException
import com.example.core.ui.components.AppEmptyView
import com.example.core.ui.components.AppErrorView
import com.example.feature.user.R
import com.example.feature.user.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
	state: UserListState,
	onRefresh: () -> Unit,
	onSearch: (String) -> Unit,
	onClearSearch: () -> Unit,
	onItemClick: (Long) -> Unit,
	onDeleteUser: (Long) -> Unit,
	onRetry: () -> Unit
) {
	val pullToRefreshState = rememberPullToRefreshState()
	if (pullToRefreshState.isRefreshing) {
		LaunchedEffect(true) {
			onRefresh()
		}
	}
	LaunchedEffect(state.isRefreshing) {
		if (state.isRefreshing) {
			pullToRefreshState.startRefresh()
		} else {
			pullToRefreshState.endRefresh()
		}
	}
	
	Column(
		modifier = Modifier.fillMaxSize()
	) {
		OutlinedTextField(
			value = state.searchQuery,
			onValueChange = onSearch,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			placeholder = { Text(stringResource(R.string.search_users)) },
			leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
			trailingIcon = {
				if (state.isSearching) {
					CircularProgressIndicator(
						modifier = Modifier.size(20.dp),
						strokeWidth = 2.dp
					)
				} else if (state.searchQuery.isNotEmpty()) {
					IconButton(onClick = onClearSearch) {
						Icon(Icons.Default.Clear, contentDescription = "Clear search")
					}
				}
			},
			singleLine = true
		)
		
		Box(
			modifier = Modifier
				.weight(1f)
				.nestedScroll(pullToRefreshState.nestedScrollConnection)
		) {
			when {
				state.isLoading && !state.isRefreshing -> {
					CircularProgressIndicator(
						modifier = Modifier.align(Alignment.Center)
					)
				}
				
				state.hasError -> {
					Box(
						modifier = Modifier
							.fillMaxSize()
							.verticalScroll(rememberScrollState()),
						contentAlignment = Alignment.Center
					) {
						AppErrorView(
							message = state.error?.message
								?: stringResource(id = R.string.error_generic_message),
							icon = painterResource(id = R.drawable.ic_error),
							onRetry = onRetry
						)
					}
				}
				
				state.isEmpty -> {
					Box(
						modifier = Modifier
							.fillMaxSize()
							.verticalScroll(rememberScrollState()),
						contentAlignment = Alignment.Center
					) {
						AppEmptyView(
							title = stringResource(id = R.string.empty_user_list_title),
							message = stringResource(id = R.string.empty_user_list_message),
							icon = painterResource(id = R.drawable.ic_empty_users)
						)
					}
				}
				
				else -> {
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
						contentPadding = PaddingValues(bottom = 16.dp)
					) {
						items(items = state.displayUsers, key = { it.id }) { user ->
							UserItem(
								user = user,
								onItemClick = onItemClick,
								onDeleteClick = onDeleteUser
							)
						}
					}
				}
			}
			
			// Only show the indicator when the user is actively pulling or refreshing
			if (pullToRefreshState.progress > 0f || pullToRefreshState.isRefreshing) {
				PullToRefreshContainer(
					state = pullToRefreshState,
					modifier = Modifier.align(Alignment.TopCenter)
				)
			}
		}
	}
	
}


@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen1() {
	MaterialTheme {
		UserListScreen(
			state = UserListState(
				isLoading = false,
				users = listOf(
					User(
						1,
						"Mr A",
						"a@gmail.com",
						null,
						System.currentTimeMillis(),
						true
					),
					User(
						2,
						"Ms B",
						"b@gmail.com",
						null,
						System.currentTimeMillis() - 100000,
						false
					)
				)
			),
			onRefresh = {},
			onSearch = {},
			onClearSearch = {},
			onItemClick = {},
			onDeleteUser = {},
			onRetry = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen2() {
	MaterialTheme {
		UserListScreen(
			state = UserListState(
				isLoading = true,
				users = listOf()
			),
			onRefresh = {},
			onSearch = {},
			onClearSearch = {},
			onItemClick = {},
			onDeleteUser = {},
			onRetry = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen3() {
	MaterialTheme {
		UserListScreen(
			state = UserListState(
				isLoading = false,
				users = listOf()
			),
			onRefresh = {},
			onSearch = {},
			onClearSearch = {},
			onItemClick = {},
			onDeleteUser = {},
			onRetry = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen4() {
	MaterialTheme {
		UserListScreen(
			state = UserListState(
				error = DomainException.NetworkException("Network error"),
				users = listOf()
			),
			onRefresh = {},
			onSearch = {},
			onClearSearch = {},
			onItemClick = {},
			onDeleteUser = {},
			onRetry = {},
		)
	}
}
