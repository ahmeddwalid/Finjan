package com.example.finjan.utils.locale

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Manages app locale/language settings.
 * Supports runtime language switching with proper RTL handling.
 */
object LocaleManager {
    
    /**
     * Available languages in the app.
     */
    enum class AppLanguage(
        val code: String,
        val displayName: String,
        val nativeName: String,
        val isRtl: Boolean
    ) {
        ENGLISH("en", "English", "English", false),
        ARABIC("ar", "Arabic", "العربية", true);
        
        companion object {
            /**
             * Get language by code, falling back to English.
             */
            fun fromCode(code: String): AppLanguage {
                return entries.find { it.code == code } ?: ENGLISH
            }
            
            /**
             * Get the system's default language if supported.
             */
            fun getSystemDefault(): AppLanguage {
                val systemLocale = Locale.getDefault().language
                return fromCode(systemLocale)
            }
        }
    }
    
    /**
     * Set the app's locale.
     * Uses per-app language preferences on Android 13+ for seamless switching.
     */
    fun setLocale(language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    
    /**
     * Get the current app locale.
     */
    fun getCurrentLocale(): AppLanguage {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) {
            AppLanguage.getSystemDefault()
        } else {
            val languageTag = locales.get(0)?.language ?: "en"
            AppLanguage.fromCode(languageTag)
        }
    }
    
    /**
     * Check if the current locale is RTL.
     */
    fun isCurrentLocaleRtl(): Boolean {
        return getCurrentLocale().isRtl
    }
    
    /**
     * Apply locale to a context (for older Android versions).
     * On Android 13+, this is handled automatically by AppCompatDelegate.
     */
    fun applyLocaleToContext(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        config.setLayoutDirection(locale)
        
        return context.createConfigurationContext(config)
    }
    
    /**
     * Get the layout direction for the current locale.
     * @return View.LAYOUT_DIRECTION_RTL or View.LAYOUT_DIRECTION_LTR
     */
    fun getLayoutDirection(): Int {
        return if (isCurrentLocaleRtl()) {
            android.view.View.LAYOUT_DIRECTION_RTL
        } else {
            android.view.View.LAYOUT_DIRECTION_LTR
        }
    }
}
