package com.example.finjan.ui.screens.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.navigation.Route
import com.example.finjan.navigation.navigateAfterAuth
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.ErrorColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.SurfaceColor
import com.example.finjan.utils.auth.GoogleAuthManager
import com.example.finjan.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.delay
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

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Animated Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_color),
                contentDescription = "Finjan Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Welcome Text with animation
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back!",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                    Text(
                        text = "Sign in to continue your coffee journey",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = SecondaryColor
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceColor.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppTextField(
                        hint = "Email",
                        value = authViewModel.email,
                        onValueChange = { input ->
                            authViewModel.email = input
                            authViewModel.isEmailValid = authViewModel.isEmailValid(input)
                        },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        hint = "Password",
                        value = authViewModel.password,
                        onValueChange = { input ->
                            authViewModel.password = input
                            authViewModel.isPasswordValid = authViewModel.isPasswordValid(input)
                        },
                        keyboardType = KeyboardType.Password
                    )

                    // Forgot Password link - inside card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { navController.navigate(Route.ForgotPassword) }
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = SecondaryColor,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login Button
                    FilledButton(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        onClick = {
                            authViewModel.signIn {
                                navController.navigateAfterAuth(Route.Home)
                            }
                        },
                        text = if (isLoading) "Signing In..." else "Sign In",
                        enabled = !isLoading
                    )

                    // Error message
                    AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorColor.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = errorMessage,
                                style = TextStyle(
                                    color = ErrorColor,
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = SecondaryColor.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Text(
                    text = "  or continue with  ",
                    style = TextStyle(
                        color = SecondaryColor,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = SecondaryColor.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, SecondaryColor.copy(alpha = 0.6f)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google logo",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = TextStyle(
                                color = PrimaryColor,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = SecondaryColor,
                        fontFamily = PoppinsFontFamily
                    )
                )
                TextButton(onClick = { navController.navigate(Route.SignUp) }) {
                    Text(
                        text = "Sign Up",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = PrimaryColor,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
