package com.example.finjan.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.finjan.ui.MainActivity
import com.example.finjan.R

/**
 * Helper class for creating and displaying notifications.
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        // Channel IDs
        const val CHANNEL_ORDERS = "orders_channel"
        const val CHANNEL_PROMOTIONS = "promotions_channel"
        const val CHANNEL_GENERAL = "general_channel"
        
        // Notification IDs
        const val NOTIFICATION_ORDER_STATUS = 1001
        const val NOTIFICATION_PROMOTION = 2001
        const val NOTIFICATION_GENERAL = 3001
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Create all notification channels.
     * Required for Android O and above.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ORDERS,
                    "Order Updates",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications about your order status"
                    enableVibration(true)
                    enableLights(true)
                },
                NotificationChannel(
                    CHANNEL_PROMOTIONS,
                    "Promotions & Offers",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Special offers and promotions"
                },
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "General app notifications"
                }
            )
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            channels.forEach { channel ->
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Show order status notification.
     */
    fun showOrderStatusNotification(
        orderId: String,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("order_id", orderId)
            putExtra("navigate_to", "order_tracking")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ORDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                orderId.hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
    
    /**
     * Show promotion notification.
     */
    fun showPromotionNotification(
        promoId: String,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("promo_id", promoId)
            putExtra("navigate_to", "offers")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            promoId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_PROMOTIONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                promoId.hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
    
    /**
     * Show general notification.
     */
    fun showGeneralNotification(
        notificationId: Int = NOTIFICATION_GENERAL,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
    
    /**
     * Cancel a specific notification.
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    /**
     * Cancel all notifications.
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
