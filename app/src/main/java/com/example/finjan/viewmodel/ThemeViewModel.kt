package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.ThemePreferences
import com.example.finjan.data.local.ThemePreferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing theme settings.
 * Provides observable theme state and methods to update theme preferences.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {
    
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
