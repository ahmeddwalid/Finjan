package com.example.finjan.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// LIGHT THEME COLORS
// ============================================

// Primary Color - Rich coffee brown
val PrimaryColor = Color(0xFF493628)  // #493628

// Secondary Color - Light caramel
val SecondaryColor = Color(0xFFDFA878)  // #dfa878

// Accent Color - Golden brown
val AccentColor = Color(0xFFA17738)    // #a17738

// Background Color for Light Theme - Warm cream
val BackgroundColor = Color(0xFFD6C8B3)  // #d6c0b3

// Surface Color for Light Theme - Slightly lighter cream
val SurfaceColor = Color(0xFFE8DCD0)  // #E8DCD0

// Text Colors for Light Theme
val TextColor = Color(0xFF87C498)        // #87c498 - Green accent text
val TextPrimaryLight = Color(0xFF2D1F14)  // Dark brown for primary text
val TextSecondaryLight = Color(0xFF5D4A3C)  // Medium brown for secondary text

// ============================================
// DARK THEME COLORS (Brown-based, not black/grey)
// ============================================

// Dark Primary Color - Deep espresso brown
val DarkPrimaryColor = Color(0xFF2D1F14)  // #2D1F14

// Dark Secondary Color - Dark caramel
val DarkSecondaryColor = Color(0xFF8B5A2B)  // #8B5A2B

// Dark Accent Color - Warm amber
val DarkAccentColor = Color(0xFFD4A574)  // #D4A574

// Background Color for Dark Theme - Deep mocha
val DarkBackgroundColor = Color(0xFF1A120D)  // #1A120D

// Surface Color for Dark Theme - Slightly lighter mocha
val DarkSurfaceColor = Color(0xFF2A1E16)  // #2A1E16

// Card/Container Color for Dark Theme
val DarkCardColor = Color(0xFF3D2B1F)  // #3D2B1F

// Text Colors for Dark Theme
val DarkTextPrimary = Color(0xFFE8DCD0)  // Warm cream
val DarkTextSecondary = Color(0xFFC4B8A8)  // Light beige
val DarkTextAccent = Color(0xFFA8D5BA)  // Soft green (matches light theme)

// ============================================
// SEMANTIC COLORS (Same for both themes)
// ============================================

// Success - Soft green
val SuccessColor = Color(0xFF4CAF50)
val SuccessColorDark = Color(0xFF81C784)

// Error - Warm red (not harsh)
val ErrorColor = Color(0xFFB85450)
val ErrorColorDark = Color(0xFFE57373)

// Warning - Golden
val WarningColor = Color(0xFFD4A574)
val WarningColorDark = Color(0xFFFFB74D)

// Info - Coffee blue
val InfoColor = Color(0xFF6B8E9E)
val InfoColorDark = Color(0xFF90CAF9)

// ============================================
// LEGACY ALIASES (for backward compatibility)
// ============================================

val DarkBrown = PrimaryColor
val LightBrown = SecondaryColor
val MediumBrown = AccentColor

// ============================================
// SHIMMER/SKELETON LOADING COLORS
// ============================================

val ShimmerBaseLight = Color(0xFFD6C8B3)
val ShimmerHighlightLight = Color(0xFFE8DCD0)
val ShimmerBaseDark = Color(0xFF2A1E16)
val ShimmerHighlightDark = Color(0xFF3D2B1F)
