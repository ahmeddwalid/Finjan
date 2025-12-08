package com.example.finjan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finjan.model.BottomNavItem
import com.example.finjan.navigation.NavigationItems
import com.example.finjan.navigation.Route
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor

/**
 * Floating navigation bar component used across main screens.
 * Uses NavigationItems singleton for consistent navigation options.
 */
@Composable
fun FloatingNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = NavigationItems.bottomNavItems
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(
                color = PrimaryColor,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentDestination?.hasRoute(item.route::class) == true
                
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(Route.Home) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.contentDescription,
                            tint = if (isSelected) SecondaryColor else Color.White
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
