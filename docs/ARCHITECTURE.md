# Finjan Architecture

This document describes the architecture and design patterns used in the Finjan coffee shop application.

## Overview

Finjan follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns across multiple layers.

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   Screens   │  │ Components  │  │   Theme     │          │
│  └──────┬──────┘  └─────────────┘  └─────────────┘          │
│         │                                                    │
│         ▼                                                    │
│  ┌─────────────────────────────────────────────┐            │
│  │              ViewModels                      │            │
│  │   StateFlow ◄──── Events ◄──── UI           │            │
│  └──────────────────────┬──────────────────────┘            │
└─────────────────────────┼────────────────────────────────────┘
                          │
┌─────────────────────────┼────────────────────────────────────┐
│                   Data Layer                                 │
│         ┌───────────────┴───────────────┐                   │
│         ▼                               ▼                   │
│  ┌─────────────┐                 ┌─────────────┐            │
│  │   Local     │                 │   Remote    │            │
│  │ Repository  │                 │ Repository  │            │
│  └──────┬──────┘                 └──────┬──────┘            │
│         │                               │                   │
│         ▼                               ▼                   │
│  ┌─────────────┐                 ┌─────────────┐            │
│  │    Room     │                 │  Firestore  │            │
│  │  Database   │                 │  Firebase   │            │
│  └─────────────┘                 └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

## Directory Structure

```
app/src/main/java/com/example/finjan/
├── FinjanApplication.kt      # Application class
├── data/
│   ├── local/
│   │   ├── dao/             # Room DAOs
│   │   ├── entity/          # Room entities
│   │   ├── FinjanDatabase.kt
│   │   └── ThemePreferences.kt
│   ├── model/
│   │   └── FirestoreModels.kt
│   └── repository/
│       ├── LocalRepository.kt
│       ├── FirestoreRepository.kt
│       └── LegalRepository.kt
├── navigation/
│   ├── Routes.kt            # Type-safe routes
│   ├── NavigationItems.kt
│   └── NavigationManager.kt
├── service/
│   └── FinjanMessagingService.kt
├── ui/
│   ├── components/          # Reusable UI components
│   ├── screens/            # Screen composables
│   │   ├── authentication/
│   │   ├── cart/
│   │   ├── favorites/
│   │   ├── home/
│   │   ├── order/
│   │   ├── payment/
│   │   ├── product/
│   │   ├── search/
│   │   ├── settings/
│   │   └── welcome/
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── auth/
│   │   └── GoogleAuthManager.kt
│   └── security/
│       ├── SecurePreferencesManager.kt
│       └── SessionManager.kt
└── viewmodel/
    ├── AuthenticationViewModel.kt
    ├── CartViewModel.kt
    └── ...
```

## Key Components

### Type-Safe Navigation

Uses Kotlin Serialization for compile-time safe navigation:

```kotlin
sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data class ProductDetails(val productId: String) : Route
}
```

### State Management

ViewModels use `StateFlow` for reactive state:

```kotlin
class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
}
```

### Theming

Material 3 with brown-themed dark mode:

- **Light**: Warm cream backgrounds (#D6C8B3)
- **Dark**: Mocha/espresso browns (#1A120D, #2A1E16)

### Data Persistence

- **Room**: Local cart, favorites, search history
- **DataStore**: User preferences (theme)
- **Firestore**: Orders, user profiles, menu items

## Data Flow Example

```
User taps "Add to Cart"
        │
        ▼
┌───────────────────┐
│   CartScreen      │ ─── onClick event
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│  CartViewModel    │ ─── addToCart(item)
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│ LocalRepository   │ ─── suspend fun addToCart()
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│    CartDao        │ ─── @Insert
└─────────┬─────────┘
          │
          ▼
    Room Database
```

## Security

- **EncryptedSharedPreferences**: Sensitive data storage
- **Input Sanitization**: AuthenticationViewModel
- **Rate Limiting**: Password reset, login attempts
- **Nonce Generation**: Google Sign-In
