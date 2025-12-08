package com.example.finjan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.FinjanDatabase
import com.example.finjan.data.model.MenuItem
import com.example.finjan.data.repository.LocalRepository
import com.example.finjan.FinjanApplication
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Product Details screen.
 * Handles product loading, favorites, and cart operations.
 */
class ProductDetailsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val localRepository: LocalRepository = LocalRepository(
        FinjanDatabase.getInstance(application)
    )
    
    private val firestoreRepository = FinjanApplication.getInstance().firestoreRepository
    
    private val _product = MutableStateFlow<MenuItem?>(null)
    val product: StateFlow<MenuItem?> = _product.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _addedToCart = MutableStateFlow(false)
    val addedToCart: StateFlow<Boolean> = _addedToCart.asStateFlow()
    
    /**
     * Load product details.
     */
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // For now, create a mock product - in production, fetch from Firestore
            _product.value = MenuItem(
                id = productId,
                title = "Premium Coffee",
                description = "A rich and smooth blend of arabica beans",
                category = "Coffee",
                price = 4.99,
                isAvailable = true,
                isFeatured = true
            )
            
            _isLoading.value = false
            
            // Check favorite status
            checkFavoriteStatus(productId)
        }
    }
    
    /**
     * Check if product is in favorites.
     */
    private fun checkFavoriteStatus(productId: String) {
        viewModelScope.launch {
            localRepository.isFavorite(productId).collect { isFav ->
                _isFavorite.value = isFav
            }
        }
    }
    
    /**
     * Toggle favorite status.
     */
    fun toggleFavorite() {
        val currentProduct = _product.value ?: return
        
        viewModelScope.launch {
            val result = localRepository.toggleFavorite(
                itemId = currentProduct.id,
                title = currentProduct.title,
                description = currentProduct.description,
                imageRes = 0, // No local image resource
                category = currentProduct.category,
                price = currentProduct.price
            )
            
            when (result) {
                is Result.Success -> {
                    _isFavorite.value = result.data
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> { /* Ignored */ }
            }
        }
    }
    
    /**
     * Add current product to cart with customizations.
     */
    fun addToCart(
        quantity: Int,
        size: String,
        milk: String,
        sweetness: String
    ) {
        val currentProduct = _product.value ?: return
        
        // Calculate price based on size
        val sizeMultiplier = when (size) {
            "Large" -> 1.3
            "Medium" -> 1.15
            else -> 1.0
        }
        val finalPrice = currentProduct.price * sizeMultiplier
        
        // Build customization string
        val customizations = listOf(
            "Size: $size",
            "Milk: $milk",
            "Sweetness: $sweetness"
        ).joinToString(", ")
        
        viewModelScope.launch {
            val result = localRepository.addToCart(
                productId = currentProduct.id,
                name = currentProduct.title,
                price = finalPrice,
                quantity = quantity,
                imageRes = null,
                customizations = customizations
            )
            
            when (result) {
                is Result.Success -> {
                    _addedToCart.value = true
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> { /* Ignored */ }
            }
        }
    }
    
    /**
     * Reset added to cart state.
     */
    fun resetAddedToCart() {
        _addedToCart.value = false
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
}
