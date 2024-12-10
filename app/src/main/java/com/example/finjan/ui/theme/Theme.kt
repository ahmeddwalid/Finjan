package com.example.finjan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,           // #493628
    secondary = SecondaryColor,       // #dfa878
    tertiary = AccentColor,           // #a17738
    background = DarkBackgroundColor, // #1B100E
    surface = DarkBackgroundColor,    // #1B100E
    onPrimary = TextColor,            // #87c498
    onSecondary = TextColor,          // #87c498
    onBackground = TextColor,         // #87c498
    onSurface = TextColor             // #87c498
)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,           // #493628
    secondary = SecondaryColor,       // #dfa878
    tertiary = AccentColor,           // #a17738
    background = BackgroundColor,     // #d6c0b3
    surface = BackgroundColor,        // #d6c0b3
    onPrimary = TextColor,            // #87c498
    onSecondary = TextColor,          // #87c498
    onTertiary = TextColor,           // #87c498
    onBackground = TextColor,         // #87c498
    onSurface = TextColor             // #87c498
)

@Composable
fun FinjanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
