package com.example.finjan.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.finjan.R
import com.example.finjan.ui.MainActivity
import com.example.finjan.FinjanApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging service for handling push notifications.
 * 
 * Notification Types:
 * - ORDER_UPDATE: Updates about order status changes
 * - PROMOTION: Promotional offers and discounts
 * - GENERAL: General announcements
 */
class FinjanMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "FinjanMessaging"
        
        // Notification channels
        const val CHANNEL_ORDER = "order_updates"
        const val CHANNEL_PROMO = "promotions"
        const val CHANNEL_GENERAL = "general"
        
        // Notification types
        const val TYPE_ORDER_UPDATE = "ORDER_UPDATE"
        const val TYPE_PROMOTION = "PROMOTION"
        const val TYPE_GENERAL = "GENERAL"
    }
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    /**
     * Called when a new FCM token is generated.
     * Store the token in Firestore for targeted notifications.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token: $token")
        
        // Update token in Firestore if user is logged in
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            serviceScope.launch {
                try {
                    val repository = FinjanApplication.getInstance().firestoreRepository
                    repository.updateFcmToken(userId, token)
                    Log.d(TAG, "FCM token updated in Firestore")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update FCM token", e)
                }
            }
        }
    }
    
    /**
     * Called when a message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        
        // Handle notification payload (when app is in foreground)
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "Finjan",
                body = notification.body ?: "",
                type = remoteMessage.data["type"] ?: TYPE_GENERAL,
                data = remoteMessage.data
            )
        }
    }
    
    /**
     * Handle data-only messages for background processing.
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: TYPE_GENERAL
        
        when (type) {
            TYPE_ORDER_UPDATE -> {
                val orderId = data["orderId"]
                val status = data["status"]
                val title = data["title"] ?: "Order Update"
                val body = data["body"] ?: "Your order status has been updated"
                
                showNotification(title, body, type, data)
            }
            TYPE_PROMOTION -> {
                val title = data["title"] ?: "Special Offer!"
                val body = data["body"] ?: "Check out our latest deals"
                
                showNotification(title, body, type, data)
            }
            else -> {
                val title = data["title"] ?: "Finjan"
                val body = data["body"] ?: ""
                
                if (body.isNotEmpty()) {
                    showNotification(title, body, type, data)
                }
            }
        }
    }
    
    /**
     * Display a notification.
     */
    private fun showNotification(
        title: String,
        body: String,
        type: String,
        data: Map<String, String>
    ) {
        val channelId = when (type) {
            TYPE_ORDER_UPDATE -> CHANNEL_ORDER
            TYPE_PROMOTION -> CHANNEL_PROMO
            else -> CHANNEL_GENERAL
        }
        
        // Create intent for notification tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            // Add data to intent for deep linking
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    /**
     * Create notification channels for Android O and above.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Order Updates Channel
            val orderChannel = NotificationChannel(
                CHANNEL_ORDER,
                "Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about your order status"
                enableVibration(true)
            }
            
            // Promotions Channel
            val promoChannel = NotificationChannel(
                CHANNEL_PROMO,
                "Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Special offers and discounts"
            }
            
            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General announcements"
            }
            
            notificationManager.createNotificationChannels(
                listOf(orderChannel, promoChannel, generalChannel)
            )
        }
    }
}
