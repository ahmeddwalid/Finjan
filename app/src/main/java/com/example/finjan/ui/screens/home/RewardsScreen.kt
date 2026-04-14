package com.example.finjan.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finjan.ui.FloatingNavigationBar
import com.example.finjan.ui.components.QrCodeDisplay
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.viewmodel.OffersViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RewardsScreen(
    navController: NavController,
    offersViewModel: OffersViewModel = hiltViewModel()
) {
    val offers by offersViewModel.offers.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabs = listOf("QR Code", "Offers")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Rewards",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = BackgroundColor,
            contentColor = PrimaryColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = AccentColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (pagerState.currentPage == index) PrimaryColor else SecondaryColor
                            )
                        )
                    }
                )
            }
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> QrCodeTab()
                1 -> OffersTab(offers)
            }
        }

        FloatingNavigationBar(navController = navController)
    }
}

@Composable
private fun QrCodeTab() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Claim Points",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = PrimaryColor
                ),
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = "✦ Scan at Register ✦",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryColor
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            QrCodeDisplay(
                content = "finjan:user:$userId",
                modifier = Modifier.size(280.dp)
            )
        }
    }
}

@Composable
private fun OffersTab(offers: List<com.example.finjan.viewmodel.Offer>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(offers) { offer ->
            OfferCard(
                title = offer.title,
                description = offer.description,
                discount = offer.discount
            )
        }
    }
}

@Composable
private fun OfferCard(
    title: String,
    description: String,
    discount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFontFamily,
                        color = TextColor.copy(alpha = 0.8f)
                    )
                )
            }

            Text(
                text = discount,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryColor
                ),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
