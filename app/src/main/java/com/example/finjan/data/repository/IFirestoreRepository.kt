package com.example.finjan.data.repository

import com.example.finjan.data.model.MenuItem
import com.example.finjan.data.model.Offer
import com.example.finjan.data.model.Order
import com.example.finjan.data.model.OrderItem
import com.example.finjan.data.model.PaymentMethod
import com.example.finjan.data.model.UserProfile
import com.example.finjan.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Firestore remote data operations.
 * Enables testability and decouples ViewModels from concrete implementation.
 */
interface IFirestoreRepository {

    // ============= USER PROFILE =============
    suspend fun getUserProfile(userId: String): Result<UserProfile>
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit>
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<Unit>

    // ============= MENU ITEMS =============
    suspend fun getMenuItems(): Result<List<MenuItem>>
    suspend fun getMenuItemsByCategory(category: String): Result<List<MenuItem>>
    suspend fun getFeaturedItems(): Result<List<MenuItem>>
    fun observeMenuItems(): Flow<Result<List<MenuItem>>>

    // ============= ORDERS =============
    suspend fun createOrder(items: List<OrderItem>, totalAmount: Double, paymentMethod: String, notes: String? = null): Result<String>
    suspend fun createOrder(order: Order): Result<String>
    suspend fun getOrderHistory(limit: Int = 20): Result<List<Order>>
    suspend fun getOrder(orderId: String): Result<Order>
    fun observeOrder(orderId: String): Flow<Result<Order>>
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>

    // ============= OFFERS =============
    suspend fun getActiveOffers(): Result<List<Offer>>
    suspend fun validatePromoCode(code: String): Result<Offer>

    // ============= PAYMENT METHODS =============
    suspend fun getPaymentMethods(): Result<List<PaymentMethod>>
    suspend fun addPaymentMethod(method: PaymentMethod): Result<String>
    suspend fun deletePaymentMethod(methodId: String): Result<Unit>

    // ============= FCM =============
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit>
}
