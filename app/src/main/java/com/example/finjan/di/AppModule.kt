package com.example.finjan.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.finjan.data.local.ThemePreferences
import com.example.finjan.data.local.UserPreferences
import com.example.finjan.utils.security.InputValidator
import com.example.finjan.utils.security.RateLimiter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    fun provideRateLimiter(): RateLimiter {
        return RateLimiter(
            maxAttempts = 5,
            windowMs = 60_000L,
            lockoutMs = 300_000L
        )
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
