package com.example.feature.user.presentation.user.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.core.ui.components.AppErrorView
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.domain.exception.DomainException
import com.example.feature.user.R
import com.example.feature.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    state: UserDetailState,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Loading State
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                
                // 2. Error State
                state.hasError -> {
                    AppErrorView(
                        modifier = Modifier.align(Alignment.Center),
                        message = state.error?.message ?: "An unknown error occurred",
                        icon = painterResource(id = R.drawable.ic_error),
                        onRetry = onRetry
                    )
                }
                
                // 3. Success State
                state.user != null -> {
                    UserDetailSuccess(user = state.user)
                }
            }
        }
    }
}

@Composable
private fun UserDetailSuccess(user: User) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Section: Gradient Background and Overlapping Avatar ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Gradient Background Cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
            
            // Circular Avatar overlapping the bottom edge of the cover
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_user_placeholder),
                error = painterResource(R.drawable.ic_user_placeholder),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Name and Title Information ---
        Text(
            text = user.username,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Community Member", // Future placeholder for roles/tiers
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Detail Info Card ---
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                DetailRowItem(
                    icon = Icons.Default.Email,
                    title = "Email Address",
                    value = user.email
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(user.createdAt))
                DetailRowItem(
                    icon = Icons.Default.DateRange,
                    title = "Member Since",
                    value = dateFormatted
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                DetailRowItem(
                    icon = Icons.Default.Info,
                    title = "Account Status",
                    value = if (user.isActive) "Active" else "Inactive",
                    valueColor = if (user.isActive) Color(0xFF00796B) else MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // --- Future Action Buttons ---
        Button(
            onClick = { /* TODO: Edit Profile Action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Edit Profile", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { /* TODO: Logout Action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.error)
        }
    }
}

// Reusable Component for standardized detail rows
@Composable
private fun DetailRowItem(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailLoading() {
    MaterialTheme { UserDetailScreen(state = UserDetailState(isLoading = true), onNavigateBack = {}, onRetry = {}) }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailError() {
    MaterialTheme {
        UserDetailScreen(
            state = UserDetailState(
                error = DomainException.NetworkException("Connection Timeout")
            ),
            onNavigateBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailSuccess() {
    MaterialTheme {
        UserDetailScreen(
            state = UserDetailState(
                user = User(
                    id = 1L,
                    username = "John Doe",
                    email = "john.doe@example.com",
                    avatarUrl = "https://i.pravatar.cc/300",
                    createdAt = System.currentTimeMillis(),
                    isActive = true
                )
            ),
            onNavigateBack = {},
            onRetry = {}
        )
    }
}
