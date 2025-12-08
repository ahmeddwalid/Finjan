package com.example.finjan.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finjan.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for search history operations.
 */
@Dao
interface SearchHistoryDao {
    
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryEntity>>
    
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    suspend fun getAllSearches(): List<SearchHistoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)
    
    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)
    
    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
    
    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY timestamp DESC LIMIT :keepCount)")
    suspend fun pruneOldSearches(keepCount: Int = 50)
    
    @Query("SELECT EXISTS(SELECT 1 FROM search_history WHERE query = :query)")
    suspend fun exists(query: String): Boolean
}
