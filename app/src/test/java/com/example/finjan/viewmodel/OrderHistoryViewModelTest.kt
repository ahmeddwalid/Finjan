package com.example.finjan.viewmodel

import com.example.finjan.data.model.Order
import com.example.finjan.data.model.OrderItem
import com.example.finjan.data.model.OrderStatus
import com.example.finjan.data.repository.FirestoreRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class OrderHistoryViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var firestoreRepository: FirestoreRepository
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        firestoreRepository = mockk(relaxed = true)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `getOrderHistory should return list of orders`() = runTest {
        // Given
        val mockOrders = listOf(
            Order(
                id = "order-1",
                userId = "user-1",
                items = listOf(
                    OrderItem(
                        menuItemId = "item-1",
                        name = "Espresso",
                        quantity = 2,
                        price = 3.99
                    )
                ),
                total = 7.98,
                status = OrderStatus.COMPLETED.name
            ),
            Order(
                id = "order-2",
                userId = "user-1",
                items = listOf(
                    OrderItem(
                        menuItemId = "item-2",
                        name = "Latte",
                        quantity = 1,
                        price = 4.99
                    )
                ),
                total = 4.99,
                status = OrderStatus.PENDING.name
            )
        )
        
        coEvery { firestoreRepository.getOrderHistory(any()) } returns mockOrders
        
        // When
        val result = firestoreRepository.getOrderHistory(limit = 20)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("order-1", result[0].id)
        assertEquals(OrderStatus.COMPLETED.name, result[0].status)
    }
    
    @Test
    fun `empty order history should return empty list`() = runTest {
        // Given
        coEvery { firestoreRepository.getOrderHistory(any()) } returns emptyList()
        
        // When
        val result = firestoreRepository.getOrderHistory()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `order total should be calculated correctly`() = runTest {
        // Given
        val orderItem1 = OrderItem(
            menuItemId = "item-1",
            name = "Coffee",
            quantity = 2,
            price = 3.50
        )
        val orderItem2 = OrderItem(
            menuItemId = "item-2",
            name = "Muffin",
            quantity = 1,
            price = 2.99
        )
        
        // When
        val total = (orderItem1.price * orderItem1.quantity) + (orderItem2.price * orderItem2.quantity)
        
        // Then
        assertEquals(9.99, total, 0.01)
    }
}
