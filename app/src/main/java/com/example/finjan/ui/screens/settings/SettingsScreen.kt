package com.example.finjan.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.model.BottomNavItem
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.LoaderOne
import com.example.finjan.ui.LoaderTwo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor

@Composable
fun SettingsScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
    ) {

        LoaderTwo()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            )
        }
    }
}

