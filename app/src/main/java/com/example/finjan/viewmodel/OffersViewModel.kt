package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import com.example.finjan.data.local.MenuDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

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
@HiltViewModel
class OffersViewModel @Inject constructor(
    private val menuDataSource: MenuDataSource
) : ViewModel() {
    
    private val _offers = MutableStateFlow(menuDataSource.getOffers())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    /**
     * Refresh offers from backend (placeholder for future Firebase integration).
     */
    fun refreshOffers() {
        // TODO: Fetch offers from Firestore when implemented
    }
}
