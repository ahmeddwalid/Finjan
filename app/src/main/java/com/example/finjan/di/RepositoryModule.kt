package com.example.finjan.di

import com.example.finjan.data.repository.FirestoreRepository
import com.example.finjan.data.repository.IFirestoreRepository
import com.example.finjan.data.repository.ILegalRepository
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.data.repository.IPaymentRepository
import com.example.finjan.data.repository.LegalRepository
import com.example.finjan.data.repository.LocalRepository
import com.example.finjan.data.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocalRepository(impl: LocalRepository): ILocalRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(impl: FirestoreRepository): IFirestoreRepository

    @Binds
    @Singleton
    abstract fun bindLegalRepository(impl: LegalRepository): ILegalRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepository): IPaymentRepository
}
