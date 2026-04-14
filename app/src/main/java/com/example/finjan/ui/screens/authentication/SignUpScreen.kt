package com.example.finjan.ui.screens.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.ErrorColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.SuccessColor
import com.example.finjan.ui.theme.SurfaceColor
import com.example.finjan.ui.theme.WarningColor
import com.example.finjan.viewmodel.AuthenticationViewModel
import com.example.finjan.viewmodel.GoogleAuthState
import kotlinx.coroutines.delay

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val errorMessage = authViewModel.errorMessage
    val isLoading = authViewModel.isLoading
    val context = LocalContext.current
    val googleAuthState by authViewModel.googleAuthState.collectAsState()

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

    LaunchedEffect(googleAuthState) {
        if (googleAuthState is GoogleAuthState.Success) {
            authViewModel.resetGoogleAuthState()
            navController.navigateAfterAuth(Route.Home)
        }
    }

    // Password strength calculation
    val passwordStrength by remember(authViewModel.password) {
        derivedStateOf {
            calculatePasswordStrength(authViewModel.password)
        }
    }

    val passwordStrengthColor by animateColorAsState(
        targetValue = when {
            passwordStrength < 0.25f -> ErrorColor
            passwordStrength < 0.5f -> WarningColor
            passwordStrength < 0.75f -> AccentColor
            else -> SuccessColor
        },
        label = "passwordStrengthColor"
    )

    val passwordStrengthText = when {
        authViewModel.password.isEmpty() -> ""
        passwordStrength < 0.25f -> "Weak"
        passwordStrength < 0.5f -> "Fair"
        passwordStrength < 0.75f -> "Good"
        else -> "Strong"
    }

    // Password match check
    val passwordsMatch = confirmPassword.isNotEmpty() && authViewModel.password == confirmPassword

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
            Spacer(modifier = Modifier.height(48.dp))

            // Animated Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_color),
                contentDescription = "Finjan Logo",
                modifier = Modifier
                    .size(100.dp)
                    .scale(logoScale)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Welcome Text with animation
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Create Account",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                    Text(
                        text = "Join us for the perfect brew ☕",
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

            Spacer(modifier = Modifier.height(32.dp))

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
                        hint = "Full Name",
                        value = username,
                        onValueChange = { username = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        hint = "Email",
                        value = authViewModel.email,
                        onValueChange = {
                            authViewModel.email = it
                            authViewModel.isEmailValid = authViewModel.isEmailValid(it)
                        },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        hint = "Password",
                        value = authViewModel.password,
                        onValueChange = {
                            authViewModel.password = it
                            authViewModel.isPasswordValid = authViewModel.isPasswordValid(it)
                        },
                        keyboardType = KeyboardType.Password
                    )

                    // Password Strength Indicator
                    AnimatedVisibility(
                        visible = authViewModel.password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Password Strength",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PoppinsFontFamily,
                                        color = SecondaryColor
                                    )
                                )
                                Text(
                                    text = passwordStrengthText,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = passwordStrengthColor
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { passwordStrength },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = passwordStrengthColor,
                                trackColor = SecondaryColor.copy(alpha = 0.2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AppTextField(
                        hint = "Confirm Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        keyboardType = KeyboardType.Password
                    )

                    // Password match indicator
                    AnimatedVisibility(
                        visible = confirmPassword.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (passwordsMatch) SuccessColor else ErrorColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (passwordsMatch) "Passwords match" else "Passwords don't match",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PoppinsFontFamily,
                                    color = if (passwordsMatch) SuccessColor else ErrorColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up Button
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
                        text = if (isLoading) "Creating Account..." else "Create Account",
                        modifier = Modifier.padding(horizontal = 24.dp),
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
                    text = "  or sign up with  ",
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
                    authViewModel.signInWithGoogle(context)
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
                            text = "Sign Up with Google",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Login Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = SecondaryColor,
                        fontFamily = PoppinsFontFamily
                    )
                )
                TextButton(onClick = { navController.navigate(Route.SignIn) }) {
                    Text(
                        text = "Sign In",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = PrimaryColor,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Calculate password strength as a value between 0 and 1.
 */
private fun calculatePasswordStrength(password: String): Float {
    if (password.isEmpty()) return 0f

    var score = 0f

    // Length score (up to 0.25)
    score += when {
        password.length >= 12 -> 0.25f
        password.length >= 8 -> 0.15f
        password.length >= 6 -> 0.1f
        else -> 0.05f
    }

    // Uppercase check (0.2)
    if (password.any { it.isUpperCase() }) score += 0.2f

    // Lowercase check (0.15)
    if (password.any { it.isLowerCase() }) score += 0.15f

    // Digit check (0.2)
    if (password.any { it.isDigit() }) score += 0.2f

    // Special character check (0.2)
    if (password.any { !it.isLetterOrDigit() }) score += 0.2f

    return score.coerceIn(0f, 1f)
}
