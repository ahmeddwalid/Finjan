package com.example.finjan.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.model.BottomNavItem
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {
    val items = listOf(
        BottomNavItem(icon = R.drawable.ic_home, route = "home"),
        BottomNavItem(icon = R.drawable.ic_qr_code, route = "qrcode"),
        BottomNavItem(icon = R.drawable.ic_shopping_bag, route = "offers"),
        BottomNavItem(icon = R.drawable.ic_profile, route = "profile")
    )

    // Add state for user's name
    var userName by remember { mutableStateOf("") }

    // Effect to fetch user's name when the screen is created
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        userName = when {
            !currentUser?.displayName.isNullOrEmpty() -> currentUser?.displayName ?: ""
            !currentUser?.email.isNullOrEmpty() -> currentUser?.email?.substringBefore("@") ?: ""
            else -> "User"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                style = TextStyle(
                    fontSize = 27.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            )

            IconButton(
                onClick = { navController.navigate("settings_screen") },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .background(AccentColor, shape = CircleShape)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome $userName",
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        FilledButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("welcome_screen") {
                    popUpTo(0)
                }
            },
            text = "Logout",
            modifier = Modifier.padding(horizontal = 60.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        FloatingNavigationBar(navController = navController, items = items)
    }
}