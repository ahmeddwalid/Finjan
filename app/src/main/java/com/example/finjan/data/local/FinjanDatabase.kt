package com.example.finjan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finjan.data.local.dao.CartDao
import com.example.finjan.data.local.dao.FavoritesDao
import com.example.finjan.data.local.dao.PendingOrderDao
import com.example.finjan.data.local.dao.SearchHistoryDao
import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.local.entity.FavoriteEntity
import com.example.finjan.data.local.entity.MenuItemCacheEntity
import com.example.finjan.data.local.entity.PendingOrderEntity
import com.example.finjan.data.local.entity.SearchHistoryEntity

/**
 * Room database for local data persistence.
 * Handles search history, favorites, cart, and offline caching.
 */
@Database(
    entities = [
        SearchHistoryEntity::class,
        FavoriteEntity::class,
        CartItemEntity::class,
        MenuItemCacheEntity::class,
        PendingOrderEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class FinjanDatabase : RoomDatabase() {
    
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun cartDao(): CartDao
    abstract fun pendingOrderDao(): PendingOrderDao
    
    companion object {
        private const val DATABASE_NAME = "finjan_database"
        
        @Volatile
        private var INSTANCE: FinjanDatabase? = null
        
        fun getInstance(context: Context): FinjanDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): FinjanDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FinjanDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(DatabaseCallback())
                .addMigrations(MIGRATION_1_2)
                .build()
        }
        
        /**
         * Database callback for initialization tasks.
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database created - can pre-populate if needed
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Database opened - perform maintenance if needed
            }
        }
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add indices for better query performance
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_search_history_timestamp` ON `search_history` (`timestamp`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_pending_orders_syncStatus` ON `pending_orders` (`syncStatus`)"
                )
            }
        }
    }
}
