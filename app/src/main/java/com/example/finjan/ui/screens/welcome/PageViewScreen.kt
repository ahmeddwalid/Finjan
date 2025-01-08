package com.example.finjan.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.model.PageItem
import com.example.finjan.ui.components.BorderButton
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import kotlinx.coroutines.launch

@Composable
fun PageViewScreen(navController: NavController) {
    val pages = listOf(
        PageItem(
            image = R.drawable.brewed_coffee,
            title = "Brewed to Perfection",
            subtitle = "Discover the best Coffee you could taste"
        ),
        PageItem(
            image = R.drawable.green_takeaway,
            title = "Naturally harvested",
            subtitle = "High Quality GMO-Free Beans"
        ),
        PageItem(
            image = R.drawable.heart_coffee,
            title = "Warm Coffee",
            subtitle = "At the palm of your hands"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = pages[page].image),
                    contentDescription = null,
                    modifier = Modifier.padding(32.dp)
                )

                Text(
                    text = pages[page].title,
                    style = TextStyle(
                        fontSize = 28.sp,
                        color = PrimaryColor
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[page].subtitle,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = SecondaryColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                PrimaryColor
                            else
                                Color.LightGray
                        )
                )
            }
        }

        // Buttons
        FilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next"
        ) {
            scope.launch {
                if (pagerState.currentPage < pages.size - 1) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } else {
                    navController.navigate("welcome_screen")
                }
            }
        }

        if (pagerState.currentPage < pages.size - 1) {
            Spacer(modifier = Modifier.height(8.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = "Skip",
                color = SecondaryColor
            ) {
                navController.navigate("welcome_screen")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}