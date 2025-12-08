package com.example.finjan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.finjan.navigation.NavigationManager
import com.example.finjan.navigation.Route
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.viewmodel.SharedViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        Firebase.initialize(this)
        auth = FirebaseAuth.getInstance()

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
                
                NavigationManager(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}