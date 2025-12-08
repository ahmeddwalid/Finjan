package com.example.finjan.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.components.QrCodeDisplay
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun QrCodeScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Claim Points",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = PrimaryColor
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                )

                Text(
                    text = "✦ QR Code ✦",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    ),
                    modifier = Modifier.padding(16.dp)
                )

                QrCodeDisplay(
                    content = "finjan:user:$userId",
                    modifier = Modifier.size(300.dp)
                )
            }
        }

        FloatingNavigationBar(navController = navController)
    }
}
