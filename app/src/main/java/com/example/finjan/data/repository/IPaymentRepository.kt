package com.example.finjan.data.repository

import com.example.finjan.utils.Result

interface IPaymentRepository {
    suspend fun createPaymentIntent(amount: Long, currency: String): Result<String>
}
