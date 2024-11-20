package com.example.finjan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.finjan.NavigationManager
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.viewmodel.MainViewModel
import com.example.finjan.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinjanTheme {
                val navController = rememberNavController()
                val sharedViewModel = SharedViewModel()
                NavigationManager(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }
}
