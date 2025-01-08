package com.example.finjan.model

import androidx.compose.runtime.Composable

data class SettingItem(
    val title: String,
    val icon: Int,
    val description: String,
    val content: @Composable () -> Unit
)