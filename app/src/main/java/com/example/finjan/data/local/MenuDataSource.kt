package com.example.finjan.data.local

import com.example.finjan.R
import com.example.finjan.viewmodel.Category
import com.example.finjan.viewmodel.GridItem
import com.example.finjan.viewmodel.Offer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for static menu items, categories, and offers.
 * Centralizes hardcoded data for easier maintenance and future migration to remote sources.
 */
@Singleton
class MenuDataSource @Inject constructor() {

    fun getMenuItems(): List<GridItem> = listOf(
        GridItem("1", R.drawable.icecoffee_creamymixednuts, "Creamy mixed nuts ice coffee", "Ice Coffee Mixed Nuts", "Coffee", 5.99),
        GridItem("2", R.drawable.mocha_icecoffee, "Rich mocha ice coffee", "Coffee Mocha", "Coffee", 4.99),
        GridItem("3", R.drawable.icecoffee_boba, "Refreshing boba ice coffee", "Ice Coffee Boba", "Coffee", 6.49),
        GridItem("4", R.drawable.chocolate_milkshake, "Creamy chocolate milkshake", "Chocolate Milkshake", "Ice-cream", 5.49),
        GridItem("5", R.drawable.chocolate_icecream, "Rich chocolate ice cream", "Chocolate Ice Cream", "Ice-cream", 4.49),
        GridItem("6", R.drawable.cookies_darker, "Fresh baked cookies", "Cookies", "Cookies", 3.99),
        GridItem("7", R.drawable.espresso, "Strong Italian espresso", "Espresso", "Coffee", 3.49),
        GridItem("8", R.drawable.otetein, "Special oat protein blend", "Oat Protein", "Coffee", 5.99),
        GridItem("9", R.drawable.hellothere, "Cat cafe special", "Meow Special", "Coffee", 4.99),
    )

    fun getCategories(): List<Category> = listOf(
        Category("All"),
        Category("Coffee"),
        Category("Tea"),
        Category("Ice-cream"),
        Category("Cookies"),
        Category("Cakes"),
    )

    fun getOffers(): List<Offer> = listOf(
        Offer(
            id = "1",
            title = "Morning Special",
            description = "Get any coffee before 10 AM",
            discount = "20% OFF"
        ),
        Offer(
            id = "2",
            title = "Buy 2 Get 1 Free",
            description = "On all cookies and pastries",
            discount = "FREE"
        ),
        Offer(
            id = "3",
            title = "Weekend Treat",
            description = "Any milkshake on Saturday & Sunday",
            discount = "15% OFF"
        ),
        Offer(
            id = "4",
            title = "Loyalty Reward",
            description = "After 10 purchases",
            discount = "50% OFF"
        ),
        Offer(
            id = "5",
            title = "Student Discount",
            description = "Show your student ID",
            discount = "10% OFF"
        ),
    )
}
