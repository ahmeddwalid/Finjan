package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.finjan.ui.Footer
import com.example.finjan.ui.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.AuthenticationViewModel

@Composable
fun SignUpScreen(navController: NavController, loginViewModel: AuthenticationViewModel) {
    FinjanTheme {
        // State variables for the form fields
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        val errorMessage = loginViewModel.errorMessage
        val isLoading = loginViewModel.isLoading

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Logo(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Sign Up",
                style = TextStyle(
                    fontSize = 35.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            AppTextField(
                hint = "Name",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                hint = "Email",
                value = email,
                onValueChange = {
                    email = it
                    loginViewModel.email = it
                },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                hint = "Password",
                value = password,
                onValueChange = {
                    password = it
                    loginViewModel.password = it
                },
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                hint = "Confirm password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign-Up Button
            FilledButton(
                onClick = {
                    if (password != confirmPassword) {
                        loginViewModel.errorMessage = "Passwords do not match!"
                    } else if (username.isBlank()) {
                        loginViewModel.errorMessage = "Name cannot be empty!"
                    } else {
                        loginViewModel.signUp(username) {
                            navController.navigate("home")
                        }
                    }
                },
                text = if (isLoading) "Signing Up..." else "Sign Up",
                modifier = Modifier.padding(horizontal = 34.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = TextStyle(color = androidx.compose.ui.graphics.Color.Red)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Footer(
                text = "Already have an Account?",
                textButton = "Login",
                onClick = { navController.navigate("login_screen") }
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}
