package com.example.finjan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.finjan.NavigationManager
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val viewModel: SharedViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            FinjanTheme {
                val navController = rememberNavController()
                val sharedViewModel = SharedViewModel()
                NavigationManager(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    startDestination = if (auth.currentUser != null) "home" else "splash_screen"
                )
            }
        }
    }
}