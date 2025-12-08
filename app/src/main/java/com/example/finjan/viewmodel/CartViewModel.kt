package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.repository.LocalRepository
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for cart management.
 */
class CartViewModel(private val localRepository: LocalRepository) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems.asStateFlow()
    
    val cartTotal: StateFlow<Double> = localRepository.getCartTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val cartItemCount: StateFlow<Int> = localRepository.getCartItemCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    init {
        loadCartItems()
    }
    
    private fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            localRepository.getCartItems().collect { result ->
                _isLoading.value = false
                when (result) {
                    is Result.Success -> {
                        _cartItems.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception?.message ?: result.message
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    fun addToCart(
        productId: String,
        name: String,
        price: Double,
        quantity: Int = 1,
        imageRes: Int? = null,
        customizations: String = ""
    ) {
        viewModelScope.launch {
            when (val result = localRepository.addToCart(
                productId, name, price, quantity, imageRes, customizations
            )) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            when (val result = localRepository.updateCartItemQuantity(productId, quantity)) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            when (val result = localRepository.removeFromCart(productId)) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            when (val result = localRepository.clearCart()) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

/**
 * Cart UI state for composables.
 */
data class CartUiState(
    val items: List<CartItemEntity> = emptyList(),
    val total: Double = 0.0,
    val itemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = items.isEmpty()
    val canCheckout: Boolean get() = items.isNotEmpty() && !isLoading
}
