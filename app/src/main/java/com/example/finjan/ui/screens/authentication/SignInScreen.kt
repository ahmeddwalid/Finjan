package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import com.example.finjan.viewmodel.LoginViewModel
import androidx.compose.runtime.mutableStateOf


@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    // Observe the error message from the ViewModel
    val errorMessage = loginViewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier)
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
            value = loginViewModel.email, // Bind to ViewModel state
            onValueChange = { input -> // Validate and update ViewModel state
                loginViewModel.email = input // Update email in ViewModel
                loginViewModel.isEmailValid = loginViewModel.isEmailValid(input) // Validate email
            },
            keyboardType = KeyboardType.Email // Email-specific keyboard
        )
        Spacer(modifier = Modifier.height(28.dp))

        AppTextField(
            hint = "Password",
            value = loginViewModel.password,
            onValueChange = { input -> // Validate and update ViewModel state
                loginViewModel.password = input
                loginViewModel.isPasswordValid = loginViewModel.isPasswordValid(input)
            },
            keyboardType = KeyboardType.Password
        )
        Spacer(modifier = Modifier.height(28.dp))

        FilledButton(
            onClick = {
                if (loginViewModel.authenticate()) {
                    navController.navigate("home")
                }
            },
            text = "Login",
            modifier = Modifier.padding(horizontal = 34.dp)
        )

        // Display the error message in a Snackbar or Text
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                style = TextStyle(color = androidx.compose.ui.graphics.Color.Red)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hidden back button
        OutlinedButton (onClick = {
            // Navigate back to the previous screen
            navController.popBackStack()
        }) {
            Text(text = "Back")
        }
    }
}
