package com.example.finjan.data.repository

import com.example.finjan.model.LegalDocument

/**
 * Interface for legal document operations.
 */
interface ILegalRepository {
    suspend fun getLegalDocument(documentId: String): kotlin.Result<LegalDocument>
}
