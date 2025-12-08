package com.example.finjan.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finjan.navigation.Route
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.components.CategoryChip
import com.example.finjan.ui.components.ImageCard
import com.example.finjan.ui.components.SearchBar
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.viewmodel.HomeViewModel
import com.example.finjan.viewmodel.SharedViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = viewModel()
) {
    val gridItems by homeViewModel.gridItems.collectAsState()
    val categories by homeViewModel.categories.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val filteredItems by homeViewModel.filteredItems.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Spacer(modifier = Modifier.size(50.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = { homeViewModel.updateSearchQuery(it) }
            )

            Spacer(modifier = Modifier.size(15.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category.name,
                        isSelected = selectedCategory == category.name,
                        onClick = { homeViewModel.selectCategory(category.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredItems) { item ->
                    ImageCard(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.description,
                        title = item.title,
                        modifier = Modifier.clickable {
                            navController.navigate(Route.ProductDetails(item.title))
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            FloatingNavigationBar(navController = navController)
        }
    }
}
