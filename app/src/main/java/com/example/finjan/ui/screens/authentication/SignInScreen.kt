package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.BorderButton
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.components.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.AuthenticationViewModel
import com.example.finjan.ui.theme.FinjanTheme


@Composable
fun SignInScreen(navController: NavController, authViewModel: AuthenticationViewModel) {
    FinjanTheme {
        val errorMessage = authViewModel.errorMessage
        val isLoading = authViewModel.isLoading

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Logo(modifier = Modifier
                .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Welcome Back!",
                style = TextStyle(
                    fontSize = 35.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            )

            Spacer(modifier = Modifier.height(45.dp))

            AppTextField(
                hint = "Email",
                value = authViewModel.email,
                onValueChange = { input ->
                    authViewModel.email = input
                    authViewModel.isEmailValid = authViewModel.isEmailValid(input)
                },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                hint = "Password",
                value = authViewModel.password,
                onValueChange = { input ->
                    authViewModel.password = input
                    authViewModel.isPasswordValid = authViewModel.isPasswordValid(input)
                },
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(28.dp))

            FilledButton(
                modifier = Modifier.padding(horizontal = 34.dp),
                onClick = {
                    authViewModel.signIn {
                        navController.navigate("home")
                    }
                },
                text = if (isLoading) "Logging In..." else "Login"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display the error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = TextStyle(color = androidx.compose.ui.graphics.Color.Red)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            BorderButton(
                modifier = Modifier.padding(horizontal = 100.dp),
                text = "Sign Up",
                onClick = {
                    navController.navigate("signup_screen")
                }
            )
        }
    }
}
