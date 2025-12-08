package com.example.finjan.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finjan.ui.components.BankCardUi
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor

/**
 * Screen displaying bank card details.
 * Uses sample data for demonstration purposes.
 */
@Composable
fun BankCardDetails() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Card Details",
            style = TextStyle(
                fontSize = 35.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        BankCardUi(
            modifier = Modifier.padding(16.dp),
            baseColor = Color(0xFFFF9800),
            cardNumber = "•••• •••• •••• 1234",
            cardHolder = "Card Holder",
            expires = "••/••",
            cvv = "•••",
            brand = "VISA"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap to view full card details",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsFontFamily,
                color = PrimaryColor.copy(alpha = 0.7f)
            )
        )
    }
}
