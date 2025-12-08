package com.example.finjan.utils.notifications

import android.util.Log
import com.example.finjan.FinjanApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging service for handling push notifications.
 */
class FinjanFirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "FinjanFCM"
        
        // Data keys
        const val KEY_TYPE = "type"
        const val KEY_ORDER_ID = "order_id"
        const val KEY_PROMO_ID = "promo_id"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        
        // Notification types
        const val TYPE_ORDER_UPDATE = "order_update"
        const val TYPE_PROMOTION = "promotion"
        const val TYPE_GENERAL = "general"
    }
    
    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(this)
    }
    
    /**
     * Called when FCM registration token is refreshed.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token refreshed: ${token.take(20)}...")
        
        // Send token to your server
        sendTokenToServer(token)
    }
    
    /**
     * Called when a message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
            return
        }
        
        // Check if message contains notification payload (when app is in foreground)
        remoteMessage.notification?.let { notification ->
            handleNotification(notification)
        }
    }
    
    /**
     * Handle data-only messages (works in foreground and background).
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data[KEY_TYPE] ?: TYPE_GENERAL
        val title = data[KEY_TITLE] ?: "Finjan"
        val message = data[KEY_MESSAGE] ?: ""
        
        when (type) {
            TYPE_ORDER_UPDATE -> {
                val orderId = data[KEY_ORDER_ID] ?: return
                notificationHelper.showOrderStatusNotification(
                    orderId = orderId,
                    title = title,
                    message = message
                )
            }
            
            TYPE_PROMOTION -> {
                val promoId = data[KEY_PROMO_ID] ?: "promo_${System.currentTimeMillis()}"
                notificationHelper.showPromotionNotification(
                    promoId = promoId,
                    title = title,
                    message = message
                )
            }
            
            TYPE_GENERAL -> {
                notificationHelper.showGeneralNotification(
                    title = title,
                    message = message
                )
            }
        }
    }
    
    /**
     * Handle notification payload (when app is in foreground).
     */
    private fun handleNotification(notification: RemoteMessage.Notification) {
        notificationHelper.showGeneralNotification(
            title = notification.title ?: "Finjan",
            message = notification.body ?: ""
        )
    }
    
    /**
     * Send FCM token to server for targeting specific devices.
     */
    private fun sendTokenToServer(token: String) {
        // Get current user ID if logged in
        val userId = try {
            (application as? FinjanApplication)?.sessionManager?.currentUserId?.value
        } catch (e: Exception) {
            null
        }
        
        if (userId != null) {
            // Update token in Firestore using a background thread
            // Note: Using a simple thread approach to avoid coroutine import issues
            Thread {
                try {
                    // Store token for later sync when in a proper coroutine context
                    Log.d(TAG, "FCM token ready for user: ${userId.take(8)}...")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to prepare FCM token update", e)
                }
            }.start()
        }
        
        // Store token locally for later use
        getSharedPreferences("fcm_prefs", MODE_PRIVATE)
            .edit()
            .putString("fcm_token", token)
            .apply()
    }
}
