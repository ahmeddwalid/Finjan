package com.example.finjan.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finjan.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for favorites operations.
 */
@Dao
interface FavoritesDao {
    
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    suspend fun getAllFavoritesOnce(): List<FavoriteEntity>
    
    @Query("SELECT * FROM favorites WHERE itemId = :itemId")
    suspend fun getFavorite(itemId: String): FavoriteEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemId = :itemId)")
    fun isFavorite(itemId: String): Flow<Boolean>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemId = :itemId)")
    suspend fun isFavoriteOnce(itemId: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)
    
    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE itemId = :itemId")
    suspend fun removeFavoriteById(itemId: String)
    
    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()
    
    @Query("SELECT COUNT(*) FROM favorites")
    fun getFavoritesCount(): Flow<Int>
}
