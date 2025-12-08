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
            route = Route.QrCode,
            contentDescription = "QR Code"
        ),
        BottomNavItem(
            icon = R.drawable.ic_shopping_bag,
            route = Route.Offers,
            contentDescription = "Offers"
        ),
        BottomNavItem(
            icon = R.drawable.ic_profile,
            route = Route.Profile,
            contentDescription = "Profile"
        )
    )
}
