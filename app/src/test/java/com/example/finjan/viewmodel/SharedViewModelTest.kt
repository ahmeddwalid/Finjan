package com.example.finjan.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: SharedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        auth = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserData should populate user fields when signed in`() = runTest {
        val user: FirebaseUser = mockk(relaxed = true)
        every { auth.currentUser } returns user
        every { user.displayName } returns "Ahmed"
        every { user.email } returns "ahmed@test.com"
        every { user.photoUrl } returns null

        viewModel = SharedViewModel(auth)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Ahmed", viewModel.userName.value)
        assertEquals("ahmed@test.com", viewModel.userEmail.value)
    }

    @Test
    fun `loadUserData should use email prefix when displayName is null`() = runTest {
        val user: FirebaseUser = mockk(relaxed = true)
        every { auth.currentUser } returns user
        every { user.displayName } returns null
        every { user.email } returns "user@example.com"

        viewModel = SharedViewModel(auth)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("user", viewModel.userName.value)
    }

    @Test
    fun `loadUserData should handle null user gracefully`() = runTest {
        every { auth.currentUser } returns null

        viewModel = SharedViewModel(auth)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.userName.value)
        assertEquals("", viewModel.userEmail.value)
    }

    @Test
    fun `addPoints should increase loyalty points`() = runTest {
        every { auth.currentUser } returns null
        viewModel = SharedViewModel(auth)

        viewModel.addPoints(100)
        assertEquals(100, viewModel.loyaltyPoints.value)

        viewModel.addPoints(50)
        assertEquals(150, viewModel.loyaltyPoints.value)
    }

    @Test
    fun `redeemPoints should succeed when sufficient points`() = runTest {
        every { auth.currentUser } returns null
        viewModel = SharedViewModel(auth)

        viewModel.addPoints(200)
        val result = viewModel.redeemPoints(100)

        assertTrue(result)
        assertEquals(100, viewModel.loyaltyPoints.value)
    }

    @Test
    fun `redeemPoints should fail when insufficient points`() = runTest {
        every { auth.currentUser } returns null
        viewModel = SharedViewModel(auth)

        viewModel.addPoints(50)
        val result = viewModel.redeemPoints(100)

        assertFalse(result)
        assertEquals(50, viewModel.loyaltyPoints.value)
    }
}
