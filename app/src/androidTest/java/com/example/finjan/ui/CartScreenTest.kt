package com.example.finjan.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finjan.data.local.entity.CartItemEntity
import com.example.finjan.data.repository.LocalRepository
import com.example.finjan.ui.screens.cart.CartScreen
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.viewmodel.CartViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the Cart screen.
 */
@RunWith(AndroidJUnit4::class)
class CartScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun createMockCartViewModel(items: List<CartItemEntity> = emptyList()): CartViewModel {
        val mockRepository = mockk<LocalRepository>(relaxed = true)
        every { mockRepository.getCartItems() } returns flowOf(
            com.example.finjan.utils.Result.Success(items)
        )
        every { mockRepository.getCartTotal() } returns flowOf(
            items.sumOf { it.price * it.quantity }
        )
        every { mockRepository.getCartItemCount() } returns flowOf(items.size)
        
        return CartViewModel(mockRepository)
    }
    
    @Test
    fun cartScreen_displaysEmptyState_whenCartIsEmpty() {
        val viewModel = createMockCartViewModel()
        
        composeTestRule.setContent {
            FinjanTheme {
                CartScreen(
                    navController = rememberNavController(),
                    cartViewModel = viewModel
                )
            }
        }
        
        composeTestRule.onNodeWithText("Your cart is empty")
            .assertIsDisplayed()
    }
    
    @Test
    fun cartScreen_displaysCartItems_whenItemsExist() {
        val items = listOf(
            CartItemEntity(
                productId = "1",
                name = "Espresso",
                price = 4.99,
                quantity = 1,
                imageRes = null
            ),
            CartItemEntity(
                productId = "2",
                name = "Latte",
                price = 5.99,
                quantity = 2,
                imageRes = null
            )
        )
        val viewModel = createMockCartViewModel(items)
        
        composeTestRule.setContent {
            FinjanTheme {
                CartScreen(
                    navController = rememberNavController(),
                    cartViewModel = viewModel
                )
            }
        }
        
        // Wait for the UI to update
        composeTestRule.waitForIdle()
        
        // Verify items are displayed
        composeTestRule.onNodeWithText("Espresso", useUnmergedTree = true)
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Latte", useUnmergedTree = true)
            .assertIsDisplayed()
    }
    
    @Test
    fun cartScreen_displaysCheckoutButton_whenItemsExist() {
        val items = listOf(
            CartItemEntity(
                productId = "1",
                name = "Coffee",
                price = 4.99,
                quantity = 1,
                imageRes = null
            )
        )
        val viewModel = createMockCartViewModel(items)
        
        composeTestRule.setContent {
            FinjanTheme {
                CartScreen(
                    navController = rememberNavController(),
                    cartViewModel = viewModel
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Proceed to Checkout", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun cartScreen_displaysBackButton() {
        val viewModel = createMockCartViewModel()
        
        composeTestRule.setContent {
            FinjanTheme {
                CartScreen(
                    navController = rememberNavController(),
                    cartViewModel = viewModel
                )
            }
        }
        
        composeTestRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }
    
    @Test
    fun cartScreen_showsCorrectTotal() {
        val items = listOf(
            CartItemEntity(
                productId = "1",
                name = "Coffee",
                price = 5.00,
                quantity = 2,
                imageRes = null
            )
        )
        val viewModel = createMockCartViewModel(items)
        
        composeTestRule.setContent {
            FinjanTheme {
                CartScreen(
                    navController = rememberNavController(),
                    cartViewModel = viewModel
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Should show $10.00 total (5.00 * 2)
        composeTestRule.onNodeWithText("$10.00", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
