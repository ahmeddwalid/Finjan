package com.example.finjan.data.repository

import com.example.finjan.data.local.FinjanDatabase
import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.local.entity.FavoriteEntity
import com.example.finjan.data.local.entity.SearchHistoryEntity
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Repository for local database operations.
 * Provides clean API for search history, favorites, and cart management.
 */
class LocalRepository(private val database: FinjanDatabase) {
    
    private val searchHistoryDao = database.searchHistoryDao()
    private val favoritesDao = database.favoritesDao()
    private val cartDao = database.cartDao()
    
    // ==================== Search History ====================
    
    /**
     * Get recent search queries as Flow.
     */
    fun getRecentSearches(limit: Int = 10): Flow<Result<List<SearchHistoryEntity>>> {
        return searchHistoryDao.getRecentSearches(limit)
            .map<List<SearchHistoryEntity>, Result<List<SearchHistoryEntity>>> { Result.Success(it) }
            .catch { emit(Result.Error("Failed to load search history", it)) }
    }
    
    /**
     * Add a search query to history.
     */
    suspend fun addSearchQuery(query: String): Result<Unit> {
        return try {
            val trimmedQuery = query.trim()
            if (trimmedQuery.isBlank()) {
                return Result.Error("Search query cannot be empty")
            }
            
            // Check if query already exists
            if (searchHistoryDao.exists(trimmedQuery)) {
                // Update timestamp by re-inserting
                searchHistoryDao.deleteSearch(trimmedQuery)
            }
            
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = trimmedQuery))
            searchHistoryDao.pruneOldSearches(50) // Keep only last 50
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to save search query", e)
        }
    }
    
    /**
     * Remove a search query from history.
     */
    suspend fun removeSearchQuery(query: String): Result<Unit> {
        return try {
            searchHistoryDao.deleteSearch(query)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove search query", e)
        }
    }
    
    /**
     * Clear all search history.
     */
    suspend fun clearSearchHistory(): Result<Unit> {
        return try {
            searchHistoryDao.clearHistory()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear search history", e)
        }
    }
    
    // ==================== Favorites ====================
    
    /**
     * Get all favorites as Flow.
     */
    fun getAllFavorites(): Flow<Result<List<FavoriteEntity>>> {
        return favoritesDao.getAllFavorites()
            .map<List<FavoriteEntity>, Result<List<FavoriteEntity>>> { Result.Success(it) }
            .catch { emit(Result.Error("Failed to load favorites", it)) }
    }
    
    /**
     * Check if an item is a favorite.
     */
    fun isFavorite(itemId: String): Flow<Boolean> {
        return favoritesDao.isFavorite(itemId)
    }
    
    /**
     * Add item to favorites.
     */
    suspend fun addToFavorites(
        itemId: String,
        title: String,
        description: String,
        imageRes: Int,
        category: String,
        price: Double
    ): Result<Unit> {
        return try {
            val favorite = FavoriteEntity(
                itemId = itemId,
                title = title,
                description = description,
                imageRes = imageRes,
                category = category,
                price = price
            )
            favoritesDao.addFavorite(favorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add to favorites", e)
        }
    }
    
    /**
     * Remove item from favorites.
     */
    suspend fun removeFromFavorites(itemId: String): Result<Unit> {
        return try {
            favoritesDao.removeFavoriteById(itemId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove from favorites", e)
        }
    }
    
    /**
     * Toggle favorite status.
     */
    suspend fun toggleFavorite(
        itemId: String,
        title: String,
        description: String,
        imageRes: Int,
        category: String,
        price: Double
    ): Result<Boolean> {
        return try {
            val isFav = favoritesDao.isFavoriteOnce(itemId)
            if (isFav) {
                favoritesDao.removeFavoriteById(itemId)
                Result.Success(false)
            } else {
                addToFavorites(itemId, title, description, imageRes, category, price)
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error("Failed to toggle favorite", e)
        }
    }
    
    /**
     * Get favorites count.
     */
    fun getFavoritesCount(): Flow<Int> {
        return favoritesDao.getFavoritesCount()
    }
    
    // ==================== Cart ====================
    
    /**
     * Get all cart items as Flow.
     */
    fun getCartItems(): Flow<Result<List<CartItemEntity>>> {
        return cartDao.getAllCartItems()
            .map<List<CartItemEntity>, Result<List<CartItemEntity>>> { Result.Success(it) }
            .catch { emit(Result.Error("Failed to load cart", it)) }
    }
    
    /**
     * Add item to cart or update quantity if exists.
     */
    suspend fun addToCart(
        productId: String,
        name: String,
        price: Double,
        quantity: Int = 1,
        imageRes: Int? = null,
        customizations: String = ""
    ): Result<Unit> {
        return try {
            val existingItem = cartDao.getCartItem(productId)
            if (existingItem != null) {
                // Update quantity
                cartDao.updateQuantity(productId, existingItem.quantity + quantity)
            } else {
                cartDao.insertCartItem(
                    CartItemEntity(
                        productId = productId,
                        name = name,
                        price = price,
                        quantity = quantity,
                        imageRes = imageRes,
                        customizations = customizations
                    )
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add to cart", e)
        }
    }
    
    /**
     * Update cart item quantity.
     */
    suspend fun updateCartItemQuantity(productId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                cartDao.deleteCartItemById(productId)
            } else {
                cartDao.updateQuantity(productId, quantity)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update cart", e)
        }
    }
    
    /**
     * Remove item from cart.
     */
    suspend fun removeFromCart(productId: String): Result<Unit> {
        return try {
            cartDao.deleteCartItemById(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove from cart", e)
        }
    }
    
    /**
     * Clear entire cart.
     */
    suspend fun clearCart(): Result<Unit> {
        return try {
            cartDao.clearCart()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear cart", e)
        }
    }
    
    /**
     * Get cart item count.
     */
    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }
    
    /**
     * Get cart total.
     */
    fun getCartTotal(): Flow<Double> {
        return cartDao.getCartTotal()
            .map { it ?: 0.0 }
    }
}
