package com.example.finjan.utils.config

/**
 * Type-safe accessors for feature flags and A/B test parameters.
 * All keys should match those defined in remote_config_defaults.xml.
 */
object FeatureFlags {
    
    // ========== Force Update ==========
    
    /** Whether force update checks are enabled */
    val forceUpdateEnabled: Boolean
        get() = RemoteConfigManager.getBoolean("force_update_enabled")
    
    /** Minimum required version code */
    val minVersionCode: Long
        get() = RemoteConfigManager.getLong("min_version_code")
    
    /** Force update message shown to users */
    val forceUpdateMessage: String
        get() = RemoteConfigManager.getString("force_update_message")
    
    /** Play Store URL for updates */
    val updateUrl: String
        get() = RemoteConfigManager.getString("update_url")
    
    // ========== Feature Toggles ==========
    
    /** Whether the loyalty points feature is enabled */
    val loyaltyPointsEnabled: Boolean
        get() = RemoteConfigManager.getBoolean("feature_loyalty_points_enabled")
    
    /** Whether the offers carousel is enabled on home screen */
    val homeOffersCarouselEnabled: Boolean
        get() = RemoteConfigManager.getBoolean("feature_home_offers_carousel")
    
    /** Whether social login options are shown */
    val socialLoginEnabled: Boolean
        get() = RemoteConfigManager.getBoolean("feature_social_login_enabled")
    
    /** Whether order scheduling is available */
    val orderSchedulingEnabled: Boolean
        get() = RemoteConfigManager.getBoolean("feature_order_scheduling_enabled")
    
    // ========== A/B Tests ==========
    
    /** Checkout button style variant: "primary", "accent", or "gradient" */
    val checkoutButtonVariant: String
        get() = RemoteConfigManager.getString("ab_checkout_button_variant")
    
    /** Home screen layout: "grid" or "list" */
    val homeLayoutVariant: String
        get() = RemoteConfigManager.getString("ab_home_layout_variant")
    
    /** Product card style: "compact" or "expanded" */
    val productCardVariant: String
        get() = RemoteConfigManager.getString("ab_product_card_variant")
    
    // ========== Dynamic Values ==========
    
    /** Minimum order amount for free delivery */
    val freeDeliveryThreshold: Double
        get() = RemoteConfigManager.getDouble("value_free_delivery_threshold")
    
    /** Points earned per dollar spent */
    val pointsPerDollar: Long
        get() = RemoteConfigManager.getLong("value_points_per_dollar")
    
    /** Welcome message shown on home screen */
    val welcomeMessage: String
        get() = RemoteConfigManager.getString("value_welcome_message")
    
    /** Banner promotional text */
    val promoBannerText: String
        get() = RemoteConfigManager.getString("value_promo_banner_text")
}
