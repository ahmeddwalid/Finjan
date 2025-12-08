package com.example.finjan.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore extension
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manager for app-wide theme and settings preferences.
 * Uses DataStore for persistent storage.
 */
class ThemeManager(private val context: Context) {
    
    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        private val KEY_LOCALE = stringPreferencesKey("locale")
        private val KEY_PRIMARY_COLOR = stringPreferencesKey("primary_color")
    }
    
    /**
     * Get dark mode preference as Flow.
     */
    val isDarkMode: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: false
    }
    
    /**
     * Get system theme preference as Flow.
     */
    val useSystemTheme: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_SYSTEM_THEME] ?: true
    }
    
    /**
     * Get locale preference as Flow.
     */
    val locale: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_LOCALE] ?: "en"
    }
    
    /**
     * Get primary color as Flow.
     */
    val primaryColor: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_PRIMARY_COLOR] ?: "brown"
    }
    
    /**
     * Combined theme settings for easy observation.
     */
    val themeSettings: Flow<ThemeSettings> = context.settingsDataStore.data.map { preferences ->
        ThemeSettings(
            isDarkMode = preferences[KEY_DARK_MODE] ?: false,
            useSystemTheme = preferences[KEY_SYSTEM_THEME] ?: true,
            locale = preferences[KEY_LOCALE] ?: "en",
            primaryColor = preferences[KEY_PRIMARY_COLOR] ?: "brown"
        )
    }
    
    /**
     * Set dark mode.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
            preferences[KEY_SYSTEM_THEME] = false // Disable system theme when manually set
        }
    }
    
    /**
     * Set whether to use system theme.
     */
    suspend fun setUseSystemTheme(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_SYSTEM_THEME] = enabled
        }
    }
    
    /**
     * Set locale.
     */
    suspend fun setLocale(locale: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_LOCALE] = locale
        }
    }
    
    /**
     * Set primary color theme.
     */
    suspend fun setPrimaryColor(color: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_PRIMARY_COLOR] = color
        }
    }
    
    /**
     * Toggle dark mode.
     */
    suspend fun toggleDarkMode() {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[KEY_DARK_MODE] ?: false
            preferences[KEY_DARK_MODE] = !current
            preferences[KEY_SYSTEM_THEME] = false
        }
    }
    
    /**
     * Reset all theme settings to defaults.
     */
    suspend fun resetToDefaults() {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = false
            preferences[KEY_SYSTEM_THEME] = true
            preferences[KEY_LOCALE] = "en"
            preferences[KEY_PRIMARY_COLOR] = "brown"
        }
    }
}

/**
 * Data class representing theme settings.
 */
data class ThemeSettings(
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val locale: String = "en",
    val primaryColor: String = "brown"
) {
    val isRtl: Boolean get() = locale == "ar"
}

/**
 * Available app locales.
 */
enum class AppLocale(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    ARABIC("ar", "Arabic", "العربية")
}

/**
 * Available theme colors.
 */
enum class ThemeColor(val code: String, val displayName: String) {
    BROWN("brown", "Coffee Brown"),
    DARK("dark", "Dark Espresso"),
    CREAM("cream", "Cream"),
    GREEN("green", "Matcha Green")
}
