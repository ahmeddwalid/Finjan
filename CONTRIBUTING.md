# Contributing to Finjan

Thank you for your interest in contributing to Finjan! This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Architecture Guidelines](#architecture-guidelines)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)

## Code of Conduct

Please be respectful and considerate of other contributors. We're here to build something great together!

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35
- Firebase project with `google-services.json`

### Fork & Clone

```bash
# Fork the repository on GitHub, then:
git clone https://github.com/YOUR_USERNAME/Finjan.git
cd Finjan
```

### Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable Authentication (Email/Password and Google Sign-In)
3. Enable Firestore Database
4. Download `google-services.json` to `/app/`
5. Add your SHA-1 fingerprint for Google Sign-In

## Development Setup

```bash
# Open in Android Studio
# Sync Gradle files
# Create a local.properties file if not exists
echo "sdk.dir=/path/to/your/android/sdk" > local.properties

# Build the project
./gradlew assembleDebug

# Run tests
./gradlew testDebugUnitTest
```

## Architecture Guidelines

Finjan follows **MVVM (Model-View-ViewModel)** architecture:

```
app/src/main/java/com/example/finjan/
├── data/                  # Data layer
│   ├── local/            # Room database, DAOs, DataStore
│   ├── model/            # Data classes
│   └── repository/       # Repository pattern
├── navigation/           # Compose Navigation
├── service/              # Background services
├── ui/
│   ├── components/       # Reusable composables
│   ├── screens/          # Screen composables
│   └── theme/            # Material3 theming
├── utils/                # Utility classes
└── viewmodel/            # ViewModels
```

### Key Principles

1. **Single Source of Truth**: Data flows from Repository → ViewModel → UI
2. **Unidirectional Data Flow**: Events flow up, state flows down
3. **Separation of Concerns**: Clear boundaries between layers
4. **Testability**: ViewModels should be unit testable

## Pull Request Process

1. **Branch Naming**
   - Features: `feature/short-description`
   - Bugs: `fix/issue-description`
   - Docs: `docs/what-was-updated`

2. **Before Submitting**

   ```bash
   ./gradlew lint
   ./gradlew testDebugUnitTest
   ./gradlew assembleDebug
   ```

3. **PR Description**
   - Clear title describing the change
   - Reference any related issues
   - Include screenshots for UI changes
   - List breaking changes if any

4. **Review Process**
   - At least one approval required
   - All CI checks must pass
   - No merge conflicts

## Coding Standards

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Maximum line length: 120 characters
- Use trailing commas in multi-line collections

### Compose Guidelines

```kotlin
// ✅ Good: Named parameters for clarity
Button(
    onClick = { /* action */ },
    modifier = Modifier.fillMaxWidth(),
    enabled = isEnabled
) {
    Text(text = "Click Me")
}

// ✅ Good: Extract complex modifiers
val cardModifier = Modifier
    .fillMaxWidth()
    .padding(16.dp)
    .clip(RoundedCornerShape(12.dp))
```

### ViewModel Pattern

```kotlin
class ExampleViewModel : ViewModel() {
    // Private mutable state
    private val _uiState = MutableStateFlow(UiState())
    
    // Public read-only state
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Public functions for UI events
    fun onAction(action: Action) {
        viewModelScope.launch {
            // Handle action
        }
    }
}
```

### Testing

- Write unit tests for ViewModels
- Use MockK for mocking
- Follow AAA pattern (Arrange, Act, Assert)
- Aim for meaningful coverage, not 100%

## Questions?

Open an issue or start a discussion on GitHub!
