package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.model.Order
import com.example.finjan.data.model.OrderItem
import com.example.finjan.data.model.OrderStatus
import com.example.finjan.data.repository.IFirestoreRepository
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.data.repository.IPaymentRepository
import com.example.finjan.utils.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PaymentState {
    data object Idle : PaymentState
    data object Loading : PaymentState
    data class Ready(val clientSecret: String) : PaymentState
    data class Success(val orderId: String) : PaymentState
    data class Error(val message: String) : PaymentState
}

/**
 * ViewModel for Checkout screen.
 * Handles order placement, cart management, and Stripe payment flow.
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val localRepository: ILocalRepository,
    private val firestoreRepository: IFirestoreRepository,
    private val paymentRepository: IPaymentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems.asStateFlow()
    
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _orderSuccess = MutableStateFlow<String?>(null)
    val orderSuccess: StateFlow<String?> = _orderSuccess.asStateFlow()
    
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    init {
        loadCartItems()
    }
    
    private fun loadCartItems() {
        viewModelScope.launch {
            localRepository.getCartItems().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _cartItems.value = result.data
                        _subtotal.value = result.data.sumOf { it.price * it.quantity }
                    }
                    is Result.Error -> {
                        _error.value = result.message
                    }
                    is Result.Loading -> { /* Ignored */ }
                }
            }
        }
    }
    
    /**
     * Place an order with the current cart items.
     */
    fun placeOrder(
        paymentMethod: String,
        pickupTime: String,
        total: Double
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "Please sign in to place an order"
            return
        }
        
        if (_cartItems.value.isEmpty()) {
            _error.value = "Your cart is empty"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Convert cart items to order items
                val orderItems = _cartItems.value.map { cartItem ->
                    OrderItem(
                        menuItemId = cartItem.productId,
                        name = cartItem.name,
                        quantity = cartItem.quantity,
                        price = cartItem.price,
                        customizations = if (cartItem.customizations.isNotEmpty()) {
                            cartItem.customizations.split(",").map { it.trim() }
                        } else {
                            emptyList()
                        }
                    )
                }
                
                // Create order
                val order = Order(
                    userId = userId,
                    items = orderItems,
                    total = total,
                    status = OrderStatus.PENDING.name,
                    paymentMethod = paymentMethod,
                    specialInstructions = "Pickup: $pickupTime"
                )
                
                // Save to Firestore
                val result = firestoreRepository.createOrder(order)
                
                when (result) {
                    is Result.Success -> {
                        // Clear cart on success
                        localRepository.clearCart()
                        _orderSuccess.value = result.data
                    }
                    is Result.Error -> {
                        _error.value = result.message
                    }
                    is Result.Loading -> { /* Ignored */ }
                }
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to place order"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Start the Stripe payment flow by requesting a PaymentIntent client secret.
     */
    fun startPayment(amountCents: Long) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            when (val result = paymentRepository.createPaymentIntent(amountCents, "usd")) {
                is Result.Success -> _paymentState.value = PaymentState.Ready(result.data)
                is Result.Error -> _paymentState.value = PaymentState.Error(result.message)
                is Result.Loading -> { /* Ignored */ }
            }
        }
    }

    /**
     * Called when the Stripe PaymentSheet completes successfully.
     */
    fun onPaymentSuccess(paymentMethod: String, pickupTime: String, total: Double) {
        placeOrder(paymentMethod, pickupTime, total)
    }

    /**
     * Called when the Stripe PaymentSheet fails or is cancelled.
     */
    fun onPaymentFailed(message: String) {
        _paymentState.value = PaymentState.Error(message)
    }

    /**
     * Reset payment state back to idle.
     */
    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
}
