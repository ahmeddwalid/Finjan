package com.example.finjan.data.repository

import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.local.entity.FavoriteEntity
import com.example.finjan.data.local.entity.SearchHistoryEntity
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local database operations.
 * Enables testability and decouples ViewModels from concrete implementation.
 */
interface ILocalRepository {

    // ==================== Search History ====================
    fun getRecentSearches(limit: Int = 10): Flow<Result<List<SearchHistoryEntity>>>
    suspend fun addSearchQuery(query: String): Result<Unit>
    suspend fun removeSearchQuery(query: String): Result<Unit>
    suspend fun clearSearchHistory(): Result<Unit>

    // ==================== Favorites ====================
    fun getAllFavorites(): Flow<Result<List<FavoriteEntity>>>
    fun isFavorite(itemId: String): Flow<Boolean>
    suspend fun addToFavorites(
        itemId: String, title: String, description: String,
        imageRes: Int, category: String, price: Double
    ): Result<Unit>
    suspend fun removeFromFavorites(itemId: String): Result<Unit>
    suspend fun toggleFavorite(
        itemId: String, title: String, description: String,
        imageRes: Int, category: String, price: Double
    ): Result<Boolean>
    fun getFavoritesCount(): Flow<Int>

    // ==================== Cart ====================
    fun getCartItems(): Flow<Result<List<CartItemEntity>>>
    suspend fun addToCart(
        productId: String, name: String, price: Double,
        quantity: Int = 1, imageRes: Int? = null, customizations: String = ""
    ): Result<Unit>
    suspend fun updateCartItemQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(productId: String): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    fun getCartItemCount(): Flow<Int>
    fun getCartTotal(): Flow<Double>
}
