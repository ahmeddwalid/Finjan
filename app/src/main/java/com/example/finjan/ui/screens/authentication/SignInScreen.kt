package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.ui.AppTextField
import com.example.finjan.ui.FilledButton
import com.example.finjan.ui.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor

@Composable
fun LoginScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier)
        Text(
            text = "Login Screen",
            style = TextStyle(
                fontSize = 35.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )
        Spacer(modifier = Modifier.height(45.dp))

        AppTextField(hint = "Email", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(hint = "Password", keyboardType = KeyboardType.Password)
        Spacer(modifier = Modifier.height(28.dp))
        FilledButton (
            onClick = {navController.navigate("home")},
            text = "Login",
            modifier = Modifier
                .padding(horizontal = 34.dp)
        )
    }
}
