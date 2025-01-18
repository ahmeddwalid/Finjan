package com.example.finjan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor

@Composable
fun TermsAndPrivacyPolicy(
    onTermsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "By continuing, you agree to our",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = SecondaryColor,
                    fontFamily = PoppinsFontFamily
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onTermsClick,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Terms of Service",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = PrimaryColor,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Text(
                text = "and",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = SecondaryColor,
                    fontFamily = PoppinsFontFamily
                )
            )

            TextButton(
                onClick = onPrivacyPolicyClick,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Privacy Policy",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = PrimaryColor,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}