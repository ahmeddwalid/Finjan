package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Shared ViewModel for cross-screen state management.
 * Handles user data and app-wide state.
 * Cart operations are handled by CartViewModel backed by Room.
 */
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    // User state
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl.asStateFlow()

    // Points/Loyalty state
    private val _loyaltyPoints = MutableStateFlow(0)
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Load user data from Firebase Auth.
     */
    fun loadUserData() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: user?.email?.substringBefore("@") ?: ""
        _userEmail.value = user?.email ?: ""
        _userPhotoUrl.value = user?.photoUrl?.toString()
    }

    /**
     * Add loyalty points.
     */
    fun addPoints(points: Int) {
        _loyaltyPoints.value += points
    }

    /**
     * Redeem loyalty points.
     */
    fun redeemPoints(points: Int): Boolean {
        return if (_loyaltyPoints.value >= points) {
            _loyaltyPoints.value -= points
            true
        } else {
            false
        }
    }
}