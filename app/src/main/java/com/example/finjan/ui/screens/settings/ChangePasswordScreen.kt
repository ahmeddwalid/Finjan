package com.example.finjan.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.ui.components.AppTextField
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.AuthenticationViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val errorMessage = authViewModel.errorMessage
    val successMessage = authViewModel.successMessage
    val isLoading = authViewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dropdown),
                contentDescription = "Back",
                tint = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Change Password",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppTextField(
            hint = "Current Password",
            value = currentPassword,
            onValueChange = { currentPassword = it },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            hint = "New Password",
            value = newPassword,
            onValueChange = { newPassword = it },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            hint = "Confirm New Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledButton(
            text = if (isLoading) "Changing..." else "Change Password",
            enabled = !isLoading && currentPassword.isNotEmpty() && 
                     newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
            onClick = {
                when {
                    newPassword != confirmPassword -> {
                        authViewModel.errorMessage = "Passwords do not match"
                    }
                    else -> {
                        authViewModel.changePassword(currentPassword, newPassword) {
                            navController.popBackStack()
                        }
                    }
                }
            },
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

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                style = TextStyle(color = Color(0xFF4CAF50)),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
