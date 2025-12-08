package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for cross-screen state management.
 * Handles user data, cart, and app-wide state.
 */
class SharedViewModel : ViewModel() {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // User state
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl.asStateFlow()

    // Cart state
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()

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
     * Add item to cart.
     */
    fun addToCart(item: CartItem) {
        val existingItem = _cartItems.value.find { it.productId == item.productId }
        if (existingItem != null) {
            _cartItems.value = _cartItems.value.map {
                if (it.productId == item.productId) {
                    it.copy(quantity = it.quantity + 1)
                } else it
            }
        } else {
            _cartItems.value = _cartItems.value + item
        }
        updateCartTotal()
    }

    /**
     * Remove item from cart.
     */
    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.productId == productId }
        updateCartTotal()
    }

    /**
     * Update cart item quantity.
     */
    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
        } else {
            _cartItems.value = _cartItems.value.map {
                if (it.productId == productId) it.copy(quantity = quantity) else it
            }
            updateCartTotal()
        }
    }

    /**
     * Clear all cart items.
     */
    fun clearCart() {
        _cartItems.value = emptyList()
        _cartTotal.value = 0.0
    }

    /**
     * Update cart total.
     */
    private fun updateCartTotal() {
        _cartTotal.value = _cartItems.value.sumOf { it.price * it.quantity }
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

/**
 * Data class for cart items.
 */
data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val imageRes: Int? = null
)