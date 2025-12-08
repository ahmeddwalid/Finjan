package com.example.finjan.utils.payment

import android.content.Context
import com.example.finjan.utils.AppLogger
import com.example.finjan.utils.Result
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.stripe.android.model.PaymentMethod

/**
 * Manager for Stripe payment processing.
 * Handles payment initialization, method creation, and payment confirmation.
 */
class StripePaymentManager(private val context: Context) {
    
    companion object {
        private const val TAG = "StripePaymentManager"
        
        // TODO: Replace with your actual Stripe publishable key
        private const val STRIPE_PUBLISHABLE_KEY = "pk_test_your_publishable_key"
    }
    
    private var stripe: Stripe? = null
    
    /**
     * Initialize Stripe SDK.
     * Call this during app startup.
     */
    fun initialize() {
        try {
            PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY)
            stripe = Stripe(context, STRIPE_PUBLISHABLE_KEY)
            AppLogger.i(TAG, "Stripe SDK initialized")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to initialize Stripe", e)
        }
    }
    
    /**
     * Create card payment method parameters.
     */
    fun createCardParams(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        name: String? = null,
        postalCode: String? = null
    ): PaymentMethodCreateParams {
        val cardParams = PaymentMethodCreateParams.Card.Builder()
            .setNumber(cardNumber.replace(" ", ""))
            .setExpiryMonth(expiryMonth)
            .setExpiryYear(expiryYear)
            .setCvc(cvc)
            .build()
        
        val billingDetails = PaymentMethod.BillingDetails.Builder()
            .apply {
                name?.let { setName(it) }
                postalCode?.let { pc ->
                    setAddress(
                        com.stripe.android.model.Address.Builder()
                            .setPostalCode(pc)
                            .build()
                    )
                }
            }
            .build()
        
        return PaymentMethodCreateParams.create(cardParams, billingDetails)
    }
    
    /**
     * Confirm a payment intent with card details.
     */
    suspend fun confirmPayment(
        clientSecret: String,
        cardParams: PaymentMethodCreateParams
    ): Result<PaymentResult> = withContext(Dispatchers.IO) {
        try {
            val stripeInstance = stripe ?: return@withContext Result.Error("Stripe not initialized")
            
            val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                paymentMethodCreateParams = cardParams,
                clientSecret = clientSecret
            )
            
            val paymentIntent = stripeInstance.confirmPaymentIntentSynchronous(confirmParams)
            
            if (paymentIntent != null) {
                when (paymentIntent.status) {
                    com.stripe.android.model.StripeIntent.Status.Succeeded -> {
                        AppLogger.i(TAG, "Payment succeeded")
                        Result.Success(PaymentResult.Success(paymentIntent.id ?: ""))
                    }
                    com.stripe.android.model.StripeIntent.Status.RequiresAction -> {
                        AppLogger.i(TAG, "Payment requires additional action")
                        Result.Success(PaymentResult.RequiresAction(
                            paymentIntent.id ?: "",
                            paymentIntent.nextActionData
                        ))
                    }
                    com.stripe.android.model.StripeIntent.Status.RequiresPaymentMethod -> {
                        AppLogger.w(TAG, "Payment method failed")
                        Result.Error("Payment method declined")
                    }
                    else -> {
                        AppLogger.w(TAG, "Payment status: ${paymentIntent.status}")
                        Result.Error("Payment in unexpected state")
                    }
                }
            } else {
                Result.Error("Payment confirmation failed")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Payment error", e)
            Result.Error("Payment failed: ${e.message}", e)
        }
    }
    
    /**
     * Validate card number using Luhn algorithm.
     */
    fun isValidCardNumber(cardNumber: String): Boolean {
        val sanitized = cardNumber.replace(" ", "").replace("-", "")
        
        if (sanitized.length < 13 || sanitized.length > 19) return false
        if (!sanitized.all { it.isDigit() }) return false
        
        // Luhn algorithm
        var sum = 0
        var alternate = false
        for (i in sanitized.length - 1 downTo 0) {
            var digit = sanitized[i].digitToInt()
            if (alternate) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
            alternate = !alternate
        }
        return sum % 10 == 0
    }
    
    /**
     * Validate expiry date.
     */
    fun isValidExpiry(month: Int, year: Int): Boolean {
        if (month < 1 || month > 12) return false
        
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        
        val fullYear = if (year < 100) year else year % 100
        
        return when {
            fullYear < currentYear -> false
            fullYear == currentYear && month < currentMonth -> false
            else -> true
        }
    }
    
    /**
     * Validate CVC.
     */
    fun isValidCvc(cvc: String): Boolean {
        return cvc.length in 3..4 && cvc.all { it.isDigit() }
    }
    
    /**
     * Get card brand from card number prefix.
     */
    fun getCardBrand(cardNumber: String): CardBrand {
        val sanitized = cardNumber.replace(" ", "").replace("-", "")
        return when {
            sanitized.startsWith("4") -> CardBrand.VISA
            sanitized.startsWith("5") || sanitized.startsWith("2") -> CardBrand.MASTERCARD
            sanitized.startsWith("34") || sanitized.startsWith("37") -> CardBrand.AMEX
            sanitized.startsWith("6011") || sanitized.startsWith("65") -> CardBrand.DISCOVER
            else -> CardBrand.UNKNOWN
        }
    }
    
    /**
     * Format card number with spaces.
     */
    fun formatCardNumber(cardNumber: String): String {
        val sanitized = cardNumber.replace(" ", "").replace("-", "")
        val brand = getCardBrand(sanitized)
        
        return when (brand) {
            CardBrand.AMEX -> {
                // AMEX: 4-6-5 pattern
                buildString {
                    sanitized.forEachIndexed { index, char ->
                        if (index == 4 || index == 10) append(" ")
                        append(char)
                    }
                }
            }
            else -> {
                // Visa, Mastercard, etc: 4-4-4-4 pattern
                sanitized.chunked(4).joinToString(" ")
            }
        }
    }
}

/**
 * Payment result sealed class.
 */
sealed class PaymentResult {
    data class Success(val paymentIntentId: String) : PaymentResult()
    data class RequiresAction(val paymentIntentId: String, val nextAction: Any?) : PaymentResult()
    data class Failed(val errorMessage: String) : PaymentResult()
}

/**
 * Card brand enum.
 */
enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    UNKNOWN("Unknown")
}
