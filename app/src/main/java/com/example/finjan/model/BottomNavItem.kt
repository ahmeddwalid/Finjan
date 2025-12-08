package com.example.finjan.model

import com.example.finjan.navigation.Route

/**
 * Represents a bottom navigation item with type-safe routing.
 */
data class BottomNavItem(
    val icon: Int,
    val route: Route,
    val contentDescription: String = ""
)