package com.example.finjan.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * Each route is a serializable object for compile-time safety.
 */
sealed interface Route {
    
    // Welcome/Onboarding Flow
    @Serializable
    data object Splash : Route
    
    @Serializable
    data object PageView : Route
    
    @Serializable
    data object Welcome : Route
    
    // Authentication Flow
    @Serializable
    data object SignIn : Route
    
    @Serializable
    data object SignUp : Route
    
    @Serializable
    data object ForgotPassword : Route
    
    // Main App Flow
    @Serializable
    data object Home : Route
    
    @Serializable
    data object QrCode : Route
    
    @Serializable
    data object Offers : Route
    
    @Serializable
    data object Profile : Route
    
    // Settings Flow
    @Serializable
    data object Settings : Route
    
    @Serializable
    data object BankCardDetails : Route
    
    @Serializable
    data object EditProfile : Route
    
    @Serializable
    data object ChangePassword : Route
    
    // Product Details
    @Serializable
    data class ProductDetails(val productId: String) : Route
    
    // Cart & Orders
    @Serializable
    data object Cart : Route
    
    @Serializable
    data object Favorites : Route
    
    @Serializable
    data class OrderTracking(val orderId: String) : Route
    
    @Serializable
    data object OrderHistory : Route
    
    @Serializable
    data object Checkout : Route
    
    // Payment
    @Serializable
    data object AddPaymentMethod : Route
    
    // Search
    @Serializable
    data object SearchHistory : Route
}
