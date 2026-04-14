package com.example.finjan.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.finjan.navigation.NavigationManager
import com.example.finjan.navigation.Route
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.utils.DeepLinkManager
import com.example.finjan.viewmodel.SharedViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    
    // Deep link state
    private var pendingDeepLink by mutableStateOf<Route?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        Firebase.initialize(this)
        auth = FirebaseAuth.getInstance()
        
        // Handle deep link from launch intent
        handleDeepLink(intent)

        enableEdgeToEdge()
        setContent {
            FinjanTheme {
                val navController = rememberNavController()
                
                // Determine start destination based on auth state
                val startDestination: Route = if (auth.currentUser != null) {
                    Route.Home
                } else {
                    Route.Splash
                }
                
                // Handle pending deep link navigation
                LaunchedEffect(pendingDeepLink) {
                    pendingDeepLink?.let { route ->
                        // Only navigate if user is authenticated
                        if (auth.currentUser != null) {
                            navigateToDeepLink(navController, route)
                            pendingDeepLink = null
                        }
                    }
                }
                
                NavigationManager(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
    
    /**
     * Handle new intents when activity is in singleTask mode.
     * This is called when the app is already running and a deep link is clicked.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }
    
    /**
     * Parse deep link from intent and set pending navigation.
     */
    private fun handleDeepLink(intent: Intent?) {
        val result = DeepLinkManager.parseIntent(intent)
        val route = DeepLinkManager.toRoute(result)
        
        if (route != null && route != Route.Home) {
            pendingDeepLink = route
        }
    }
    
    /**
     * Navigate to a deep link destination.
     */
    private fun navigateToDeepLink(navController: NavHostController, route: Route) {
        // Navigate to the destination, clearing the back stack to Home
        navController.navigate(route) {
            popUpTo(Route.Home) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}