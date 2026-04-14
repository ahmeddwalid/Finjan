package com.example.finjan.di

import android.content.Context
import com.example.finjan.data.local.FinjanDatabase
import com.example.finjan.data.local.dao.CartDao
import com.example.finjan.data.local.dao.FavoritesDao
import com.example.finjan.data.local.dao.PendingOrderDao
import com.example.finjan.data.local.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinjanDatabase {
        return FinjanDatabase.getInstance(context)
    }

    @Provides
    fun provideCartDao(database: FinjanDatabase): CartDao = database.cartDao()

    @Provides
    fun provideFavoritesDao(database: FinjanDatabase): FavoritesDao = database.favoritesDao()

    @Provides
    fun provideSearchHistoryDao(database: FinjanDatabase): SearchHistoryDao = database.searchHistoryDao()

    @Provides
    fun providePendingOrderDao(database: FinjanDatabase): PendingOrderDao = database.pendingOrderDao()
}
