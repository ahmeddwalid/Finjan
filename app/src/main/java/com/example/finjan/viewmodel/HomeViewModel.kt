package com.example.finjan.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.finjan.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GridItem(
    @DrawableRes val imageRes: Int,
    val description: String,
    val title: String
)

data class Category(val name: String)

class HomeViewModel : ViewModel() {
    private val _gridItems = MutableStateFlow(
        listOf(
            GridItem(R.drawable.otetein, "Item Description 1", "Two cute cats"),
            GridItem(R.drawable.otetein, "Item Description 2", "Otetein cute"),
            GridItem(R.drawable.otetein, "Item Description 3", "Herratan cute"),
            GridItem(R.drawable.otetein, "Item Description 4", "Bessetein cute"),
            GridItem(R.drawable.otetein, "Item Description 5", "Zwei süße Katzen"),
            GridItem(R.drawable.otetein, "Item Description 6", "Deux chats joli"),
            GridItem(R.drawable.otetein, "Item Description 7", "Dos bonitos gatos"),
            GridItem(R.drawable.otetein, "Item Description 8", "Cookies"),
            GridItem(R.drawable.hellothere, "Item Description 9", "Meow"),
        )
    )

    val gridItems: StateFlow<List<GridItem>> = _gridItems

    private val _categories = MutableStateFlow(
        listOf(
            Category("Coffee"),
            Category("Tea"),
            Category("Ice-cream"),
            Category("Cookies"),
            Category("Cakes")
        )
    )

    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    fun addItem(gridItem: GridItem) {
        _gridItems.value += gridItem
    }

    fun removeItem(title: String) {
        _gridItems.value = _gridItems.value.filterNot { it.title == title }
    }
}
