package com.example.finjan

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finjan.ui.screens.authentication.LoginScreen
import com.example.finjan.ui.screens.authentication.SignUpScreen
import com.example.finjan.ui.screens.welcome.PageViewScreen
import com.example.finjan.ui.screens.welcome.WelcomeScreen
import com.example.finjan.viewmodel.SharedViewModel

@Composable
fun NavigationManager(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "page_view_screen"
    ) {
        composable("page_view_screen") {
            PageViewScreen(navController)
        }
        composable("welcome_screen") {
            WelcomeScreen(navController)
        }
        composable("login_screen") {
            LoginScreen(navController)
        }
        composable("signup_screen") {
            SignUpScreen(navController)
        }
    }
}
