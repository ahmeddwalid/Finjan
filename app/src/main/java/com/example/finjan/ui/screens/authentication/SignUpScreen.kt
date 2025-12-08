package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.navigation.Route
import com.example.finjan.navigation.navigateAfterAuth
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.components.Footer
import com.example.finjan.ui.components.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.AuthenticationViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val errorMessage = authViewModel.errorMessage
    val isLoading = authViewModel.isLoading

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
            value = authViewModel.email,
            onValueChange = {
                authViewModel.email = it
                authViewModel.isEmailValid = authViewModel.isEmailValid(it)
            },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppTextField(
            hint = "Password",
            value = authViewModel.password,
            onValueChange = {
                authViewModel.password = it
                authViewModel.isPasswordValid = authViewModel.isPasswordValid(it)
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

        FilledButton(
            onClick = {
                when {
                    username.isBlank() -> {
                        authViewModel.errorMessage = "Name cannot be empty!"
                    }
                    !authViewModel.isEmailValid(authViewModel.email) -> {
                        authViewModel.errorMessage = "Please enter a valid email address"
                    }
                    !authViewModel.isPasswordValid(authViewModel.password) -> {
                        authViewModel.errorMessage = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
                    }
                    authViewModel.password != confirmPassword -> {
                        authViewModel.errorMessage = "Passwords do not match!"
                    }
                    else -> {
                        authViewModel.signUp(username) {
                            navController.navigateAfterAuth(Route.Home)
                        }
                    }
                }
            },
            text = if (isLoading) "Signing Up..." else "Sign Up",
            modifier = Modifier.padding(horizontal = 34.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = TextStyle(color = Color.Red),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Footer(
            text = "Already have an Account?",
            textButton = "Login",
            onClick = { navController.navigate(Route.SignIn) }
        )
    }
}
