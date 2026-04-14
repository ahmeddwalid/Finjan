package com.example.finjan.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.data.local.ThemePreferences
import com.example.finjan.model.SettingItem
import com.example.finjan.navigation.Route
import com.example.finjan.ui.components.BorderButton
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.ErrorColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.viewmodel.SettingsViewModel
import com.example.finjan.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDarkMode = when (themeMode) {
        ThemePreferences.ThemeMode.DARK -> true
        ThemePreferences.ThemeMode.LIGHT -> false
        ThemePreferences.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val notificationSettings by settingsViewModel.notificationSettings.collectAsState()
    val language by settingsViewModel.language.collectAsState()
    
    val settings = remember(themeMode, notificationSettings, language) {
        listOf(
            SettingItem(
                "Appearance",
                R.drawable.ic_appearance,
                "Dark mode and display settings",
            ) {
                AppearanceSettings(
                    themeMode = themeMode,
                    isDarkMode = isDarkMode,
                    onThemeModeChange = { themeViewModel.setThemeMode(it) },
                    onToggleDarkMode = { themeViewModel.toggleDarkMode(isDarkMode) }
                )
            },
            SettingItem(
                "Account Settings",
                R.drawable.ic_profile,
                "Manage your profile and preferences",
            ) {
                AccountSettings(navController)
            },
            SettingItem(
                "Notifications",
                R.drawable.ic_notification,
                "Configure notifications and alerts",
            ) {
                NotificationSettings(
                    orderUpdates = notificationSettings.orderUpdates,
                    promotionalOffers = notificationSettings.promotionalOffers,
                    newProducts = notificationSettings.newProducts,
                    onOrderUpdatesChange = settingsViewModel::setOrderUpdates,
                    onPromotionalOffersChange = settingsViewModel::setPromotionalOffers,
                    onNewProductsChange = settingsViewModel::setNewProducts
                )
            },
            SettingItem(
                "Language",
                R.drawable.ic_settings,
                "Choose your preferred language",
            ) {
                LanguageSettings(
                    selectedLanguage = language,
                    onLanguageChange = settingsViewModel::setLanguage
                )
            },
            SettingItem(
                "Payment Methods",
                R.drawable.simple_credit_card_outline,
                "Manage your payment options",
            ) {
                PaymentSettings(navController)
            },
            SettingItem(
                "Order History",
                R.drawable.ic_shopping_bag,
                "View your past orders",
            ) {
                OrderHistory(navController)
            },
            SettingItem(
                "About",
                R.drawable.ic_info,
                "App info and legal",
            ) {
                AboutSection(navController)
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
                ExpandableSettingItem(setting)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SignOutButton(
                    onSignOut = {
                        settingsViewModel.signOut()
                        navController.navigate(Route.Welcome) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExpandableSettingItem(setting: SettingItem) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
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
                        painter = painterResource(id = setting.icon),
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
                    painter = painterResource(id = R.drawable.ic_dropdown),
                    contentDescription = if (expanded) "Collapse" else "Expand",
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
private fun AccountSettings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledButton(
            text = "Edit Profile",
            onClick = { navController.navigate(Route.EditProfile) }
        )
        BorderButton(
            text = "Change Password",
            onClick = { navController.navigate(Route.ChangePassword) }
        )
    }
}

@Composable
private fun NotificationSettings(
    orderUpdates: Boolean,
    promotionalOffers: Boolean,
    newProducts: Boolean,
    onOrderUpdatesChange: (Boolean) -> Unit,
    onPromotionalOffersChange: (Boolean) -> Unit,
    onNewProductsChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchSetting("Order Updates", orderUpdates, onOrderUpdatesChange)
        SwitchSetting("Promotional Offers", promotionalOffers, onPromotionalOffersChange)
        SwitchSetting("New Products", newProducts, onNewProductsChange)
    }
}

@Composable
private fun PaymentSettings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledButton(
            text = "Add Payment Method",
            onClick = { /* TODO: Implement payment method addition */ }
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
                .clickable { navController.navigate(Route.BankCardDetails) },
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
                        text = "•••• •••• •••• 1234",
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
private fun OrderHistory(navController: NavController) {
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
        
        // Sample order history items
        listOf(
            "Coffee Mocha - Dec 5, 2024" to "$4.99",
            "Ice Coffee Boba - Dec 3, 2024" to "$6.49",
            "Espresso - Dec 1, 2024" to "$3.49"
        ).forEach { (order, price) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFontFamily,
                        color = SecondaryColor
                    )
                )
                Text(
                    text = price,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        BorderButton(
            text = "View All Orders",
            onClick = { navController.navigate(Route.OrderHistory) }
        )
    }
}

@Composable
private fun AppearanceSettings(
    themeMode: ThemePreferences.ThemeMode,
    isDarkMode: Boolean,
    onThemeModeChange: (ThemePreferences.ThemeMode) -> Unit,
    onToggleDarkMode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dark Mode Quick Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dark Mode",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor
                    )
                )
                Text(
                    text = if (isDarkMode) "Brown-themed dark mode active" else "Light mode active",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PoppinsFontFamily,
                        color = SecondaryColor
                    )
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AccentColor,
                    checkedTrackColor = PrimaryColor,
                    uncheckedThumbColor = SecondaryColor,
                    uncheckedTrackColor = BackgroundColor
                )
            )
        }
        
        // Theme Mode Selector
        Text(
            text = "Theme Mode",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemePreferences.ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeChange(mode) },
                    label = {
                        Text(
                            text = when (mode) {
                                ThemePreferences.ThemeMode.SYSTEM -> "System"
                                ThemePreferences.ThemeMode.LIGHT -> "Light"
                                ThemePreferences.ThemeMode.DARK -> "Dark"
                            },
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PoppinsFontFamily
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryColor,
                        selectedLabelColor = TextColor,
                        containerColor = BackgroundColor,
                        labelColor = PrimaryColor
                    )
                )
            }
        }
    }
}

@Composable
private fun SwitchSetting(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryColor,
                checkedTrackColor = AccentColor
            )
        )
    }
}

@Composable
private fun LanguageSettings(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "ar" to "العربية",
        "fr" to "Français",
        "es" to "Español"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { (code, label) ->
                FilterChip(
                    selected = selectedLanguage == code,
                    onClick = { onLanguageChange(code) },
                    label = {
                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PoppinsFontFamily
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryColor,
                        selectedLabelColor = TextColor,
                        containerColor = BackgroundColor,
                        labelColor = PrimaryColor
                    )
                )
            }
        }
    }
}

@Composable
private fun AboutSection(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Version",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PoppinsFontFamily,
                    color = PrimaryColor
                )
            )
            Text(
                text = "1.0",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PoppinsFontFamily,
                    color = SecondaryColor
                )
            )
        }
        HorizontalDivider(color = SecondaryColor.copy(alpha = 0.3f))
        BorderButton(
            text = "Privacy Policy",
            onClick = { navController.navigate(Route.PrivacyPolicy) }
        )
        BorderButton(
            text = "Terms of Service",
            onClick = { navController.navigate(Route.TermsOfService) }
        )
    }
}

@Composable
private fun SignOutButton(onSignOut: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSignOut() },
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColor
        ),
        border = BorderStroke(1.dp, ErrorColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign Out",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = ErrorColor
                )
            )
        }
    }
}