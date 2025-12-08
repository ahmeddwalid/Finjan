package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.FinjanApplication
import com.example.finjan.data.model.Order
import com.example.finjan.data.repository.FirestoreRepository
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Order History screen.
 * Handles fetching and managing past orders.
 */
class OrderHistoryViewModel : ViewModel() {
    
    private val firestoreRepository: FirestoreRepository = 
        FinjanApplication.getInstance().firestoreRepository
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadOrders()
    }
    
    /**
     * Load order history from Firestore.
     */
    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = firestoreRepository.getOrderHistory()) {
                is Result.Success -> {
                    _orders.value = result.data
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> { /* Ignored */ }
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Refresh order history.
     */
    fun refresh() {
        loadOrders()
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
}
