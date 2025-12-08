package com.example.finjan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-based storage for theme preferences.
 * Persists user's dark mode preference across app restarts.
 */
class ThemePreferences(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
        
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    /**
     * Theme mode options.
     */
    enum class ThemeMode {
        SYSTEM,  // Follow system setting
        LIGHT,   // Always light
        DARK     // Always dark (brown-themed)
    }
    
    /**
     * Flow of the current theme mode.
     */
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(modeString)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }
    
    /**
     * Flow of whether dark mode is explicitly enabled (for backward compatibility).
     */
    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY]
    }
    
    /**
     * Set the theme mode.
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
    
    /**
     * Set dark mode directly (updates theme mode accordingly).
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
            preferences[THEME_MODE_KEY] = if (enabled) ThemeMode.DARK.name else ThemeMode.LIGHT.name
        }
    }
    
    /**
     * Reset to system default.
     */
    suspend fun resetToSystem() {
        context.dataStore.edit { preferences ->
            preferences.remove(DARK_MODE_KEY)
            preferences[THEME_MODE_KEY] = ThemeMode.SYSTEM.name
        }
    }
}
