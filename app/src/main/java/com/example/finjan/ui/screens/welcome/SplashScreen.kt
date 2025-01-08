package com.example.finjan.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.navigate("page_view_screen")
    }
    FinjanTheme {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.example.finjan.ui.components.SplashScreen()
            Text(
                text = "Your Premium Coffee",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = com.example.finjan.ui.theme.PoppinsFontFamily,
                    fontWeight = FontWeight.Thin,
                    color = PrimaryColor
                )
            )
        }
    }
}