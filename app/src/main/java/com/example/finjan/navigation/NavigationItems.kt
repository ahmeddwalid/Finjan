package com.example.finjan.navigation

import com.example.finjan.R
import com.example.finjan.model.BottomNavItem

/**
 * Single source of truth for bottom navigation items.
 * Used consistently across all screens with bottom navigation.
 */
object NavigationItems {
    
    val bottomNavItems: List<BottomNavItem> = listOf(
        BottomNavItem(
            icon = R.drawable.ic_home,
            route = Route.Home,
            contentDescription = "Home"
        ),
        BottomNavItem(
            icon = R.drawable.ic_qr_code,
            route = Route.Rewards,
            contentDescription = "Rewards"
        ),
        BottomNavItem(
            icon = R.drawable.ic_cart,
            route = Route.Cart,
            contentDescription = "Cart"
        ),
        BottomNavItem(
            icon = R.drawable.ic_profile,
            route = Route.Profile,
            contentDescription = "Profile"
        )
    )
}
