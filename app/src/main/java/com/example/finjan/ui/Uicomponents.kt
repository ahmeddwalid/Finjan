package com.example.finjan.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finjan.R
import com.example.finjan.ui.screens.welcome.primaryFontColor
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
    keyboardType: KeyboardType = KeyboardType.Text,
    action: ImeAction = ImeAction.Next,
    rounded: Int = 28,
    fontSize: Int = 14,
) {
    var textFieldState by remember {
        mutableStateOf("")
    }
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
        value = textFieldState,
        onValueChange = {
            textFieldState = it
        },
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