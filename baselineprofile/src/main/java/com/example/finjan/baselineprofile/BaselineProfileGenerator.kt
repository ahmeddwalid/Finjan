package com.example.finjan.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile Generator for Finjan app.
 * 
 * This test generates a baseline profile which can be used to improve
 * app startup time by precompiling critical code paths.
 * 
 * Run with: ./gradlew :baselineprofile:connectedAndroidTest
 * 
 * The generated profile will be copied to app/src/main/baseline-prof.txt
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateStartupProfile() {
        rule.collect(
            packageName = "com.example.finjan",
            includeInStartupProfile = true
        ) {
            // Start the app
            pressHome()
            startActivityAndWait()
            
            // Wait for splash screen to finish
            device.wait(Until.hasObject(By.res("home_screen")), 5_000)
        }
    }

    @Test
    fun generateCriticalUserJourneyProfile() {
        rule.collect(
            packageName = "com.example.finjan",
            includeInStartupProfile = true
        ) {
            // Cold start
            pressHome()
            startActivityAndWait()
            
            // Wait for content to load
            device.wait(Until.hasObject(By.res("home_screen")), 5_000)
            
            // Scroll through home content
            device.findObject(By.scrollable(true))?.apply {
                scroll(Direction.DOWN, 0.5f)
                scroll(Direction.UP, 0.5f)
            }
            
            // Navigate to offers
            device.findObject(By.res("nav_offers"))?.click()
            device.waitForIdle()
            
            // Navigate to QR code
            device.findObject(By.res("nav_qr"))?.click()
            device.waitForIdle()
            
            // Navigate to profile
            device.findObject(By.res("nav_profile"))?.click()
            device.waitForIdle()
            
            // Return to home
            device.findObject(By.res("nav_home"))?.click()
            device.waitForIdle()
        }
    }
    
    @Test
    fun generateProductBrowsingProfile() {
        rule.collect(
            packageName = "com.example.finjan",
            includeInStartupProfile = false
        ) {
            // Start app
            pressHome()
            startActivityAndWait()
            
            // Wait for home to load
            device.wait(Until.hasObject(By.res("home_screen")), 5_000)
            
            // Click on a product card (if visible)
            device.findObject(By.res("product_card"))?.click()
            device.waitForIdle()
            
            // Go back
            device.pressBack()
            device.waitForIdle()
            
            // Navigate to cart
            device.findObject(By.res("nav_cart"))?.click()
            device.waitForIdle()
            
            // Navigate to favorites
            device.findObject(By.res("nav_favorites"))?.click()
            device.waitForIdle()
        }
    }
}
