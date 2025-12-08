package com.example.finjan.data.repository

import com.example.finjan.data.model.MenuItem
import com.example.finjan.data.model.Offer
import com.example.finjan.data.model.Order
import com.example.finjan.data.model.OrderItem
import com.example.finjan.data.model.OrderStatus
import com.example.finjan.data.model.PaymentMethod
import com.example.finjan.data.model.UserProfile
import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for Firestore data operations.
 * Provides a clean API for CRUD operations on all collections.
 */
class FirestoreRepository {

    companion object {
        private const val TAG = "FirestoreRepository"
        
        // Collection names
        private const val USERS_COLLECTION = "users"
        private const val MENU_ITEMS_COLLECTION = "menu_items"
        private const val ORDERS_COLLECTION = "orders"
        private const val OFFERS_COLLECTION = "offers"
        private const val PAYMENT_METHODS_COLLECTION = "payment_methods"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ============= USER PROFILE =============

    /**
     * Get user profile by ID.
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val doc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    Result.Success(profile)
                } else {
                    Result.Error("Failed to parse user profile")
                }
            } else {
                Result.Error("User profile not found")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error getting user profile", e)
            Result.Error("Failed to get profile: ${e.message}", e)
        }
    }

    /**
     * Create or update user profile.
     */
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val profileWithTimestamp = profile.copy(updatedAt = Timestamp.now())
            
            firestore.collection(USERS_COLLECTION)
                .document(profile.id)
                .set(profileWithTimestamp)
                .await()

            AppLogger.logAuth("Profile saved", true, profile.id)
            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error saving user profile", e)
            Result.Error("Failed to save profile: ${e.message}", e)
        }
    }

    /**
     * Update specific fields in user profile.
     */
    suspend fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updated_at"] = Timestamp.now()
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updatesWithTimestamp)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error updating user profile", e)
            Result.Error("Failed to update profile: ${e.message}", e)
        }
    }

    // ============= MENU ITEMS =============

    /**
     * Get all menu items.
     */
    suspend fun getMenuItems(): Result<List<MenuItem>> {
        return try {
            val snapshot = firestore.collection(MENU_ITEMS_COLLECTION)
                .whereEqualTo("is_available", true)
                .orderBy("title")
                .get()
                .await()

            val items = snapshot.toObjects(MenuItem::class.java)
            AppLogger.d(TAG, "Fetched ${items.size} menu items")
            Result.Success(items)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching menu items", e)
            Result.Error("Failed to load menu: ${e.message}", e)
        }
    }

    /**
     * Get menu items by category.
     */
    suspend fun getMenuItemsByCategory(category: String): Result<List<MenuItem>> {
        return try {
            val snapshot = firestore.collection(MENU_ITEMS_COLLECTION)
                .whereEqualTo("category", category)
                .whereEqualTo("is_available", true)
                .get()
                .await()

            val items = snapshot.toObjects(MenuItem::class.java)
            Result.Success(items)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching menu items by category", e)
            Result.Error("Failed to load menu: ${e.message}", e)
        }
    }

    /**
     * Get featured menu items.
     */
    suspend fun getFeaturedItems(): Result<List<MenuItem>> {
        return try {
            val snapshot = firestore.collection(MENU_ITEMS_COLLECTION)
                .whereEqualTo("is_featured", true)
                .whereEqualTo("is_available", true)
                .limit(10)
                .get()
                .await()

            val items = snapshot.toObjects(MenuItem::class.java)
            Result.Success(items)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching featured items", e)
            Result.Error("Failed to load featured items: ${e.message}", e)
        }
    }

    /**
     * Observe menu items in real-time.
     */
    fun observeMenuItems(): Flow<Result<List<MenuItem>>> = callbackFlow {
        val listener = firestore.collection(MENU_ITEMS_COLLECTION)
            .whereEqualTo("is_available", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error("Failed to observe menu: ${error.message}"))
                    return@addSnapshotListener
                }

                val items = snapshot?.toObjects(MenuItem::class.java) ?: emptyList()
                trySend(Result.Success(items))
            }

        awaitClose { listener.remove() }
    }

    // ============= ORDERS =============

    /**
     * Create a new order.
     */
    suspend fun createOrder(
        items: List<OrderItem>,
        totalAmount: Double,
        paymentMethod: String,
        notes: String? = null
    ): Result<String> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error("User not authenticated")

        return try {
            val order = Order(
                userId = userId,
                items = items,
                total = totalAmount,
                status = OrderStatus.PENDING.name,
                paymentMethod = paymentMethod,
                specialInstructions = notes
            )

            val docRef = firestore.collection(ORDERS_COLLECTION)
                .add(order)
                .await()

            AppLogger.i(TAG, "Order created: ${docRef.id}")
            Result.Success(docRef.id)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error creating order", e)
            Result.Error("Failed to create order: ${e.message}", e)
        }
    }

    /**
     * Get user's order history.
     */
    suspend fun getOrderHistory(limit: Int = 20): Result<List<Order>> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error("User not authenticated")

        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            Result.Success(orders)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching order history", e)
            Result.Error("Failed to load orders: ${e.message}", e)
        }
    }

    /**
     * Get a specific order by ID.
     */
    suspend fun getOrder(orderId: String): Result<Order> {
        return try {
            val doc = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = doc.toObject(Order::class.java)
            if (order != null) {
                Result.Success(order)
            } else {
                Result.Error("Order not found")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching order", e)
            Result.Error("Failed to load order: ${e.message}", e)
        }
    }

    /**
     * Observe order status changes in real-time.
     */
    fun observeOrder(orderId: String): Flow<Result<Order>> = callbackFlow {
        val listener = firestore.collection(ORDERS_COLLECTION)
            .document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error("Failed to observe order: ${error.message}"))
                    return@addSnapshotListener
                }

                val order = snapshot?.toObject(Order::class.java)
                if (order != null) {
                    trySend(Result.Success(order))
                } else {
                    trySend(Result.Error("Order not found"))
                }
            }

        awaitClose { listener.remove() }
    }

    // ============= OFFERS =============

    /**
     * Get active offers.
     */
    suspend fun getActiveOffers(): Result<List<Offer>> {
        return try {
            val now = Timestamp.now()
            val snapshot = firestore.collection(OFFERS_COLLECTION)
                .whereEqualTo("is_active", true)
                .whereLessThanOrEqualTo("start_date", now)
                .get()
                .await()

            val offers = snapshot.toObjects(Offer::class.java)
                .filter { it.endDate == null || it.endDate!! > now }

            Result.Success(offers)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching offers", e)
            Result.Error("Failed to load offers: ${e.message}", e)
        }
    }

    /**
     * Validate promo code.
     */
    suspend fun validatePromoCode(code: String): Result<Offer> {
        return try {
            val now = Timestamp.now()
            val snapshot = firestore.collection(OFFERS_COLLECTION)
                .whereEqualTo("promo_code", code.uppercase())
                .whereEqualTo("is_active", true)
                .limit(1)
                .get()
                .await()

            val offer = snapshot.toObjects(Offer::class.java).firstOrNull()
            
            when {
                offer == null -> Result.Error("Invalid promo code")
                offer.endDate != null && offer.endDate!! < now -> Result.Error("Promo code has expired")
                else -> Result.Success(offer)
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error validating promo code", e)
            Result.Error("Failed to validate code: ${e.message}", e)
        }
    }

    // ============= PAYMENT METHODS =============

    /**
     * Get user's saved payment methods.
     */
    suspend fun getPaymentMethods(): Result<List<PaymentMethod>> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error("User not authenticated")

        return try {
            val snapshot = firestore.collection(PAYMENT_METHODS_COLLECTION)
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val methods = snapshot.toObjects(PaymentMethod::class.java)
            Result.Success(methods)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error fetching payment methods", e)
            Result.Error("Failed to load payment methods: ${e.message}", e)
        }
    }

    /**
     * Add a payment method.
     */
    suspend fun addPaymentMethod(method: PaymentMethod): Result<String> {
        val userId = auth.currentUser?.uid
            ?: return Result.Error("User not authenticated")

        return try {
            val methodWithUser = method.copy(userId = userId)
            val docRef = firestore.collection(PAYMENT_METHODS_COLLECTION)
                .add(methodWithUser)
                .await()

            Result.Success(docRef.id)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error adding payment method", e)
            Result.Error("Failed to add payment method: ${e.message}", e)
        }
    }

    /**
     * Delete a payment method.
     */
    suspend fun deletePaymentMethod(methodId: String): Result<Unit> {
        return try {
            firestore.collection(PAYMENT_METHODS_COLLECTION)
                .document(methodId)
                .delete()
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error deleting payment method", e)
            Result.Error("Failed to delete payment method: ${e.message}", e)
        }
    }

    // ============= ORDERS =============

    /**
     * Create a new order.
     */
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderWithTimestamp = order.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            val docRef = firestore.collection(ORDERS_COLLECTION).document()
            val orderWithId = orderWithTimestamp.copy(id = docRef.id)
            
            docRef.set(orderWithId).await()
            
            AppLogger.d(TAG, "Order created: ${docRef.id}")
            Result.Success(docRef.id)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error creating order", e)
            Result.Error("Failed to create order: ${e.message}", e)
        }
    }

    /**
     * Update order status.
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(
                    mapOf(
                        "status" to status,
                        "updated_at" to Timestamp.now()
                    )
                )
                .await()
                
            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error updating order status", e)
            Result.Error("Failed to update status: ${e.message}", e)
        }
    }

    // ============= FCM =============
    
    /**
     * Update FCM token for user.
     */
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit> {
        return try {
             firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update("fcm_token", token)
                .await()
             Result.Success(Unit)
        } catch (e: Exception) {
            // Ignore if user doc doesn't exist yet or other silent fails
             AppLogger.w(TAG, "Failed to update FCM token", e)
             Result.Error("Failed to update token", e)
        }
    }
}
