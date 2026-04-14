package com.example.finjan.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.finjan.R
import com.example.finjan.data.local.MenuDataSource
import com.example.finjan.utils.security.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Data class representing a menu item (product).
 */
data class GridItem(
    val id: String = "",
    @DrawableRes val imageRes: Int,
    val description: String,
    val title: String,
    val category: String = "Coffee",
    val price: Double = 0.0
)

/**
 * Data class representing a product category.
 */
data class Category(val name: String)

/**
 * UI state for the home screen.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "All"
)

/**
 * Production-ready ViewModel for the home screen.
 * Manages menu items, categories, search, and filtering with input validation.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val menuDataSource: MenuDataSource
) : ViewModel() {
    
    private val _gridItems = MutableStateFlow(menuDataSource.getMenuItems())
    val gridItems: StateFlow<List<GridItem>> = _gridItems.asStateFlow()

    private val _categories = MutableStateFlow(menuDataSource.getCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<GridItem>>(emptyList())
    val filteredItems: StateFlow<List<GridItem>> = _filteredItems.asStateFlow()

    init {
        updateFilteredItems()
    }

    /**
     * Update search query with input validation.
     */
    fun updateSearchQuery(query: String) {
        val validation = InputValidator.validateSearchQuery(query)
        if (validation.isValid) {
            _searchQuery.value = InputValidator.sanitizeInput(query)
            _uiState.value = _uiState.value.copy(
                searchQuery = _searchQuery.value,
                error = null
            )
            updateFilteredItems()
        } else {
            _uiState.value = _uiState.value.copy(error = validation.errorMessage)
        }
    }

    /**
     * Select a category and filter items.
     */
    fun selectCategory(category: String) {
        _selectedCategory.value = if (_selectedCategory.value == category) "All" else category
        _uiState.value = _uiState.value.copy(selectedCategory = _selectedCategory.value)
        updateFilteredItems()
    }

    /**
     * Update filtered items based on search and category.
     */
    private fun updateFilteredItems() {
        val query = _searchQuery.value.lowercase().trim()
        val category = _selectedCategory.value
        
        _filteredItems.value = _gridItems.value.filter { item ->
            val matchesSearch = query.isEmpty() || 
                item.title.lowercase().contains(query) ||
                item.description.lowercase().contains(query)
            
            val matchesCategory = category == "All" || item.category == category
            
            matchesSearch && matchesCategory
        }
    }

    /**
     * Get item by ID.
     */
    fun getItemById(id: String): GridItem? {
        return _gridItems.value.find { it.id == id }
    }

    /**
     * Clear search and filters.
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = "All"
        _uiState.value = HomeUiState()
        updateFilteredItems()
    }

    /**
     * Refresh items (placeholder for future Firebase integration).
     */
    fun refreshItems() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // TODO: Fetch from Firestore when implemented
        _uiState.value = _uiState.value.copy(isLoading = false)
        updateFilteredItems()
    }
}
