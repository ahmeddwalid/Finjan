package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.model.PaymentMethod
import com.example.finjan.data.model.PaymentType
import com.example.finjan.data.repository.IFirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Add Payment Method screen.
 * Handles payment method validation and storage.
 */
@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val firestoreRepository: IFirestoreRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()
    
    /**
     * Add a new payment method.
     * In a real app, this would integrate with Stripe SDK.
     */
    fun addPaymentMethod(
        cardNumber: String,
        cardHolderName: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvv: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Validate card number (basic Luhn check would go here)
                if (cardNumber.length < 16) {
                    _error.value = "Invalid card number"
                    _isLoading.value = false
                    return@launch
                }
                
                // Validate expiry
                if (expiryMonth !in 1..12) {
                    _error.value = "Invalid expiry month"
                    _isLoading.value = false
                    return@launch
                }
                
                // Validate CVV
                if (cvv.length < 3) {
                    _error.value = "Invalid CVV"
                    _isLoading.value = false
                    return@launch
                }
                
                // Detect card brand
                val brand = when {
                    cardNumber.startsWith("4") -> "Visa"
                    cardNumber.startsWith("5") -> "Mastercard"
                    cardNumber.startsWith("3") -> "American Express"
                    cardNumber.startsWith("6") -> "Discover"
                    else -> "Card"
                }
                
                // In a real app, you would:
                // 1. Send card details to Stripe to create a PaymentMethod
                // 2. Store only the Stripe PaymentMethod ID and last 4 digits
                
                val userId = auth.currentUser?.uid ?: run {
                    _error.value = "Please sign in to add a payment method"
                    _isLoading.value = false
                    return@launch
                }
                
                val paymentMethod = PaymentMethod(
                    userId = userId,
                    type = PaymentType.CARD.name,
                    lastFour = cardNumber.takeLast(4),
                    brand = brand,
                    expiryMonth = expiryMonth,
                    expiryYear = 2000 + expiryYear,
                    isDefault = true
                )
                
                firestoreRepository.addPaymentMethod(paymentMethod)
                _success.value = true
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add payment method"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Reset success state.
     */
    fun resetSuccess() {
        _success.value = false
    }
}
