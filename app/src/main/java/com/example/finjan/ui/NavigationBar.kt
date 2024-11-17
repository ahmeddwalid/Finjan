package com.example.finjan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finjan.R
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor

@Composable
fun FloatingNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    // Get the current backstack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column (
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp) // Padding for floating effect
                .background(
                    color = PrimaryColor, // Brown color for background
                    shape = RoundedCornerShape(50.dp) // Fully rounded corners
                )
                .padding(vertical = 8.dp), // Inner padding
            contentAlignment = Alignment.BottomCenter
        ) {
            NavigationBar(
                containerColor = PrimaryColor, // Brown background
                contentColor = Color.White,
                modifier = modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 17.dp)
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route, // Compare with current route
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    // Avoid building up the back stack
                                    popUpTo(navController.graph.startDestinationId) {
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
                                contentDescription = null,
                                tint = if (currentRoute == item.route) SecondaryColor
                                else Color.White
                            )
                        }
                    )
                }
            }
        }
    }
}

data class BottomNavItem(val icon: Int, val route: String)
