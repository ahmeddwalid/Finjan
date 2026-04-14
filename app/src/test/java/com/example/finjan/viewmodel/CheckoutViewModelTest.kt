package com.example.finjan.viewmodel

import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.repository.IFirestoreRepository
import com.example.finjan.data.repository.ILocalRepository
import com.example.finjan.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var localRepository: ILocalRepository
    private lateinit var firestoreRepository: IFirestoreRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        localRepository = mockk(relaxed = true)
        firestoreRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)

        coEvery { localRepository.getCartItems() } returns flowOf(Result.Success(emptyList()))
        coEvery { localRepository.getCartTotal() } returns flowOf(0.0)

        viewModel = CheckoutViewModel(localRepository, firestoreRepository, auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty cart and zero subtotal`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(emptyList<CartItemEntity>(), viewModel.cartItems.value)
        assertEquals(0.0, viewModel.subtotal.value, 0.01)
    }

    @Test
    fun `placeOrder should fail when user not signed in`() = runTest {
        every { auth.currentUser } returns null

        viewModel.placeOrder(paymentMethod = "card", pickupTime = "10:00 AM", total = 7.98)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Please sign in to place an order", viewModel.error.value)
    }

    @Test
    fun `placeOrder should succeed with valid user and items`() = runTest {
        val user: FirebaseUser = mockk(relaxed = true)
        every { auth.currentUser } returns user
        every { user.uid } returns "test-uid"

        val cartItems = listOf(
            CartItemEntity("p1", "Espresso", 3.99, 2, null, "")
        )
        coEvery { localRepository.getCartItems() } returns flowOf(Result.Success(cartItems))
        coEvery { localRepository.getCartTotal() } returns flowOf(7.98)
        coEvery { firestoreRepository.createOrder(any()) } returns Result.Success("order-123")
        coEvery { localRepository.clearCart() } returns Result.Success(Unit)

        viewModel = CheckoutViewModel(localRepository, firestoreRepository, auth)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.placeOrder(paymentMethod = "card", pickupTime = "10:00 AM", total = 7.98)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.orderSuccess.value)
    }

    @Test
    fun `clearError should reset error state`() = runTest {
        every { auth.currentUser } returns null
        viewModel.placeOrder(paymentMethod = "card", pickupTime = "10:00 AM", total = 7.98)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}
