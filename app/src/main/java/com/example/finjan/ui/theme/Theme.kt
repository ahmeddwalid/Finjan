package com.example.finjan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark theme color scheme using brown tones (espresso, mocha, caramel).
 * Intentionally NOT using black or grey - maintaining coffee shop aesthetic.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentColor,           // Warm amber for primary actions
    onPrimary = DarkTextPrimary,         // Cream text on primary
    primaryContainer = DarkCardColor,    // Brown card background
    onPrimaryContainer = DarkTextPrimary,
    
    secondary = DarkSecondaryColor,      // Dark caramel
    onSecondary = DarkTextPrimary,
    secondaryContainer = DarkSurfaceColor,
    onSecondaryContainer = DarkTextSecondary,
    
    tertiary = DarkTextAccent,           // Soft green accent
    onTertiary = DarkPrimaryColor,
    
    background = DarkBackgroundColor,    // Deep mocha (#1A120D)
    onBackground = DarkTextPrimary,      // Warm cream text
    
    surface = DarkSurfaceColor,          // Slightly lighter mocha (#2A1E16)
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardColor,      // Card surfaces (#3D2B1F)
    onSurfaceVariant = DarkTextSecondary,
    
    error = ErrorColorDark,
    onError = DarkPrimaryColor,
    
    outline = DarkSecondaryColor,
    outlineVariant = DarkCardColor
)

/**
 * Light theme color scheme using warm cream and brown tones.
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,              // Rich coffee brown (#493628)
    onPrimary = SurfaceColor,            // Light cream on primary
    primaryContainer = SecondaryColor,   // Light caramel container
    onPrimaryContainer = PrimaryColor,
    
    secondary = SecondaryColor,          // Light caramel (#DFA878)
    onSecondary = PrimaryColor,
    secondaryContainer = BackgroundColor,
    onSecondaryContainer = TextPrimaryLight,
    
    tertiary = AccentColor,              // Golden brown (#A17738)
    onTertiary = SurfaceColor,
    
    background = BackgroundColor,        // Warm cream (#D6C8B3)
    onBackground = TextPrimaryLight,     // Dark brown text
    
    surface = SurfaceColor,              // Lighter cream (#E8DCD0)
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundColor,
    onSurfaceVariant = TextSecondaryLight,
    
    error = ErrorColor,
    onError = SurfaceColor,
    
    outline = AccentColor,
    outlineVariant = SecondaryColor
)

/**
 * Finjan app theme with proper dark mode support.
 *
 * @param darkTheme Whether to use dark theme (defaults to system setting)
 * @param dynamicColor Whether to use Material You dynamic colors (disabled by default 
 *                     to maintain consistent coffee shop branding)
 * @param content The composable content to theme
 */
@Composable
fun FinjanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to maintain coffee shop branding
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
    
    // Update system bars to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Provides theme-aware colors for components that need direct color access.
 */
object FinjanColors {
    @Composable
    fun background() = MaterialTheme.colorScheme.background
    
    @Composable
    fun surface() = MaterialTheme.colorScheme.surface
    
    @Composable
    fun primary() = MaterialTheme.colorScheme.primary
    
    @Composable
    fun onPrimary() = MaterialTheme.colorScheme.onPrimary
    
    @Composable
    fun textPrimary() = MaterialTheme.colorScheme.onBackground
    
    @Composable
    fun textSecondary() = MaterialTheme.colorScheme.onSurfaceVariant
    
    @Composable
    fun cardBackground() = MaterialTheme.colorScheme.surfaceVariant
}
