package com.example.feature.user.presentation.user.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.feature.user.R
import com.example.feature.user.databinding.FragmentUserDetailBinding
import com.example.core.domain.exception.DomainException
import com.example.feature.user.domain.model.User
import com.example.core.base.mvi.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailFragment :
	BaseFragment<FragmentUserDetailBinding, UserDetailIntent, UserDetailState, UserDetailEffect, UserDetailViewModel>() {
	
	override val viewModel: UserDetailViewModel by viewModels()
	private val args: UserDetailFragmentArgs by navArgs()
	
	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	) = FragmentUserDetailBinding.inflate(inflater, container, false)
	
	override fun setupViews(savedInstanceState: Bundle?) {
		if (savedInstanceState == null) {
			viewModel.processIntent(UserDetailIntent.LoadUser(args.userId))
		}
		
		binding.buttonRetry.setOnClickListener {
			viewModel.processIntent(UserDetailIntent.Retry)
		}
	}
	
	override fun renderState(state: UserDetailState) {
		binding.progressBar.isVisible = state.isLoading
		
		when {
			state.hasError -> showError(state.error!!)
			state.user != null -> showUser(state.user)
		}
	}
	
	override fun handleEffect(effect: UserDetailEffect) {
		// hiện tại chưa cần
	}
	
	private fun showUser(user: User) {
		binding.layoutContent.isVisible = true
		binding.layoutError.root.isVisible = false
		
		binding.apply {
			textUsername.text = user.username
			textEmail.text = user.email
			textStatus.text = if (user.isActive) "Active" else "Inactive"
			
			Glide.with(imageAvatar)
				.load(user.avatarUrl)
				.placeholder(R.drawable.ic_user_placeholder)
				.circleCrop()
				.into(imageAvatar)
		}
	}
	
	private fun showError(error: DomainException) {
		binding.layoutContent.isVisible = false
		binding.layoutError.root.isVisible = true
		
		binding.layoutError.textErrorTitle.text = "Error"
		binding.layoutError.textErrorMessage.text = error.message
	}
}
