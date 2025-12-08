package com.example.finjan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.ThemePreferences
import com.example.finjan.data.local.ThemePreferences.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing theme settings.
 * Provides observable theme state and methods to update theme preferences.
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val themePreferences = ThemePreferences(application)
    
    /**
     * Current theme mode as observable state.
     */
    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )
    
    /**
     * Set theme mode.
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }
    
    /**
     * Toggle between light and dark mode.
     * If currently on SYSTEM, switches to the opposite of what system would show.
     */
    fun toggleDarkMode(currentlyDark: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(!currentlyDark)
        }
    }
    
    /**
     * Reset theme to follow system setting.
     */
    fun resetToSystem() {
        viewModelScope.launch {
            themePreferences.resetToSystem()
        }
    }
}
