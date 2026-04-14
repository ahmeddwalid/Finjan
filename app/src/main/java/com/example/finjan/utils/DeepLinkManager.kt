package com.example.finjan.utils

import android.content.Intent
import android.net.Uri
import com.example.finjan.navigation.Route

/**
 * Manages deep link parsing and routing for the Finjan app.
 * 
 * Supported deep links:
 * - finjan://order/{orderId} - Opens order tracking
 * - finjan://product/{productId} - Opens product details
 * - https://finjan.app/order/{orderId} - App Link for order tracking
 * - https://finjan.app/product/{productId} - App Link for product details
 */
object DeepLinkManager {
    
    private const val SCHEME_CUSTOM = "finjan"
    private const val SCHEME_HTTPS = "https"
    private const val HOST_APP = "finjan.app"
    
    private const val PATH_ORDER = "order"
    private const val PATH_PRODUCT = "product"
    private const val PATH_OFFERS = "offers"
    
    /**
     * Result of parsing a deep link.
     */
    sealed class DeepLinkResult {
        data class OrderTracking(val orderId: String) : DeepLinkResult()
        data class ProductDetails(val productId: String) : DeepLinkResult()
        data object Offers : DeepLinkResult()
        data object Home : DeepLinkResult()
        data object NotHandled : DeepLinkResult()
    }
    
    /**
     * Parse an intent and extract the deep link destination.
     */
    fun parseIntent(intent: Intent?): DeepLinkResult {
        if (intent == null) return DeepLinkResult.NotHandled
        
        val uri = intent.data ?: return DeepLinkResult.NotHandled
        return parseUri(uri)
    }
    
    /**
     * Parse a URI and determine the navigation destination.
     */
    fun parseUri(uri: Uri): DeepLinkResult {
        val scheme = uri.scheme?.lowercase()
        val host = uri.host?.lowercase()
        val pathSegments = uri.pathSegments
        
        // Validate scheme
        if (scheme != SCHEME_CUSTOM && !(scheme == SCHEME_HTTPS && host == HOST_APP)) {
            return DeepLinkResult.NotHandled
        }
        
        // Parse path
        if (pathSegments.isEmpty()) {
            return DeepLinkResult.Home
        }
        
        return when (pathSegments[0].lowercase()) {
            PATH_ORDER -> {
                val orderId = pathSegments.getOrNull(1)
                if (orderId.isNullOrBlank()) {
                    DeepLinkResult.NotHandled
                } else {
                    DeepLinkResult.OrderTracking(orderId)
                }
            }
            PATH_PRODUCT -> {
                val productId = pathSegments.getOrNull(1)
                if (productId.isNullOrBlank()) {
                    DeepLinkResult.NotHandled
                } else {
                    DeepLinkResult.ProductDetails(productId)
                }
            }
            PATH_OFFERS -> DeepLinkResult.Offers
            else -> DeepLinkResult.NotHandled
        }
    }
    
    /**
     * Convert a deep link result to a navigation Route.
     */
    fun toRoute(result: DeepLinkResult): Route? {
        return when (result) {
            is DeepLinkResult.OrderTracking -> Route.OrderTracking(result.orderId)
            is DeepLinkResult.ProductDetails -> Route.ProductDetails(result.productId)
            is DeepLinkResult.Offers -> Route.Offers
            is DeepLinkResult.Home -> Route.Home
            is DeepLinkResult.NotHandled -> null
        }
    }
    
    /**
     * Generate an order tracking deep link URL.
     */
    fun createOrderTrackingLink(orderId: String): String {
        return "https://$HOST_APP/$PATH_ORDER/$orderId"
    }
    
    /**
     * Generate a product details deep link URL.
     */
    fun createProductLink(productId: String): String {
        return "https://$HOST_APP/$PATH_PRODUCT/$productId"
    }
}
