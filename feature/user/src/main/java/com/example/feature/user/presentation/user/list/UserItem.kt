package com.example.feature.user.presentation.user.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.feature.user.R
import com.example.feature.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserItem(
	user: User,
	modifier: Modifier = Modifier,
	onItemClick: (Long) -> Unit = {},
	onDeleteClick: (Long) -> Unit = {}
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		onClick = { onItemClick(user.id) },
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = RoundedCornerShape(8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.Top
		) {
			Box {
				AsyncImage(
					model = user.avatarUrl,
					contentDescription = "User Avatar",
					contentScale = ContentScale.Crop,
					placeholder = painterResource(R.drawable.ic_user_placeholder),
					error = painterResource(R.drawable.ic_user_placeholder),
					modifier = Modifier
						.size(48.dp)
						.clip(CircleShape)
						.background(Color.LightGray)
				)
				
				Box(
					modifier = Modifier
						.size(12.dp)
						.align(Alignment.BottomEnd)
						.clip(CircleShape)
						.background(if (user.isActive) Color.Green else Color.Gray)
				)
			}
			Spacer(modifier = Modifier.width(12.dp))
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = user.username,
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Text(
					text = user.email,
					fontSize = 14.sp,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = if (user.isActive) "Active" else "Inactive",
						fontSize = 12.sp,
						color = if (user.isActive) Color(0xFF00796B) else Color.DarkGray,
						modifier = Modifier
							.background(
								color = if (user.isActive) Color(0xFFE0F2F1) else Color(0xFFF5F5F5),
								shape = RoundedCornerShape(4.dp)
							)
							.padding(horizontal = 8.dp, vertical = 2.dp)
					)
					
					Spacer(modifier = Modifier.width(12.dp))
					
					val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
						.format(Date(user.createdAt))
					Text(
						text = dateFormatted,
						fontSize = 12.sp,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
				}
			}
			Spacer(modifier = Modifier.width(8.dp))
			// Nút Xoá
			IconButton(
				onClick = { onDeleteClick(user.id) },
				modifier = Modifier.size(40.dp)
			) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Delete User",
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewUserItem() {
	MaterialTheme {
		UserItem(
			user = User(
				id = 1L,
				username = "John Doe",
				email = "john.doe@example.com",
				avatarUrl = "https://i.pravatar.cc/150",
				createdAt = System.currentTimeMillis(),
				isActive = true
			)
		)
	}
}
