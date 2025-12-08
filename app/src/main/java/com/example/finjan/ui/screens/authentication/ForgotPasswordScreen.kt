package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.navigation.Route
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.components.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.viewmodel.AuthenticationViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    val errorMessage = authViewModel.errorMessage
    val isLoading = authViewModel.isLoading
    val successMessage = authViewModel.successMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Forgot Password",
            style = TextStyle(
                fontSize = 32.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter your email address and we'll send you a link to reset your password",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsFontFamily,
                color = SecondaryColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            hint = "Email",
            value = authViewModel.email,
            onValueChange = { input ->
                authViewModel.email = input
                authViewModel.errorMessage = ""
                authViewModel.successMessage = ""
            },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(28.dp))

        FilledButton(
            modifier = Modifier.padding(horizontal = 34.dp),
            onClick = {
                authViewModel.sendPasswordResetEmail()
            },
            text = if (isLoading) "Sending..." else "Send Reset Link"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = TextStyle(color = Color.Red),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                style = TextStyle(color = Color(0xFF4CAF50)),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { navController.navigate(Route.SignIn) }
        ) {
            Text(
                text = "Back to Sign In",
                style = TextStyle(
                    color = PrimaryColor,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
