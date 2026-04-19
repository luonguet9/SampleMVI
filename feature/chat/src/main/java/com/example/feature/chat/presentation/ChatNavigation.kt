package com.example.feature.chat.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.feature.chat.presentation.chat.ChatScreen

object ChatRoutes {
    const val CHAT_GRAPH = "chat_graph"
    const val CHAT_MAIN = "chat_main"
}

fun NavGraphBuilder.chatGraph(navController: NavController) {
    navigation(
        startDestination = ChatRoutes.CHAT_MAIN,
        route = ChatRoutes.CHAT_GRAPH
    ) {
        composable(ChatRoutes.CHAT_MAIN) {
            ChatScreen()
        }
    }
}
