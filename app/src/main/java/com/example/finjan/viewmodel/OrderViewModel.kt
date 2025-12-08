package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.model.Order
import com.example.finjan.data.model.OrderItem
import com.example.finjan.data.model.OrderStatus
import com.example.finjan.data.repository.FirestoreRepository
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.util.Date

/**
 * ViewModel for order management and tracking.
 */
class OrderViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced.asStateFlow()
    
    /**
     * Load all orders for a user.
     */
    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = firestoreRepository.getOrderHistory()) {
                is Result.Success -> {
                    _orders.value = result.data.sortedByDescending { it.createdAt }
                    _error.value = null
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.exception?.message ?: result.message
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Load a specific order.
     */
    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = firestoreRepository.getOrder(orderId)) {
                is Result.Success -> {
                    _currentOrder.value = result.data
                    _isLoading.value = false
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception?.message ?: result.message
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Place a new order.
     */
    fun placeOrder(
        userId: String,
        items: List<OrderItem>,
        subtotal: Double,
        deliveryFee: Double = 0.0,
        discount: Double = 0.0,
        paymentMethodId: String? = null,
        specialInstructions: String? = null,
        deliveryAddress: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val order = Order(
                userId = userId,
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                discount = discount,
                total = subtotal + deliveryFee - discount,
                status = OrderStatus.PENDING.name,
                createdAt = Timestamp(Date()),
                paymentMethod = paymentMethodId ?: "",
                specialInstructions = specialInstructions,
                deliveryAddress = deliveryAddress
            )
            
            when (val result = firestoreRepository.createOrder(order)) {
                is Result.Success -> {
                    _currentOrder.value = order.copy(id = result.data)
                    _orderPlaced.value = true
                    _isLoading.value = false
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = "Failed to place order: ${result.exception?.message ?: result.message}"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Cancel an order.
     */
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = firestoreRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)) {
                is Result.Success -> {
                    // Update local state
                    _currentOrder.value = _currentOrder.value?.copy(status = OrderStatus.CANCELLED.name)
                    _orders.value = _orders.value.map { 
                        if (it.id == orderId) it.copy(status = OrderStatus.CANCELLED.name) else it 
                    }
                    _isLoading.value = false
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = "Failed to cancel order: ${result.exception?.message ?: result.message}"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Observe order status in real-time.
     */
    fun observeOrderStatus(orderId: String) {
        viewModelScope.launch {
            firestoreRepository.observeOrder(orderId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _currentOrder.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception?.message ?: result.message
                    }
                    is Result.Loading -> { /* no-op */ }
                }
            }
        }
    }
    
    fun resetOrderPlaced() {
        _orderPlaced.value = false
    }
    
    fun clearError() {
        _error.value = null
    }
}

/**
 * Order tracking step for UI.
 */
data class OrderTrackingStep(
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isActive: Boolean,
    val timestamp: Date? = null
)

/**
 * Convert order status to tracking steps.
 */
fun OrderStatus.toTrackingSteps(): List<OrderTrackingStep> {
    val steps = listOf(
        OrderTrackingStep(
            title = "Order Placed",
            description = "We've received your order",
            isCompleted = this.ordinal >= OrderStatus.PENDING.ordinal,
            isActive = this == OrderStatus.PENDING
        ),
        OrderTrackingStep(
            title = "Confirmed",
            description = "Your order has been confirmed",
            isCompleted = this.ordinal >= OrderStatus.CONFIRMED.ordinal,
            isActive = this == OrderStatus.CONFIRMED
        ),
        OrderTrackingStep(
            title = "Preparing",
            description = "Our barista is preparing your order",
            isCompleted = this.ordinal >= OrderStatus.PREPARING.ordinal,
            isActive = this == OrderStatus.PREPARING
        ),
        OrderTrackingStep(
            title = "Ready",
            description = "Your order is ready for pickup",
            isCompleted = this.ordinal >= OrderStatus.READY.ordinal,
            isActive = this == OrderStatus.READY
        ),
        OrderTrackingStep(
            title = "Completed",
            description = "Enjoy your coffee!",
            isCompleted = this == OrderStatus.COMPLETED,
            isActive = this == OrderStatus.COMPLETED
        )
    )
    
    return if (this == OrderStatus.CANCELLED) {
        steps.map { it.copy(isActive = false, isCompleted = false) }
    } else {
        steps
    }
}
