package com.example.finjan.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore data models with proper serialization annotations.
 * All models are designed for Firebase Firestore integration.
 */

/**
 * User profile stored in Firestore.
 */
data class UserProfile(
    @DocumentId
    val id: String = "",
    val email: String = "",
    @get:PropertyName("display_name")
    @set:PropertyName("display_name")
    var displayName: String = "",
    @get:PropertyName("photo_url")
    @set:PropertyName("photo_url")
    var photoUrl: String? = null,
    @get:PropertyName("phone_number")
    @set:PropertyName("phone_number")
    var phoneNumber: String? = null,
    @get:PropertyName("loyalty_points")
    @set:PropertyName("loyalty_points")
    var loyaltyPoints: Int = 0,
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    @ServerTimestamp
    var createdAt: Timestamp? = null,
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    @ServerTimestamp
    var updatedAt: Timestamp? = null
)

/**
 * Menu item/product in the catalog.
 */
data class MenuItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String? = null,
    @get:PropertyName("is_available")
    @set:PropertyName("is_available")
    var isAvailable: Boolean = true,
    @get:PropertyName("is_featured")
    @set:PropertyName("is_featured")
    var isFeatured: Boolean = false,
    val ingredients: List<String> = emptyList(),
    @get:PropertyName("nutritional_info")
    @set:PropertyName("nutritional_info")
    var nutritionalInfo: NutritionalInfo? = null,
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    @ServerTimestamp
    var createdAt: Timestamp? = null
)

/**
 * Nutritional information for menu items.
 */
data class NutritionalInfo(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val caffeine: Int = 0 // mg
)

/**
 * Order placed by a user.
 */
data class Order(
    @DocumentId
    val id: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    @get:PropertyName("delivery_fee")
    @set:PropertyName("delivery_fee")
    var deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    @get:PropertyName("total_amount")
    @set:PropertyName("total_amount")
    var total: Double = 0.0,
    val status: String = OrderStatus.PENDING.name,
    @get:PropertyName("payment_method")
    @set:PropertyName("payment_method")
    var paymentMethod: String = "",
    @get:PropertyName("order_type")
    @set:PropertyName("order_type")
    var orderType: String = OrderType.PICKUP.name,
    @get:PropertyName("special_instructions")
    @set:PropertyName("special_instructions")
    var specialInstructions: String? = null,
    @get:PropertyName("delivery_address")
    @set:PropertyName("delivery_address")
    var deliveryAddress: String? = null,
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    @ServerTimestamp
    var createdAt: Timestamp? = null,
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    @ServerTimestamp
    var updatedAt: Timestamp? = null
)

/**
 * Individual item in an order.
 */
data class OrderItem(
    @get:PropertyName("menu_item_id")
    @set:PropertyName("menu_item_id")
    var menuItemId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val customizations: List<String> = emptyList()
)

/**
 * Order status enum.
 */
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
}

/**
 * Order type enum.
 */
enum class OrderType {
    PICKUP,
    DINE_IN,
    DELIVERY
}

/**
 * Promotional offer.
 */
data class Offer(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @get:PropertyName("discount_type")
    @set:PropertyName("discount_type")
    var discountType: String = DiscountType.PERCENTAGE.name,
    @get:PropertyName("discount_value")
    @set:PropertyName("discount_value")
    var discountValue: Double = 0.0,
    @get:PropertyName("min_order_amount")
    @set:PropertyName("min_order_amount")
    var minOrderAmount: Double? = null,
    @get:PropertyName("promo_code")
    @set:PropertyName("promo_code")
    var promoCode: String? = null,
    @get:PropertyName("is_active")
    @set:PropertyName("is_active")
    var isActive: Boolean = true,
    @get:PropertyName("start_date")
    @set:PropertyName("start_date")
    var startDate: Timestamp? = null,
    @get:PropertyName("end_date")
    @set:PropertyName("end_date")
    var endDate: Timestamp? = null
)

/**
 * Discount type enum.
 */
enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_ITEM
}

/**
 * User's saved payment method (masked for security).
 */
data class PaymentMethod(
    @DocumentId
    val id: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    val type: String = PaymentType.CARD.name,
    @get:PropertyName("last_four")
    @set:PropertyName("last_four")
    var lastFour: String = "",
    val brand: String = "",
    @get:PropertyName("expiry_month")
    @set:PropertyName("expiry_month")
    var expiryMonth: Int = 0,
    @get:PropertyName("expiry_year")
    @set:PropertyName("expiry_year")
    var expiryYear: Int = 0,
    @get:PropertyName("is_default")
    @set:PropertyName("is_default")
    var isDefault: Boolean = false
)

/**
 * Payment type enum.
 */
enum class PaymentType {
    CARD,
    APPLE_PAY,
    GOOGLE_PAY,
    CASH
}
