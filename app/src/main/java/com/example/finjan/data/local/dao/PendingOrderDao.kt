package com.example.finjan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finjan.data.local.entity.PendingOrderEntity

@Dao
interface PendingOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: PendingOrderEntity)

    @Update
    suspend fun update(order: PendingOrderEntity)

    @Query("SELECT * FROM pending_orders WHERE syncStatus = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: String): List<PendingOrderEntity>

    @Query("SELECT * FROM pending_orders WHERE syncStatus IN ('PENDING', 'FAILED') ORDER BY createdAt ASC")
    suspend fun getPendingOrders(): List<PendingOrderEntity>

    @Query("DELETE FROM pending_orders WHERE orderId = :orderId")
    suspend fun delete(orderId: String)

    @Query("DELETE FROM pending_orders WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()
}
