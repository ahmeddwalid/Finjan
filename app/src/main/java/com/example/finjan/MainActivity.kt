package com.example.finjan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.finjan.ui.theme.FinjanTheme
import com.example.finjan.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {
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
