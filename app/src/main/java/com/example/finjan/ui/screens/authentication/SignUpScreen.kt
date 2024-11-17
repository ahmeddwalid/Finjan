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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.ui.AppTextField
import com.example.finjan.ui.FilledButton
import com.example.finjan.ui.Footer
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor

@Composable
fun SignUpScreen(navController: NavController) {
    // State variables for the form fields
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Sign Up",
            style = TextStyle(
                fontSize = 35.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(36.dp))
        AppTextField(hint = "Name")
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(hint = "Email", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(hint = "Mobile no", keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(hint = "Address")
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(hint = "Password", keyboardType = KeyboardType.Password)
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            hint = "Confirm password",
            keyboardType = KeyboardType.Password,
            action = ImeAction.Done
        )
        Spacer(modifier = Modifier.height(28.dp))
        FilledButton (
            onClick = {navController.navigate("home")},
            text = "Sign Up",
            modifier = Modifier
                .padding(horizontal = 34.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Footer(
            text = "Already have an Account?",
            textButton = "Login",
            onClick = { navController.navigate("login_screen") }
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back Button
        Button(onClick = {
            // Navigate back to the previous screen
            navController.popBackStack()
        }) {
            Text(text = "Back")
        }
    }
}

