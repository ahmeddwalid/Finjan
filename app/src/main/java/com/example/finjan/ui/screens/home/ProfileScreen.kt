package com.example.finjan.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                .padding(4.dp) // Padding to create the outline around the profile picture
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome Ahmed",
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
                FirebaseAuth.getInstance().signOut() // Log out the user
                navController.navigate("welcome_screen") { // Navigate to WelcomeScreen
                    popUpTo(0) // Clear the navigation stack
                }
            },
            text = "Logout",
            modifier = Modifier.padding(horizontal = 60.dp)
        )


        // Spacer for spacing the bottom navigation bar
        Spacer(modifier = Modifier.weight(1f))

        // Floating Navigation Bar
        FloatingNavigationBar(navController = navController, items = items)
    }
}
