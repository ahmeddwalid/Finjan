package com.example.finjan.data.repository

import com.example.finjan.model.LegalDocument
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Result

class LegalRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ILegalRepository {
    private val legalCollection = firestore.collection("legal_documents")

    override suspend fun getLegalDocument(documentId: String): Result<LegalDocument> = runCatching {
        val document = legalCollection.document(documentId).get().await()

        if (document.exists()) {
            LegalDocument(
                title = document.getString("title") ?: "",
                content = document.getString("content") ?: "",
                version = document.getString("version") ?: "1.0",
                lastUpdated = document.getTimestamp("lastUpdated")?.toDate()?.toString()
                    ?: "Unknown"
            )
        } else {
            throw Exception("Document not found")
        }
    }
}