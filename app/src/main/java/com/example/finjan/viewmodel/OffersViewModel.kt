package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing an offer/promotion.
 */
data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val discount: String
)

/**
 * ViewModel for the Offers screen.
 * Manages promotional offers and deals.
 */
class OffersViewModel : ViewModel() {
    
    private val _offers = MutableStateFlow(
        listOf(
            Offer(
                id = "1",
                title = "Morning Special",
                description = "Get any coffee before 10 AM",
                discount = "20% OFF"
            ),
            Offer(
                id = "2",
                title = "Buy 2 Get 1 Free",
                description = "On all cookies and pastries",
                discount = "FREE"
            ),
            Offer(
                id = "3",
                title = "Weekend Treat",
                description = "Any milkshake on Saturday & Sunday",
                discount = "15% OFF"
            ),
            Offer(
                id = "4",
                title = "Loyalty Reward",
                description = "After 10 purchases",
                discount = "50% OFF"
            ),
            Offer(
                id = "5",
                title = "Student Discount",
                description = "Show your student ID",
                discount = "10% OFF"
            )
        )
    )
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    /**
     * Refresh offers from backend (placeholder for future Firebase integration).
     */
    fun refreshOffers() {
        // TODO: Fetch offers from Firestore when implemented
    }
}
