package com.example.finjan.data.repository

import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.Result
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val functions: FirebaseFunctions
) : IPaymentRepository {

    companion object {
        private const val TAG = "PaymentRepository"
    }

    /**
     * Creates a Stripe PaymentIntent via a Firebase Cloud Function.
     * The Cloud Function creates the PaymentIntent on the server side
     * and returns the clientSecret needed by PaymentSheet.
     */
    override suspend fun createPaymentIntent(amount: Long, currency: String): Result<String> {
        return try {
            val data = hashMapOf(
                "amount" to amount,
                "currency" to currency
            )

            val result = functions
                .getHttpsCallable("createPaymentIntent")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val response = result.getData() as? Map<String, Any>
            val clientSecret = response?.get("clientSecret") as? String

            if (clientSecret != null) {
                AppLogger.i(TAG, "PaymentIntent created successfully")
                Result.Success(clientSecret)
            } else {
                AppLogger.e(TAG, "No clientSecret in response")
                Result.Error("Failed to get payment client secret")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to create payment intent", e)
            Result.Error("Failed to initiate payment: ${e.message}", e)
        }
    }
}
