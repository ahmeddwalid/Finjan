package com.example.finjan.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.model.BottomNavItem
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.components.ImageCard
import com.example.finjan.ui.components.SearchBar
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.FinjanTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.finjan.ui.components.CategoryChip
import com.example.finjan.viewmodel.HomeViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    FinjanTheme {
        val items = listOf(
            BottomNavItem(icon = R.drawable.ic_home, route = "home"),
            BottomNavItem(icon = R.drawable.ic_qr_code, route = "qrcode"),
            BottomNavItem(icon = R.drawable.ic_shopping_bag, route = "offers"),
            BottomNavItem(icon = R.drawable.ic_profile, route = "profile")
        )

        val gridItems = viewModel.gridItems.collectAsState().value
        val categories = viewModel.categories.collectAsState().value

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                Spacer(modifier = Modifier.size(50.dp))

                SearchBar()

                Spacer(modifier = Modifier.size(15.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(category.name
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
                    items(gridItems) { item ->
                        ImageCard(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.description,
                            title = item.title
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                FloatingNavigationBar(navController = navController, items = items)
            }
        }
    }
}
