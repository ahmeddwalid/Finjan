package com.example.finjan.viewmodel

import com.example.finjan.data.local.MenuDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    private val menuDataSource = MenuDataSource()

    @Test
    fun `initial gridItems should contain all menu items`() {
        val viewModel = HomeViewModel(menuDataSource)
        assertEquals(9, viewModel.gridItems.value.size)
    }

    @Test
    fun `initial filteredItems should contain all items`() {
        val viewModel = HomeViewModel(menuDataSource)
        assertEquals(9, viewModel.filteredItems.value.size)
    }

    @Test
    fun `selectCategory Coffee should filter coffee items only`() {
        val viewModel = HomeViewModel(menuDataSource)

        viewModel.selectCategory("Coffee")

        val filtered = viewModel.filteredItems.value
        assertTrue(filtered.isNotEmpty())
        assertTrue(filtered.all { it.category == "Coffee" })
    }

    @Test
    fun `selectCategory same category twice should reset to All`() {
        val viewModel = HomeViewModel(menuDataSource)

        viewModel.selectCategory("Coffee")
        viewModel.selectCategory("Coffee")

        assertEquals("All", viewModel.selectedCategory.value)
        assertEquals(9, viewModel.filteredItems.value.size)
    }

    @Test
    fun `updateSearchQuery should filter items by title`() {
        val viewModel = HomeViewModel(menuDataSource)

        viewModel.updateSearchQuery("Espresso")

        val filtered = viewModel.filteredItems.value
        assertEquals(1, filtered.size)
        assertEquals("Espresso", filtered[0].title)
    }

    @Test
    fun `updateSearchQuery should filter items by description`() {
        val viewModel = HomeViewModel(menuDataSource)

        viewModel.updateSearchQuery("chocolate")

        val filtered = viewModel.filteredItems.value
        assertTrue(filtered.all {
            it.title.lowercase().contains("chocolate") ||
            it.description.lowercase().contains("chocolate")
        })
    }

    @Test
    fun `clearFilters should reset search and category`() {
        val viewModel = HomeViewModel(menuDataSource)

        viewModel.selectCategory("Coffee")
        viewModel.updateSearchQuery("mocha")
        viewModel.clearFilters()

        assertEquals("All", viewModel.selectedCategory.value)
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(9, viewModel.filteredItems.value.size)
    }

    @Test
    fun `getItemById should return correct item`() {
        val viewModel = HomeViewModel(menuDataSource)

        val item = viewModel.getItemById("7")
        assertEquals("Espresso", item?.title)
    }

    @Test
    fun `getItemById should return null for unknown id`() {
        val viewModel = HomeViewModel(menuDataSource)

        val item = viewModel.getItemById("999")
        assertEquals(null, item)
    }
}
