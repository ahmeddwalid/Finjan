package com.example.finjan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

        private val ORDER_UPDATES_KEY = booleanPreferencesKey("order_updates")
        private val PROMOTIONAL_OFFERS_KEY = booleanPreferencesKey("promotional_offers")
        private val NEW_PRODUCTS_KEY = booleanPreferencesKey("new_products")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    data class NotificationSettings(
        val orderUpdates: Boolean = true,
        val promotionalOffers: Boolean = false,
        val newProducts: Boolean = false
    )

    val notificationSettings: Flow<NotificationSettings> = context.dataStore.data.map { prefs ->
        NotificationSettings(
            orderUpdates = prefs[ORDER_UPDATES_KEY] ?: true,
            promotionalOffers = prefs[PROMOTIONAL_OFFERS_KEY] ?: false,
            newProducts = prefs[NEW_PRODUCTS_KEY] ?: false
        )
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "en"
    }

    suspend fun setOrderUpdates(enabled: Boolean) {
        context.dataStore.edit { it[ORDER_UPDATES_KEY] = enabled }
    }

    suspend fun setPromotionalOffers(enabled: Boolean) {
        context.dataStore.edit { it[PROMOTIONAL_OFFERS_KEY] = enabled }
    }

    suspend fun setNewProducts(enabled: Boolean) {
        context.dataStore.edit { it[NEW_PRODUCTS_KEY] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }
}
