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
            GridItem(R.drawable.icecoffee_creamymixednuts, "Item Description 1", "Ice Coffee mixed nuts"),
            GridItem(R.drawable.mocha_icecoffee, "Item Description 2", "Cofee mocha"),
            GridItem(R.drawable.icecoffee_boba, "Item Description 3", "Ice coffee Boba"),
            GridItem(R.drawable.chocolate_milkshake, "Item Description 4", "Chocolate Milkshake"),
            GridItem(R.drawable.chocolate_icecream, "Item Description 5", "Chocolate Icecream"),
            GridItem(R.drawable.cookies_darker, "Item Description 6", "Cookies"),
            GridItem(R.drawable.espresso, "Item Description 7", "Espresso"),
            GridItem(R.drawable.otetein, "Item Description 8", "Otetein"),
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
