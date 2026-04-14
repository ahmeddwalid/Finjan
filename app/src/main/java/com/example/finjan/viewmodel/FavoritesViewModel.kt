package com.example.finjan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.local.entity.FavoriteEntity
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for favorites management.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(private val localRepository: ILocalRepository) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteEntity>> = _favorites.asStateFlow()
    
    val favoritesCount: StateFlow<Int> = localRepository.getFavoritesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            localRepository.getAllFavorites().collect { result ->
                _isLoading.value = false
                when (result) {
                    is Result.Success -> {
                        _favorites.value = result.data
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception?.message ?: result.message
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    fun isFavorite(itemId: String): StateFlow<Boolean> {
        return localRepository.isFavorite(itemId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }
    
    fun toggleFavorite(
        itemId: String,
        title: String,
        description: String,
        imageRes: Int,
        category: String,
        price: Double
    ) {
        viewModelScope.launch {
            when (val result = localRepository.toggleFavorite(
                itemId, title, description, imageRes, category, price
            )) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun addToFavorites(
        itemId: String,
        title: String,
        description: String,
        imageRes: Int,
        category: String,
        price: Double
    ) {
        viewModelScope.launch {
            when (val result = localRepository.addToFavorites(
                itemId, title, description, imageRes, category, price
            )) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun removeFromFavorites(itemId: String) {
        viewModelScope.launch {
            when (val result = localRepository.removeFromFavorites(itemId)) {
                is Result.Success -> _error.value = null
                is Result.Error -> _error.value = result.exception?.message ?: result.message
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

/**
 * Favorites UI state for composables.
 */
data class FavoritesUiState(
    val items: List<FavoriteEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = items.isEmpty()
}
