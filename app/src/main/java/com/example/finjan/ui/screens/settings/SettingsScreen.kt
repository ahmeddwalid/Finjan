package com.example.finjan.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.model.SettingItem
import com.example.finjan.ui.components.BorderButton
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val settings = remember {
        listOf(
            SettingItem(
                "Account Settings",
                R.drawable.ic_launcher_color,  // Replace with your account icon
                "Manage your profile and preferences",
            ) {
                AccountSettings()
            },
            SettingItem(
                "Notifications",
                R.drawable.ic_launcher_color,  // Replace with your notification icon
                "Configure notifications and alerts",
            ) {
                NotificationSettings()
            },
            SettingItem(
                "Payment Methods",
                R.drawable.ic_launcher_color,  // Replace with your payment icon
                "Manage your payment options",
            ) {
                PaymentSettings(navController)
            },
            SettingItem(
                "Order History",
                R.drawable.ic_launcher_color,  // Replace with your history icon
                "View your past orders",
            ) {
                OrderHistory()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(settings) { setting ->
                ExpandableSettingItem(setting, navController)
            }
        }
    }
}

@Composable
fun ExpandableSettingItem(setting: SettingItem, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColor
        ),
        border = BorderStroke(1.dp, PrimaryColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = setting.title,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryColor
                            )
                        )
                        Text(
                            text = setting.description,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PoppinsFontFamily,
                                color = SecondaryColor
                            )
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown), // Replace with arrow icon
                    contentDescription = "Expand",
                    tint = PrimaryColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationState)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    setting.content()
                }
            }
        }
    }
}

@Composable
fun AccountSettings() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledButton(
            text = "Edit Profile",
            onClick = { /* TODO */ }
        )
        BorderButton(
            text = "Change Password",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun NotificationSettings() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchSetting("Order Updates")
        SwitchSetting("Promotional Offers")
        SwitchSetting("New Products")
    }
}

@Composable
fun PaymentSettings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledButton(
            text = "Add Payment Method",
                onClick = { /* TODO */ }
        )
        Text(
            "Saved Cards",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { navController.navigate("bankcard_details") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.simple_credit_card_outline),
                    contentDescription = "Credit Card",
                    tint = TextColor,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "**** **** **** 1234",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = TextColor
                        )
                    )
                    Text(
                        text = "Expires 12/27",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PoppinsFontFamily,
                            color = TextColor.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistory() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Recent Orders",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )
        )
        // Add your order history items here
    }
}

@Composable
fun SwitchSetting(text: String) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PoppinsFontFamily,
                color = PrimaryColor
            )
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryColor,
                checkedTrackColor = AccentColor
            )
        )
    }
}