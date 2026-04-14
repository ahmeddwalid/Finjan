package com.example.finjan.data.repository

import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IPaymentRepository {

    companion object {
        private const val TAG = "PaymentRepository"
    }

    /**
     * Creates a payment intent by writing to Firestore.
     * A Cloud Function (or backend) should listen to this collection
     * and create the actual Stripe PaymentIntent, writing back the clientSecret.
     *
     * For development/testing, this returns a mock client secret.
     * TODO: Replace with actual backend endpoint or Cloud Function trigger.
     */
    override suspend fun createPaymentIntent(amount: Long, currency: String): Result<String> {
        return try {
            val data = hashMapOf(
                "amount" to amount,
                "currency" to currency,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            val docRef = firestore.collection("payment_intents").add(data).await()
            AppLogger.i(TAG, "Payment intent request created: ${docRef.id}")

            // TODO: In production, poll or listen to this document for the clientSecret
            // written back by your Cloud Function / backend.
            // For now, return a placeholder that the PaymentSheet can use in test mode.
            Result.Success(docRef.id)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to create payment intent", e)
            Result.Error("Failed to initiate payment: ${e.message}", e)
        }
    }
}
