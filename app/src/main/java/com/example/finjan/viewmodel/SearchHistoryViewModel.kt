package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.entity.SearchHistoryEntity
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class for search history UI representation.
 */
data class SearchHistoryItem(
    val id: Long,
    val query: String,
    val timestamp: Long
)

/**
 * ViewModel for Search History screen.
 * Handles search history management.
 */
@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val localRepository: ILocalRepository
) : ViewModel() {
    
    private val _searchHistory = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistoryItem>> = _searchHistory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadSearchHistory()
    }
    
    /**
     * Load search history from local database.
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            
            localRepository.getRecentSearches(limit = 50).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _searchHistory.value = result.data.map { entity ->
                            SearchHistoryItem(
                                id = entity.id,
                                query = entity.query,
                                timestamp = entity.timestamp
                            )
                        }
                    }
                    is Result.Error -> {
                        _error.value = result.message
                    }
                    is Result.Loading -> { /* Ignored */ }
                }
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a search history item.
     */
    fun deleteSearchItem(query: String) {
        viewModelScope.launch {
            when (val result = localRepository.removeSearchQuery(query)) {
                is Result.Success -> {
                    // List will auto-update via Flow
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> { /* Ignored */ }
            }
        }
    }
    
    /**
     * Clear all search history.
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            when (val result = localRepository.clearSearchHistory()) {
                is Result.Success -> {
                    _searchHistory.value = emptyList()
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> { /* Ignored */ }
            }
        }
    }
    
    /**
     * Add a search query to history.
     */
    fun addSearchQuery(query: String) {
        viewModelScope.launch {
            localRepository.addSearchQuery(query)
        }
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
}
