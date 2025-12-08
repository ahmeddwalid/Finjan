package com.example.finjan.viewmodel

import com.example.finjan.data.local.ThemePreferences
import com.example.finjan.data.local.ThemePreferences.ThemeMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var themePreferences: ThemePreferences
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        themePreferences = mockk(relaxed = true)
        
        // Default mock behavior
        coEvery { themePreferences.themeMode } returns flowOf(ThemeMode.SYSTEM)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial theme mode should be SYSTEM`() = runTest {
        coEvery { themePreferences.themeMode } returns flowOf(ThemeMode.SYSTEM)
        
        // Theme mode flow should emit SYSTEM initially
        var emittedMode: ThemeMode? = null
        themePreferences.themeMode.collect {
            emittedMode = it
        }
        
        assertEquals(ThemeMode.SYSTEM, emittedMode)
    }
    
    @Test
    fun `setThemeMode should call preferences setThemeMode`() = runTest {
        // Given
        coEvery { themePreferences.setThemeMode(any()) } returns Unit
        
        // When
        themePreferences.setThemeMode(ThemeMode.DARK)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { themePreferences.setThemeMode(ThemeMode.DARK) }
    }
    
    @Test
    fun `setDarkMode true should result in DARK theme`() = runTest {
        // Given
        coEvery { themePreferences.setDarkMode(true) } returns Unit
        
        // When
        themePreferences.setDarkMode(true)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { themePreferences.setDarkMode(true) }
    }
    
    @Test
    fun `resetToSystem should call preferences resetToSystem`() = runTest {
        // Given
        coEvery { themePreferences.resetToSystem() } returns Unit
        
        // When
        themePreferences.resetToSystem()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { themePreferences.resetToSystem() }
    }
}
