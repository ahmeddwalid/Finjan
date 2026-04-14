package com.example.finjan.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finjan.data.local.dao.PendingOrderDao
import com.example.finjan.data.local.entity.SyncStatus
import com.example.finjan.data.model.Order
import com.example.finjan.data.repository.IFirestoreRepository
import com.example.finjan.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

/**
 * WorkManager worker that syncs pending orders to Firestore.
 * Retries failed orders with exponential backoff handled by WorkManager.
 */
@HiltWorker
class OrderSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingOrderDao: PendingOrderDao,
    private val firestoreRepository: IFirestoreRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting order sync...")

        val pendingOrders = pendingOrderDao.getPendingOrders()
        if (pendingOrders.isEmpty()) {
            Log.d(TAG, "No pending orders to sync")
            return Result.success()
        }

        var allSuccess = true

        for (pendingOrder in pendingOrders) {
            try {
                // Mark as syncing
                pendingOrderDao.update(
                    pendingOrder.copy(syncStatus = SyncStatus.SYNCING.name)
                )

                val order = Json.decodeFromString<Order>(pendingOrder.orderJson)
                when (val result = firestoreRepository.createOrder(order)) {
                    is com.example.finjan.utils.Result.Success -> {
                        pendingOrderDao.update(
                            pendingOrder.copy(syncStatus = SyncStatus.SYNCED.name)
                        )
                        Log.d(TAG, "Synced order ${pendingOrder.orderId}")
                    }
                    is com.example.finjan.utils.Result.Error -> {
                        pendingOrderDao.update(
                            pendingOrder.copy(syncStatus = SyncStatus.FAILED.name)
                        )
                        allSuccess = false
                        Log.w(TAG, "Failed to sync order ${pendingOrder.orderId}: ${result.message}")
                    }
                    is com.example.finjan.utils.Result.Loading -> { /* no-op */ }
                }
            } catch (e: Exception) {
                pendingOrderDao.update(
                    pendingOrder.copy(syncStatus = SyncStatus.FAILED.name)
                )
                allSuccess = false
                Log.e(TAG, "Error syncing order ${pendingOrder.orderId}", e)
            }
        }

        // Clean up successfully synced orders
        pendingOrderDao.deleteSynced()

        return if (allSuccess) Result.success() else Result.retry()
    }

    companion object {
        const val TAG = "OrderSyncWorker"
        const val WORK_NAME = "order_sync"
    }
}
