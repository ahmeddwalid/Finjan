package com.example.finjan.viewmodel

import com.example.finjan.data.local.entity.FavoriteEntity
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.utils.Result
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var localRepository: ILocalRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        localRepository = mockk(relaxed = true)
        coEvery { localRepository.getAllFavorites() } returns flowOf(Result.Success(emptyList()))
        viewModel = FavoritesViewModel(localRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty favorites`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `favorites should update when repository emits data`() = runTest {
        val favorites = listOf(
            FavoriteEntity("1", "Espresso", "Strong coffee", 0, "Coffee", 3.99),
            FavoriteEntity("2", "Latte", "Creamy coffee", 0, "Coffee", 4.99)
        )
        coEvery { localRepository.getAllFavorites() } returns flowOf(Result.Success(favorites))

        viewModel = FavoritesViewModel(localRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.favorites.value.size)
        assertEquals("Espresso", viewModel.favorites.value[0].title)
    }

    @Test
    fun `removeFromFavorites should call repository`() = runTest {
        coEvery { localRepository.removeFromFavorites(any()) } returns Result.Success(Unit)

        viewModel.removeFromFavorites("1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { localRepository.removeFromFavorites("1") }
    }

    @Test
    fun `error should be set when removal fails`() = runTest {
        coEvery { localRepository.removeFromFavorites(any()) } returns Result.Error("Failed")

        viewModel.removeFromFavorites("1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed", viewModel.error.value)
    }
}
