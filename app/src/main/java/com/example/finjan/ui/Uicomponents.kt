package com.example.finjan.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.finjan.R
import com.example.finjan.ui.screens.welcome.primaryFontColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.TextColor
import kotlinx.coroutines.launch


@Composable
fun Logo(modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_color),
            contentDescription = "logo",
        )
//        Text(
//            text = buildAnnotatedString {
//                withStyle(
//                    style = SpanStyle(
//                        color = PrimaryColor,
//                    )
//                ) {
//                    append("Finjan")
//                }
//                append("Coffee")
//            })
    }
}

@Composable
fun BorderButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = PrimaryColor,
    fontSize: Int = 16,
    onClick: () -> Unit
) {
    OutlinedButton (
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(width = 1.dp, color = color),
        elevation = null
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = color,
                fontSize = fontSize.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun FilledButton(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Int = 16,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(PrimaryColor),
        shape = RoundedCornerShape(28.dp),
        elevation = null
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = TextColor,
                fontSize = fontSize.sp,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    hint: String,
    value: String, // Externally controlled value
    onValueChange: (String) -> Unit, // Callback to notify about changes
    keyboardType: KeyboardType = KeyboardType.Text,
    action: ImeAction = ImeAction.Next,
    rounded: Int = 28,
    fontSize: Int = 14,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .background(PrimaryColor, RoundedCornerShape(rounded.dp))
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        value = value, // Use externally controlled value
        onValueChange = onValueChange, // Notify about changes to the parent
        placeholder = {
            Text(
                text = hint,
                style = TextStyle(
                    color = TextColor,
                    fontSize = fontSize.sp,
                    fontFamily = PoppinsFontFamily
                )
            )
        },
        shape = RoundedCornerShape(rounded.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = action),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        textStyle = TextStyle(
            color = TextColor,
            fontSize = fontSize.sp,
            fontFamily = PoppinsFontFamily
        ),
        visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
    )
}


@Composable
fun Footer(text: String, textButton: String, onClick: @Composable () -> Unit, function: @Composable () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                color = SecondaryColor,
                fontFamily = PoppinsFontFamily
            )
        )
        TextButton(onClick = { onClick }) {
            Text(
                textButton,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = PrimaryColor,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun SplashScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_screen))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.size(500.dp),
        composition = composition,
        // iterations = LottieConstants.IterateForever
    )
}

@Composable
fun LoaderOne() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_beans_loading_zoom))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.size(500.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun LoaderTwo() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_beans_loading_jump))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.size(500.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun CoffeeCup() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee_cup))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.size(400.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun Latte() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.latte))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.size(400.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar() {
    var value by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 17.dp),
        value = value,
        onValueChange = {
            value = it
        },
        placeholder = {
            Text(
                text = "Search Finjan",
                style = TextStyle(
                    color = TextColor,
                    fontSize = 14.sp,
                    fontFamily = PoppinsFontFamily
                )
            )
        },
        shape = RoundedCornerShape(28.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                /** do something*/
                /** do something*/

                /** do something*/
                /** do something*/
                /** do something*/
                /** do something*/
                /** do something*/

                /** do something*/
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        textStyle = TextStyle(
            color = primaryFontColor,
            fontSize = 15.sp,
            fontFamily = PoppinsFontFamily
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon",
                modifier = Modifier.padding(start = 14.dp)
            )
        }
    )
}

@Composable
fun ImageCard (
    painter: Painter,
    contentDescription: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(modifier = Modifier.height(200.dp)) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 350f
                    )
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(title, style = TextStyle(color = Color.White, fontSize = 16.sp))
            }
        }
    }
}

@Composable
fun CategoriesElement(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )
        Text(
            stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.paddingFromBaseline(
                top = 24.dp, bottom = 8.dp
            )
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppTopBar(navController: NavController, title: String, action: Boolean = true) {
//
//    TopAppBar(
//        modifier = Modifier.background(BackgroundColor),
//        title = {
//            Text(
//                text = title,
//                style = TextStyle(
//                    fontSize = 24.sp,
//                    fontFamily = PoppinsFontFamily,
//                    color = primaryFontColor
//                )
//            )
//        },
//        actions = {
//            if (action) {
//                IconButton (
//                    onClick = { navController.navigate("settings_screen") },
//                ) {
//                    Icon(
//                        painter = painterResource(id = com.example.finjan.R.drawable.baseline_settings_24),
//                        contentDescription = "",
//                        tint = primaryFontColor
//                    )
//                }
//            }
//        },
//        // backgroundColor = Color.Transparent,
//    )
//}