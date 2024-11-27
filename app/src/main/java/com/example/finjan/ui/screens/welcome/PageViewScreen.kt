package com.example.finjan.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finjan.model.PageItem
import com.example.finjan.ui.BorderButton
import com.example.finjan.ui.FilledButton
import com.example.finjan.ui.theme.PrimaryColor
import kotlinx.coroutines.launch
import com.example.finjan.R
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily

val PoppinsFontFamily = FontFamily.Default
val primaryFontColor = Color.Black
val secondaryFontColor = Color.Gray

@Composable
fun PageViewScreen(navController: NavController) {
    // Array of PageItem objects representing each page in the onboarding screen
    val list = arrayOf(
        PageItem(
            image = R.drawable.brewed_coffee,
            title = "Brewed to Perfection",
            subTitle = "Discover the best Coffee you could taste"
        ),
        PageItem(
            image = R.drawable.green_takeaway,
            title = "Naturally harvested",
            subTitle = "High Quality GMO-Free Beans"
        ),
        PageItem(
            image = R.drawable.heart_coffee,
            title = "Warm Coffee",
            subTitle = "At the palm of your hands"
        )
    )

    // Pager state to manage the horizontal pager
    val pagerState = rememberPagerState(pageCount = { list.size })

    // Coroutine scope for animations or asynchronous actions
    val scope = rememberCoroutineScope()

    // HorizontalPager to display the onboarding screens
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) { index -> // "index" represents the currently displayed page
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Display the image for the current page
            Image(
                painter = painterResource(id = list[index].image),
                contentDescription = null // No content description for decorative images
            )
            Spacer(modifier = Modifier.height(30.dp))

            // Display indicators to represent the current page position
            Indicator(count = list.size, index = index)

            Spacer(modifier = Modifier.height(35.dp))

            // Display the title of the current page
            Text(
                text = list[index].title,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontFamily = PoppinsFontFamily,
                    color = primaryFontColor
                )
            )
            Spacer(modifier = Modifier.height(33.dp))

            // Display the subtitle of the current page
            Text(
                text = list[index].subTitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = PoppinsFontFamily,
                    color = secondaryFontColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 45.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))

            FilledButton(
                modifier = Modifier.padding(horizontal = 34.dp),
                text = if (index < list.size - 1) "Next" else "Let's go" // Button text based on the page index
            ) {
                scope.launch {
                    if (index < list.size - 1) {
                        // Navigate to the next page if not the last page
                        pagerState.animateScrollToPage(index + 1)
                    } else {
                        // Navigate to the welcome screen if on the last page
                        navController.navigate("welcome_screen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display the "Skip" button on all pages except the last one
            if (index < list.size - 1) {
                BorderButton(
                    modifier = Modifier.padding(horizontal = 34.dp),
                    text = "Skip",
                    color = secondaryFontColor
                ) {
                    // Navigate to the welcome screen when "Skip" is clicked
                    navController.navigate("welcome_screen")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun Indicator(count: Int, index: Int) {
    // Row of indicators representing the pages
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp), // Fixed height for the row
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until count) {
            // Circular indicator for each page
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (i == index) PrimaryColor else Color.LightGray) // Highlight the active indicator
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}
