package com.example.finjan.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.finjan.ui.CoffeeCup
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.ImageCard
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor

@Composable
fun HomeScreen(navController: NavController) {
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
                .background(BackgroundColor)
        ) {

            Spacer(modifier = Modifier.size(20.dp))

            val painter = painterResource(id = R.drawable.otetein)
            val description = "Two cats hugging cutely"
            val title = "Two cats being cute"
            Box (modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(16.dp)
            ) {
                ImageCard(
                    painter = painter,
                    contentDescription = description,
                    title = title
                )
            }

//            CoffeeCup()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Home",
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

