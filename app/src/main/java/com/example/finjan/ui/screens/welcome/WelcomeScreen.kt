package com.example.finjan.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.components.Footer
import com.example.finjan.ui.components.LegalDialog
import com.example.finjan.ui.components.Logo
import com.example.finjan.ui.components.TermsAndPrivacyPolicy
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.viewmodel.LegalViewModel

@Composable
fun WelcomeScreen(
    navController: NavController,
    legalViewModel: LegalViewModel = viewModel()
) {
    FinjanTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome text
            Text(
                text = "Ahlan beek fe Finjan :)",
                style = TextStyle(
                    fontSize = 27.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            )

            Spacer(modifier = Modifier.height(45.dp))

            // App logo
            Logo(modifier = Modifier)

            Spacer(modifier = Modifier.height(5.dp))

            // Login button
            FilledButton(
                onClick = { navController.navigate("login_screen") },
                text = "Login",
                modifier = Modifier.padding(horizontal = 34.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign-up button
            FilledButton(
                onClick = { navController.navigate("signup_screen") },
                text = "Create an Account",
                modifier = Modifier.padding(horizontal = 34.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TermsAndPrivacyPolicy (
                onTermsClick = { legalViewModel.showTermsOfService() },
                onPrivacyPolicyClick = { legalViewModel.showPrivacyPolicy() }
            )
        }

        if (legalViewModel.showLegalDialog) {
            LegalDialog(
                legalDocument = legalViewModel.currentLegalDocument,
                isLoading = legalViewModel.isLoading,
                error = legalViewModel.error,
                onDismiss = { legalViewModel.hideLegalDialog() }
            )
        }
    }
}
