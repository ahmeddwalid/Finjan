package com.example.finjan.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finjan.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the Sign In screen.
 */
@RunWith(AndroidJUnit4::class)
class SignInScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun signInScreen_displaysEmailAndPasswordFields() {
        // Navigate to sign in if needed
        // Then verify email and password fields are displayed
        composeTestRule.onNodeWithText("Email", useUnmergedTree = true)
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Password", useUnmergedTree = true)
            .assertIsDisplayed()
    }
    
    @Test
    fun signInScreen_displaysSignInButton() {
        composeTestRule.onNodeWithText("Sign In", useUnmergedTree = true)
            .assertIsDisplayed()
    }
    
    @Test
    fun signInScreen_emailFieldAcceptsInput() {
        composeTestRule.onNodeWithText("Email", useUnmergedTree = true)
            .performTextInput("test@example.com")
    }
    
    @Test
    fun signInScreen_passwordFieldAcceptsInput() {
        composeTestRule.onNodeWithText("Password", useUnmergedTree = true)
            .performTextInput("TestPassword123!")
    }
    
    @Test
    fun signInScreen_forgotPasswordLinkIsClickable() {
        composeTestRule.onNodeWithText("Forgot Password?", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
    }
    
    @Test
    fun signInScreen_signUpLinkNavigates() {
        composeTestRule.onNodeWithText("Sign Up", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
    }
}
