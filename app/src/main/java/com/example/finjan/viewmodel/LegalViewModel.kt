package com.example.finjan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finjan.data.repository.ILegalRepository
import com.example.finjan.model.LegalDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LegalViewModel @Inject constructor(
    private val repository: ILegalRepository
) : ViewModel() {
    var showLegalDialog by mutableStateOf(false)
        private set

    var currentLegalDocument by mutableStateOf<LegalDocument?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private suspend fun fetchLegalDocument(documentId: String) {
        isLoading = true
        error = null

        try {
            val result = repository.getLegalDocument(documentId)
            result.onSuccess { document ->
                currentLegalDocument = document
                showLegalDialog = true
            }.onFailure { e ->
                error = e.message ?: "Failed to load document"
            }
        } catch (e: Exception) {
            error = e.message ?: "An unexpected error occurred"
        } finally {
            isLoading = false
        }
    }

    fun showTermsOfService() {
        viewModelScope.launch {
            fetchLegalDocument("terms_of_service")
        }
    }

    fun showPrivacyPolicy() {
        viewModelScope.launch {
            fetchLegalDocument("privacy_policy")
        }
    }

    fun hideLegalDialog() {
        showLegalDialog = false
        currentLegalDocument = null
        error = null
    }
}