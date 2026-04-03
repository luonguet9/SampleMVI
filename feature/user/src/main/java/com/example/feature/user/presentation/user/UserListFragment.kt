package com.example.feature.user.presentation.user

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.user.R
import com.example.feature.user.databinding.FragmentUserListBinding
import com.example.core.domain.exception.DomainException
import com.example.feature.user.domain.model.User
import com.example.core.base.mvi.BaseFragment
import com.example.feature.user.presentation.user.list.UserListEffect
import com.example.feature.user.presentation.user.list.UserListIntent
import com.example.feature.user.presentation.user.list.UserListState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment :
	BaseFragment<FragmentUserListBinding, UserListIntent, UserListState, UserListEffect, UserListViewModel>() {
	override val viewModel: UserListViewModel by viewModels()
	
	
	private val userAdapter by lazy {
		UserAdapter(
			onUserClick = { user -> viewModel.processIntent(UserListIntent.SelectUser(user.id)) },
			onDeleteClick = { user -> viewModel.processIntent(UserListIntent.DeleteUser(user.id)) }
		)
	}
	
	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentUserListBinding {
		return FragmentUserListBinding.inflate(inflater, container, false)
	}
	
	override fun setupViews(savedInstanceState: Bundle?) {
		setupRecyclerView()
		setupSearchView()
		setupSwipeRefresh()
		setupToolbar()
		
		// Load users on first launch
		if (savedInstanceState == null) {
			viewModel.processIntent(UserListIntent.LoadUsers)
		}
	}
	
	private fun setupRecyclerView() {
		binding.recyclerViewUsers.apply {
			layoutManager = LinearLayoutManager(context)
			adapter = userAdapter
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			
			// Item animation
			itemAnimator = DefaultItemAnimator().apply {
				addDuration = 200
				removeDuration = 200
			}
		}
	}
	
	private fun setupSearchView() {
		binding.searchView.apply {
			addTextChangedListener(object : TextWatcher {
				override fun beforeTextChanged(
					s: CharSequence?,
					start: Int,
					count: Int,
					after: Int
				) {
				}
				
				override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
				override fun afterTextChanged(s: Editable?) {
					viewModel.processIntent(UserListIntent.SearchUsers(s?.toString() ?: ""))
				}
			})
			
			setOnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideKeyboard()
					true
				} else {
					false
				}
			}
		}
		
		binding.buttonClearSearch.setOnClickListener {
			binding.searchView.text?.clear()
			viewModel.processIntent(UserListIntent.ClearSearch)
			hideKeyboard()
		}
	}
	
	private fun setupSwipeRefresh() {
		binding.swipeRefreshLayout.apply {
			setColorSchemeResources(
				R.color.colorPrimary,
				R.color.colorAccent
			)
			setOnRefreshListener {
				viewModel.processIntent(UserListIntent.RefreshUsers)
			}
		}
	}
	
	private fun setupToolbar() {
		binding.toolbar.apply {
			title = getString(R.string.user_list_title)
			inflateMenu(R.menu.menu_user_list)
			setOnMenuItemClickListener { item ->
				when (item.itemId) {
					R.id.action_refresh -> {
						viewModel.processIntent(UserListIntent.RefreshUsers)
						true
					}
					
					else -> false
				}
			}
		}
	}
	
	override fun renderState(state: UserListState) {
		// Update loading states
		binding.progressBar.isVisible = state.isLoading && !state.isRefreshing
		binding.swipeRefreshLayout.isRefreshing = state.isRefreshing
		binding.progressBarSearch.isVisible = state.isSearching
		
		// Update search UI
		binding.buttonClearSearch.isVisible = state.searchQuery.isNotEmpty()
		
		// Update content
		when {
			state.hasError -> showErrorState(state.error!!)
			state.isEmpty -> showEmptyState()
			else -> showContentState(state.displayUsers)
		}
		
		// Update result count
		binding.textResultCount.apply {
			isVisible = state.searchQuery.isNotEmpty()
			text = getString(R.string.search_results_count, state.filteredUsers.size)
		}
	}
	
	private fun showContentState(users: List<User>) {
		binding.recyclerViewUsers.isVisible = true
		binding.layoutEmpty.root.isVisible = false
		binding.layoutError.root.isVisible = false
		
		userAdapter.submitList(users)
	}
	
	private fun showEmptyState() {
		binding.recyclerViewUsers.isVisible = false
		binding.layoutEmpty.root.isVisible = true
		binding.layoutError.root.isVisible = false
		
		binding.layoutEmpty.apply {
			textEmptyTitle.text = getString(R.string.empty_user_list_title)
			textEmptyMessage.text = getString(R.string.empty_user_list_message)
			imageEmpty.setImageResource(R.drawable.ic_empty_users)
		}
	}
	
	private fun showErrorState(error: DomainException) {
		binding.recyclerViewUsers.isVisible = false
		binding.layoutEmpty.root.isVisible = false
		binding.layoutError.root.isVisible = true
		
		binding.layoutError.apply {
			textErrorTitle.text = when (error) {
				is DomainException.NetworkException -> getString(R.string.error_network_title)
				is DomainException.UnauthorizedException -> getString(R.string.error_unauthorized_title)
				else -> getString(R.string.error_generic_title)
			}
			
			textErrorMessage.text = error.message ?: getString(R.string.error_generic_message)
			imageError.setImageResource(getErrorIcon(error))
			
			buttonRetry.setOnClickListener {
				viewModel.processIntent(UserListIntent.RetryLoad)
			}
		}
	}
	
	private fun getErrorIcon(error: DomainException): Int {
		return when (error) {
			is DomainException.NetworkException -> R.drawable.ic_no_connection
			is DomainException.UnauthorizedException -> R.drawable.ic_lock
			else -> R.drawable.ic_error
		}
	}
	
	override fun handleEffect(effect: UserListEffect) {
		when (effect) {
			is UserListEffect.ShowToast -> {
				Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
			}
			
			is UserListEffect.NavigateToUserDetail -> {
				findNavController().navigate(
					UserListFragmentDirections.actionUserListToUserDetail(effect.userId)
				)
			}
			
			is UserListEffect.ShowDeleteConfirmation -> {
				showDeleteConfirmationDialog(effect.userId, effect.username)
			}
			
			is UserListEffect.ShowNetworkError -> {
				Snackbar.make(
					binding.root,
					R.string.error_network_message,
					Snackbar.LENGTH_LONG
				).setAction(R.string.retry) {
					viewModel.processIntent(UserListIntent.RetryLoad)
				}.show()
			}
		}
	}
	
	private fun showDeleteConfirmationDialog(userId: Long, username: String) {
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(R.string.delete_user_title)
			.setMessage(getString(R.string.delete_user_message, username))
			.setPositiveButton(R.string.delete) { _, _ ->
				viewModel.processIntent(UserListIntent.ConfirmDelete(userId))
			}
			.setNegativeButton(R.string.cancel, null)
			.show()
	}
	
	private fun hideKeyboard() {
		val imm =
			requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
	}
}

