package com.example.finjan.ui.screens.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.navigation.Route
import com.example.finjan.navigation.navigateAfterAuth
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.BorderButton
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.components.Logo
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.utils.auth.GoogleAuthManager
import com.example.finjan.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    val errorMessage = authViewModel.errorMessage
    val isLoading = authViewModel.isLoading
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthManager = GoogleAuthManager(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Text(
            text = "Welcome Back!",
            style = TextStyle(
                fontSize = 35.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(35.dp))

        AppTextField(
            hint = "Email",
            value = authViewModel.email,
            onValueChange = { input ->
                authViewModel.email = input
                authViewModel.isEmailValid = authViewModel.isEmailValid(input)
            },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            hint = "Password",
            value = authViewModel.password,
            onValueChange = { input ->
                authViewModel.password = input
                authViewModel.isPasswordValid = authViewModel.isPasswordValid(input)
            },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(20.dp))

        FilledButton(
            modifier = Modifier.padding(horizontal = 34.dp),
            onClick = {
                authViewModel.signIn {
                    navController.navigateAfterAuth(Route.Home)
                }
            },
            text = if (isLoading) "Logging In..." else "Login",
            enabled = !isLoading
        )

        // Display error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                style = TextStyle(
                    color = Color.Red,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider with "OR"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = SecondaryColor
            )
            Text(
                text = "  OR  ",
                style = TextStyle(
                    color = SecondaryColor,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 14.sp
                )
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = SecondaryColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Button
        OutlinedButton(
            onClick = {
                scope.launch {
                    authViewModel.isLoading = true
                    when (val result = googleAuthManager.signIn()) {
                        is GoogleAuthManager.GoogleSignInResult.Success -> {
                            authViewModel.isLoading = false
                            navController.navigateAfterAuth(Route.Home)
                        }
                        is GoogleAuthManager.GoogleSignInResult.Error -> {
                            authViewModel.isLoading = false
                            authViewModel.errorMessage = result.message
                        }
                        is GoogleAuthManager.GoogleSignInResult.Cancelled -> {
                            authViewModel.isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.dp, PrimaryColor),
            enabled = !isLoading
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = TextStyle(
                        color = PrimaryColor,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier.padding(horizontal = 100.dp),
            text = "Sign Up",
            onClick = {
                navController.navigate(Route.SignUp)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password link
        TextButton(
            onClick = { navController.navigate(Route.ForgotPassword) }
        ) {
            Text(
                text = "Forgot Password?",
                style = TextStyle(
                    color = PrimaryColor,
                    fontFamily = PoppinsFontFamily
                )
            )
        }
    }
}
