package com.example.samplemvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import com.example.core.ui.theme.SampleMVITheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.core.ui.LocalSnackbarHostState
import com.example.feature.user.presentation.user.userGraph
import com.example.feature.auth.presentation.AuthRoutes
import com.example.feature.auth.presentation.authGraph
import com.example.feature.chat.presentation.ChatRoutes
import com.example.feature.chat.presentation.chatGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                SampleMVITheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val navController = rememberNavController()
                            NavHost(
                                navController = navController,
                                startDestination = AuthRoutes.AUTH_GRAPH // Start destination
                            ) {
                                authGraph(
                                    navController = navController,
                                    onLoginSuccess = {
                                        navController.navigate(ChatRoutes.CHAT_GRAPH) {
                                            popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                                        }
                                    }
                                )
                                chatGraph(navController)
                                userGraph(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
