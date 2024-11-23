package com.example.finjan.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.screens.welcome.primaryFontColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor

@Composable
fun ProfileScreen(navController: NavController) {
    FinjanTheme {
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
//            Row (modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.End)
//            )
//            {
//                Text(
//                    text = "Profile",
//                    style = TextStyle(
//                        fontSize = 27.sp,
//                        fontFamily = PoppinsFontFamily,
//                        fontWeight = FontWeight.Bold,
//                        color = PrimaryColor
//                    )
//                )
//
//                IconButton (
//                    onClick = { navController.navigate("settings_screen") },
//                ) {
//                    Icon(
//                        painter = painterResource(id = com.example.finjan.R.drawable.baseline_settings_24),
//                        contentDescription = "",
//                        tint = primaryFontColor
//                    )
//                }
//            }

            Spacer(modifier = Modifier.padding(20.dp))

            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Image",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.padding(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Welcome Ahmed",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )
            }

            // Floating Navigation Bar
            FloatingNavigationBar(navController = navController, items = items)
        }
    }
}

