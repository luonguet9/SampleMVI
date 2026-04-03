package com.example.feature.user.presentation.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feature.user.R
import com.example.feature.user.databinding.ItemUserBinding
import com.example.feature.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserAdapter(
	private val onUserClick: (User) -> Unit,
	private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
		val binding = ItemUserBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return UserViewHolder(binding, onUserClick, onDeleteClick)
	}
	
	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		holder.bind(getItem(position))
	}
	
	class UserViewHolder(
		private val binding: ItemUserBinding,
		private val onUserClick: (User) -> Unit,
		private val onDeleteClick: (User) -> Unit
	) : RecyclerView.ViewHolder(binding.root) {
		
		fun bind(user: User) {
			binding.apply {
				textUsername.text = user.username
				textEmail.text = user.email
				
				// Load avatar
				Glide.with(imageAvatar)
					.load(user.avatarUrl)
					.placeholder(R.drawable.ic_user_placeholder)
					.circleCrop()
					.into(imageAvatar)
				
				// Status indicator
				viewStatusIndicator.isVisible = user.isActive
				textStatus.apply {
					isVisible = true
					text = if (user.isActive) "Active" else "Inactive"
					setTextColor(
						ContextCompat.getColor(
							context,
							if (user.isActive) R.color.status_active else R.color.status_inactive
						)
					)
				}
				
				// Date formatting
				textCreatedAt.text = formatDate(user.createdAt)
				
				// Click listeners
				root.setOnClickListener { onUserClick(user) }
				buttonDelete.setOnClickListener { onDeleteClick(user) }
				
				// Ripple effect
				root.isClickable = true
				root.isFocusable = true
			}
		}
		
		private fun formatDate(timestamp: Long): String {
			val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
			return sdf.format(Date(timestamp))
		}
	}
	
	private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
		override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
			return oldItem == newItem
		}
	}
}
