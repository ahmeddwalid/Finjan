package com.example.finjan.utils.locale

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed preferences for locale settings.
 */
class LocalePreferences(private val context: Context) {
    
    companion object {
        private val Context.localeDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "locale_preferences"
        )
        
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private val USE_SYSTEM_LANGUAGE = stringPreferencesKey("use_system_language")
    }
    
    /**
     * Flow of the selected language.
     */
    val selectedLanguage: Flow<LocaleManager.AppLanguage> = context.localeDataStore.data
        .map { preferences ->
            val code = preferences[SELECTED_LANGUAGE]
            if (code != null) {
                LocaleManager.AppLanguage.fromCode(code)
            } else {
                LocaleManager.AppLanguage.getSystemDefault()
            }
        }
    
    /**
     * Flow of whether to use system language.
     */
    val useSystemLanguage: Flow<Boolean> = context.localeDataStore.data
        .map { preferences ->
            preferences[USE_SYSTEM_LANGUAGE]?.toBoolean() ?: true
        }
    
    /**
     * Save the selected language.
     */
    suspend fun setSelectedLanguage(language: LocaleManager.AppLanguage) {
        context.localeDataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language.code
            preferences[USE_SYSTEM_LANGUAGE] = "false"
        }
    }
    
    /**
     * Set to use system language.
     */
    suspend fun setUseSystemLanguage() {
        context.localeDataStore.edit { preferences ->
            preferences.remove(SELECTED_LANGUAGE)
            preferences[USE_SYSTEM_LANGUAGE] = "true"
        }
    }
    
    /**
     * Clear all locale preferences.
     */
    suspend fun clear() {
        context.localeDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
