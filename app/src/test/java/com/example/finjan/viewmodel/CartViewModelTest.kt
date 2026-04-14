package com.example.finjan.viewmodel

import com.example.finjan.data.local.entity.CartItemEntity
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
class CartViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var localRepository: ILocalRepository
    private lateinit var viewModel: CartViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        localRepository = mockk(relaxed = true)
        
        // Setup default mock behavior
        coEvery { localRepository.getCartItems() } returns flowOf(Result.Success(emptyList()))
        coEvery { localRepository.getCartTotal() } returns flowOf(0.0)
        coEvery { localRepository.getCartItemCount() } returns flowOf(0)
        
        viewModel = CartViewModel(localRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `addToCart should call repository with correct parameters`() = runTest {
        // Given
        coEvery { 
            localRepository.addToCart(any(), any(), any(), any(), any(), any()) 
        } returns Result.Success(Unit)
        
        // When
        viewModel.addToCart(
            productId = "test-product-1",
            name = "Espresso",
            price = 4.99,
            quantity = 1
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { 
            localRepository.addToCart(
                productId = "test-product-1",
                name = "Espresso",
                price = 4.99,
                quantity = 1,
                imageRes = null,
                customizations = ""
            ) 
        }
    }
    
    @Test
    fun `updateQuantity should call repository updateCartItemQuantity`() = runTest {
        // Given
        coEvery { 
            localRepository.updateCartItemQuantity(any(), any()) 
        } returns Result.Success(Unit)
        
        // When
        viewModel.updateQuantity("test-product-1", 3)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { localRepository.updateCartItemQuantity("test-product-1", 3) }
    }
    
    @Test
    fun `removeFromCart should call repository removeFromCart`() = runTest {
        // Given
        coEvery { localRepository.removeFromCart(any()) } returns Result.Success(Unit)
        
        // When
        viewModel.removeFromCart("test-product-1")
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { localRepository.removeFromCart("test-product-1") }
    }
    
    @Test
    fun `clearCart should call repository clearCart`() = runTest {
        // Given
        coEvery { localRepository.clearCart() } returns Result.Success(Unit)
        
        // When
        viewModel.clearCart()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        coVerify { localRepository.clearCart() }
    }
    
    @Test
    fun `error should be set when addToCart fails`() = runTest {
        // Given
        val errorMessage = "Failed to add to cart"
        coEvery { 
            localRepository.addToCart(any(), any(), any(), any(), any(), any()) 
        } returns Result.Error(errorMessage)
        
        // When
        viewModel.addToCart("test", "Test", 1.0)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(errorMessage, viewModel.error.value)
    }
    
    @Test
    fun `clearError should set error to null`() = runTest {
        // Given - set an error first
        coEvery { 
            localRepository.addToCart(any(), any(), any(), any(), any(), any()) 
        } returns Result.Error("Error")
        viewModel.addToCart("test", "Test", 1.0)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearError()
        
        // Then
        assertEquals(null, viewModel.error.value)
    }
}
