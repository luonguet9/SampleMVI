package com.example.feature.user.presentation.user

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.user.presentation.user.detail.UserDetailRoute
import com.example.feature.user.presentation.user.list.UserListRoute

/**
 * Define Navigation Routes
 */
object UserRoutes {
    const val USER_LIST = "user_list"
    const val USER_DETAIL = "user_detail/{userId}"

    fun createDetailRoute(userId: Long): String {
        return "user_detail/$userId"
    }
}

/**
 * Encapsulate the entire user feature into the NavGraph
 */
fun NavGraphBuilder.userGraph(navController: NavController) {
    
    // User List Screen
    composable(route = UserRoutes.USER_LIST) {
        UserListRoute(
            onNavigateToUserDetail = { userId ->
                navController.navigate(UserRoutes.createDetailRoute(userId))
            }
        )
    }

    // User Detail Screen
    composable(
        route = UserRoutes.USER_DETAIL,
        arguments = listOf(
            navArgument("userId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
        
        UserDetailRoute(
            userId = userId,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
