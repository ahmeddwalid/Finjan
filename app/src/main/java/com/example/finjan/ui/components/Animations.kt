package com.example.finjan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.finjan.R

/**
 * App logo component.
 */
@Composable
fun Logo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_color),
            contentDescription = "Finjan Logo",
        )
    }
}

/**
 * Splash screen animation.
 */
@Composable
fun SplashAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_screen))
    LottieAnimation(
        modifier = modifier.size(500.dp),
        composition = composition,
    )
}

/**
 * Coffee beans loading animation (zoom variant).
 */
@Composable
fun LoaderOne(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_beans_loading_zoom))
    LottieAnimation(
        modifier = modifier.size(500.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

/**
 * Coffee beans loading animation (jump variant).
 */
@Composable
fun LoaderTwo(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_beans_loading_jump))
    LottieAnimation(
        modifier = modifier.size(500.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

/**
 * Animated coffee cup.
 */
@Composable
fun CoffeeCup(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_cup))
    LottieAnimation(
        modifier = modifier.size(400.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

/**
 * Latte animation for offers screen.
 */
@Composable
fun Latte(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.latte))
    LottieAnimation(
        modifier = modifier.size(400.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}
