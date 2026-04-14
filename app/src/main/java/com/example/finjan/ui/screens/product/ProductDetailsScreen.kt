package com.example.finjan.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finjan.R
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.viewmodel.ProductDetailsViewModel
import kotlinx.coroutines.launch

/**
 * Product Details Screen showing full product info with customization options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productId: String,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var quantity by remember { mutableIntStateOf(1) }
    var selectedSize by remember { mutableStateOf("Medium") }
    var selectedMilk by remember { mutableStateOf("Regular") }
    var selectedSweetness by remember { mutableStateOf("Normal") }
    
    val sizes = listOf("Small", "Medium", "Large")
    val milkOptions = listOf("Regular", "Oat", "Almond", "Soy", "None")
    val sweetnessLevels = listOf("None", "Light", "Normal", "Extra")
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Calculate price based on size
    val basePrice = product?.price ?: 4.99
    val sizeMultiplier = when (selectedSize) {
        "Small" -> 0.85
        "Large" -> 1.25
        else -> 1.0
    }
    val totalPrice = basePrice * sizeMultiplier * quantity
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) AccentColor else PrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val customizations = buildString {
                        append("Size: $selectedSize")
                        append(", Milk: $selectedMilk")
                        append(", Sweetness: $selectedSweetness")
                    }
                    viewModel.addToCart(
                        quantity = quantity,
                        size = selectedSize,
                        milk = selectedMilk,
                        sweetness = selectedSweetness
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Added to cart!")
                    }
                },
                containerColor = PrimaryColor,
                contentColor = TextColor
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Add to cart"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (product?.imageUrl != null) {
                    AsyncImage(
                        model = product?.imageUrl,
                        contentDescription = product?.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback placeholder
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = SecondaryColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product?.title ?: productId,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                    Text(
                        text = "$${String.format("%.2f", totalPrice)}",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AccentColor
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = product?.description ?: "A delicious handcrafted coffee beverage made with premium ingredients.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFontFamily,
                        color = SecondaryColor,
                        lineHeight = 22.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Size Selection
                SectionTitle("Size")
                ChipGroup(
                    options = sizes,
                    selected = selectedSize,
                    onSelectionChange = { selectedSize = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Milk Selection
                SectionTitle("Milk")
                ChipGroup(
                    options = milkOptions,
                    selected = selectedMilk,
                    onSelectionChange = { selectedMilk = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sweetness Selection
                SectionTitle("Sweetness")
                ChipGroup(
                    options = sweetnessLevels,
                    selected = selectedSweetness,
                    onSelectionChange = { selectedSweetness = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quantity Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryColor
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuantityButton(
                            icon = Icons.Default.Remove,
                            onClick = { if (quantity > 1) quantity-- }
                        )
                        Text(
                            text = quantity.toString(),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor
                            ),
                            modifier = Modifier.width(32.dp)
                        )
                        QuantityButton(
                            icon = Icons.Default.Add,
                            onClick = { if (quantity < 10) quantity++ }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Add to Cart Button
                FilledButton(
                    text = "Add to Cart - $${String.format("%.2f", totalPrice)}",
                    onClick = {
                        val customizations = buildString {
                            append("Size: $selectedSize")
                            append(", Milk: $selectedMilk")
                            append(", Sweetness: $selectedSweetness")
                        }
                        viewModel.addToCart(
                            quantity = quantity,
                            size = selectedSize,
                            milk = selectedMilk,
                            sweetness = selectedSweetness
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Added to cart!")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 16.sp,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ChipGroup(
    options: List<String>,
    selected: String,
    onSelectionChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelectionChange(option) },
                label = {
                    Text(
                        text = option,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PoppinsFontFamily
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryColor,
                    selectedLabelColor = TextColor,
                    containerColor = BackgroundColor,
                    labelColor = PrimaryColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = SecondaryColor,
                    selectedBorderColor = PrimaryColor,
                    enabled = true,
                    selected = selected == option
                )
            )
        }
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(PrimaryColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
