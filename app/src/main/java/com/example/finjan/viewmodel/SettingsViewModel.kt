package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {

    val notificationSettings: StateFlow<UserPreferences.NotificationSettings> =
        userPreferences.notificationSettings
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserPreferences.NotificationSettings()
            )

    val language: StateFlow<String> = userPreferences.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    fun setOrderUpdates(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setOrderUpdates(enabled) }
    }

    fun setPromotionalOffers(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setPromotionalOffers(enabled) }
    }

    fun setNewProducts(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setNewProducts(enabled) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { userPreferences.setLanguage(language) }
    }

    fun signOut() {
        auth.signOut()
    }

    val userEmail: String?
        get() = auth.currentUser?.email

    val userName: String?
        get() = auth.currentUser?.displayName
}
