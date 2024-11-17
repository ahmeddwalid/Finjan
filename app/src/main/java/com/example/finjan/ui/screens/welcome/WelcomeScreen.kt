package com.example.finjan.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.ui.FilledButton
import com.example.finjan.ui.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier)

        Spacer(modifier = Modifier.height(45.dp))

        Text(
            text = "Ahlan beek fe Finjan",
            style = TextStyle(
                fontSize = 35.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        FilledButton (
            onClick = {navController.navigate("login_screen")},
            text = "Login",
            modifier = Modifier
                .padding(horizontal = 34.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        FilledButton (
            onClick = {navController.navigate("signup_screen")},
            text = "Create an Account",
            modifier = Modifier
                .padding(horizontal = 34.dp)
        )
    }
}
