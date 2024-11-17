package com.example.finjan.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.ui.BottomNavItem
import com.example.finjan.ui.FloatingNavigationBar // Use the renamed function here

@Composable
fun CartScreen(navController: NavController) {
    val items = listOf(
        BottomNavItem(icon = R.drawable.ic_home, route = "home"),
        BottomNavItem(icon = R.drawable.ic_add, route = "qrcode"),
        BottomNavItem(icon = R.drawable.ic_cart, route = "cart"),
        BottomNavItem(icon = R.drawable.ic_profile, route = "profile")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingNavigationBar(navController = navController, items = items)
    }
}
