package com.example.finjan

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finjan.ui.screens.authentication.LoginScreen
import com.example.finjan.ui.screens.authentication.SignUpScreen
import com.example.finjan.ui.screens.welcome.PageViewScreen
import com.example.finjan.ui.screens.welcome.WelcomeScreen
import com.example.finjan.ui.screens.home.HomeScreen
import com.example.finjan.ui.screens.home.QrCodeScreen
import com.example.finjan.ui.screens.home.OffersScreen
import com.example.finjan.ui.screens.home.ProfileScreen
import com.example.finjan.ui.screens.settings.SettingsScreen
import com.example.finjan.ui.screens.welcome.SplashScreen
import com.example.finjan.viewmodel.SharedViewModel

@Composable
fun NavigationManager(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController)
        }
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
        composable("home") {
            HomeScreen(navController)
        }
        composable("qrcode") {
            QrCodeScreen(navController)
        }
        composable("offers") {
            OffersScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("settings_screen") {
            SettingsScreen(navController)
        }
    }
}
