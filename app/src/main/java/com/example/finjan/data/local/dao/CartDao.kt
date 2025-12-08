package com.example.finjan.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finjan.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for cart operations.
 */
@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart_items ORDER BY name ASC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>
    
    @Query("SELECT * FROM cart_items ORDER BY name ASC")
    suspend fun getAllCartItemsOnce(): List<CartItemEntity>
    
    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItem(productId: String): CartItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)
    
    @Update
    suspend fun updateCartItem(item: CartItemEntity)
    
    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)
    
    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItemById(productId: String)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
    
    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getCartTotal(): Flow<Double?>
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)
}
