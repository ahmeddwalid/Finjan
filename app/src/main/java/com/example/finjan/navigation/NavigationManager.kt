package com.example.finjan.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finjan.ui.screens.welcome.*
import com.example.finjan.ui.screens.authentication.*
import com.example.finjan.ui.screens.home.*
import com.example.finjan.ui.screens.cart.CartScreen
import com.example.finjan.ui.screens.favorites.FavoritesScreen
import com.example.finjan.ui.screens.order.OrderTrackingScreen
import com.example.finjan.ui.screens.order.OrderHistoryScreen
import com.example.finjan.ui.screens.settings.SettingsScreen
import com.example.finjan.ui.screens.settings.BankCardDetails
import com.example.finjan.ui.screens.settings.EditProfileScreen
import com.example.finjan.ui.screens.settings.ChangePasswordScreen
import com.example.finjan.ui.screens.payment.AddPaymentMethodScreen
import com.example.finjan.ui.screens.product.ProductDetailsScreen
import com.example.finjan.ui.screens.search.SearchHistoryScreen
import com.example.finjan.ui.screens.checkout.CheckoutScreen
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.*

@Composable
fun NavigationManager(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    startDestination: Route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Splash> {
            SplashScreen(navController = navController)
        }
        composable<Route.Welcome> {
            WelcomeScreen(navController = navController)
        }
        composable<Route.PageView> {
            PageViewScreen(navController = navController)
        }
        
        composable<Route.SignIn> {
            val authViewModel: AuthenticationViewModel = viewModel()
            SignInScreen(navController = navController, authViewModel = authViewModel)
        }
        composable<Route.SignUp> {
            val authViewModel: AuthenticationViewModel = viewModel()
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable<Route.ForgotPassword> {
            val authViewModel: AuthenticationViewModel = viewModel()
            ForgotPasswordScreen(navController = navController, authViewModel = authViewModel)
        }
        
        composable<Route.Home> {
            HomeScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable<Route.Offers> {
            OffersScreen(navController = navController)
        }
        composable<Route.Profile> {
            ProfileScreen(navController = navController)
        }
        composable<Route.QrCode> {
            QrCodeScreen(navController = navController)
        }
        
        composable<Route.Cart> {
            val cartViewModel: CartViewModel = viewModel()
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }
        composable<Route.Favorites> {
            val favoritesViewModel: FavoritesViewModel = viewModel()
            FavoritesScreen(navController = navController, favoritesViewModel = favoritesViewModel)
        }
        
        composable<Route.OrderTracking> { backStackEntry ->
             val args = backStackEntry.toRoute<Route.OrderTracking>()
             val orderViewModel: OrderViewModel = viewModel()
             OrderTrackingScreen(
                navController = navController, 
                orderId = args.orderId,
                orderViewModel = orderViewModel
             )
        }
        
        // Settings screens
        composable<Route.Settings> {
            SettingsScreen(navController = navController)
        }
        composable<Route.BankCardDetails> {
            BankCardDetails()
        }
        composable<Route.EditProfile> {
            val authViewModel: AuthenticationViewModel = viewModel()
            EditProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable<Route.ChangePassword> {
            val authViewModel: AuthenticationViewModel = viewModel()
            ChangePasswordScreen(navController = navController, authViewModel = authViewModel)
        }
        
        // Now fully implemented screens
        composable<Route.OrderHistory> {
            OrderHistoryScreen(navController = navController)
        }
        composable<Route.AddPaymentMethod> {
            AddPaymentMethodScreen(navController = navController)
        }
        composable<Route.SearchHistory> {
            SearchHistoryScreen(
                navController = navController,
                onSearchClick = { query ->
                    // Navigate back to home with the search query
                    navController.popBackStack()
                }
            )
        }
        composable<Route.ProductDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.ProductDetails>()
            ProductDetailsScreen(
                navController = navController,
                productId = args.productId
            )
        }
        composable<Route.Checkout> {
            val checkoutViewModel: CheckoutViewModel = viewModel()
            CheckoutScreen(navController = navController, viewModel = checkoutViewModel)
        }
    }
}

/**
 * Extension to navigate and clear back stack (e.g. after login).
 */
fun NavController.navigateAfterAuth(destination: Route) {
    navigate(destination) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

