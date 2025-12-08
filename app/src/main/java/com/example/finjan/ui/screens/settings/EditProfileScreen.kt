package com.example.finjan.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthenticationViewModel
) {
    var displayName by remember {
        mutableStateOf(FirebaseAuth.getInstance().currentUser?.displayName ?: "")
    }
    val errorMessage = authViewModel.errorMessage
    val isLoading = authViewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
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
            text = "Edit Profile",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppTextField(
            hint = "Display Name",
            value = displayName,
            onValueChange = { displayName = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledButton(
            text = if (isLoading) "Saving..." else "Save Changes",
            enabled = !isLoading && displayName.isNotBlank(),
            onClick = {
                authViewModel.updateDisplayName(displayName) {
                    navController.popBackStack()
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
    }
}
