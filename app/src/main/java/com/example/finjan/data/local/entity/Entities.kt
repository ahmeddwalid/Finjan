package com.example.finjan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing search history locally.
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Entity for storing favorite menu items.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val itemId: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val category: String,
    val price: Double,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Entity for caching menu items locally.
 */
@Entity(tableName = "menu_items_cache")
data class MenuItemCacheEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Entity for storing cart items.
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageRes: Int?,
    val customizations: String = "" // JSON string of customizations
)

/**
 * Entity for storing pending orders (offline support).
 */
@Entity(tableName = "pending_orders")
data class PendingOrderEntity(
    @PrimaryKey
    val orderId: String,
    val orderJson: String, // Full order as JSON
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.PENDING.name
)

enum class SyncStatus {
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}
