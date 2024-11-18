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
    val list = arrayOf(
        PageItem(image = R.drawable.brewed_coffee, title = "Brewed to Perfection", subTitle = "Discover the best Coffee you could taste"), // <a href="https://www.vecteezy.com/free-vector/coffee-brewing">Coffee Brewing Vectors by Vecteezy</a>
        PageItem(image = R.drawable.green_takeaway, title = "Naturally harvested", subTitle = "High Quality GMO-Free Beans"), // https://www.svgrepo.com/svg/484760/takeaway-coffee
        PageItem(image = R.drawable.heart_coffee, title = "Warm Coffee", subTitle = "For your cozy days")
    )
    val pagerState = rememberPagerState(pageCount = {list.size})
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) { index ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(id = list[index].image), contentDescription = null)
            Spacer(modifier = Modifier.height(30.dp))
            Indicator(count = list.size, index = index)
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = list[index].title,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontFamily = PoppinsFontFamily,
                    color = primaryFontColor
                )
            )
            Spacer(modifier = Modifier.height(33.dp))
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
                text = if (index < list.size - 1) "Next" else "Begin"
            ) {
                scope.launch {
                    if (index < list.size - 1) {
                        pagerState.animateScrollToPage(index + 1)
                    } else {
                        navController.navigate("welcome_screen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            BorderButton(modifier = Modifier.padding(horizontal = 34.dp), text = "Skip", color = secondaryFontColor) {
                navController.navigate("welcome_screen")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun Indicator(count: Int, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until count) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (i == index) PrimaryColor else Color.LightGray)
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}
