package com.example.feature.auth.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.feature.auth.presentation.onboarding.OnboardingScreen
import com.example.feature.auth.presentation.login.LoginScreen
import com.example.feature.auth.presentation.register.RegisterScreen
import com.example.feature.auth.presentation.forgot_password.ForgotPasswordScreen

object AuthRoutes {
    const val AUTH_GRAPH = "auth_graph"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
}

fun NavGraphBuilder.authGraph(navController: NavController, onLoginSuccess: () -> Unit) {
    navigation(
        startDestination = AuthRoutes.ONBOARDING,
        route = AuthRoutes.AUTH_GRAPH
    ) {
        composable(AuthRoutes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToMain = { onLoginSuccess() },
                onNavigateToRegister = { navController.navigate(AuthRoutes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(AuthRoutes.FORGOT_PASSWORD) }
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMain = { onLoginSuccess() }
            )
        }
        composable(AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
