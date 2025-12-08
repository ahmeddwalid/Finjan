package com.example.finjan.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.utils.security.InputValidator
import com.example.finjan.utils.security.ValidationResult

/**
 * Email input field with validation.
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                error = InputValidator.validateEmail(newValue).errorMessage
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, fontFamily = PoppinsFontFamily) },
            enabled = enabled,
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            textStyle = TextStyle(
                fontFamily = PoppinsFontFamily,
                fontSize = 16.sp,
                color = PrimaryColor
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondaryColor,
                errorBorderColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        ValidationError(error)
    }
}

/**
 * Password input field with visibility toggle and validation.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    enabled: Boolean = true,
    validateStrength: Boolean = true,
    imeAction: ImeAction = ImeAction.Done
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                error = if (validateStrength && newValue.isNotEmpty()) {
                    InputValidator.validatePassword(newValue).errorMessage
                } else null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, fontFamily = PoppinsFontFamily) },
            enabled = enabled,
            isError = error != null,
            singleLine = true,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = SecondaryColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() },
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            textStyle = TextStyle(
                fontFamily = PoppinsFontFamily,
                fontSize = 16.sp,
                color = PrimaryColor
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondaryColor,
                errorBorderColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        ValidationError(error)
        
        if (validateStrength && value.isNotEmpty()) {
            PasswordStrengthIndicator(value)
        }
    }
}

/**
 * Name input field with validation.
 */
@Composable
fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Name",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val sanitized = InputValidator.sanitizeInput(newValue)
                onValueChange(sanitized)
                error = InputValidator.validateName(sanitized).errorMessage
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, fontFamily = PoppinsFontFamily) },
            enabled = enabled,
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            textStyle = TextStyle(
                fontFamily = PoppinsFontFamily,
                fontSize = 16.sp,
                color = PrimaryColor
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondaryColor,
                errorBorderColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        ValidationError(error)
    }
}

/**
 * Display validation error message.
 */
@Composable
private fun ValidationError(error: String?) {
    AnimatedVisibility(
        visible = error != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Text(
            text = error ?: "",
            color = Color.Red,
            fontSize = 12.sp,
            fontFamily = PoppinsFontFamily,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

/**
 * Password strength indicator.
 */
@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val (color, label) = when {
        strength >= 4 -> Color(0xFF4CAF50) to "Strong"
        strength >= 3 -> Color(0xFFFF9800) to "Medium"
        strength >= 2 -> Color(0xFFFF5722) to "Weak"
        else -> Color.Red to "Very Weak"
    }

    Spacer(modifier = Modifier.height(4.dp))
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Progress bar
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.LightGray, RoundedCornerShape(2.dp))
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = strength / 5f)
                    .height(4.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = "Password strength: $label",
            fontSize = 10.sp,
            fontFamily = PoppinsFontFamily,
            color = color
        )
    }
}

/**
 * Calculate password strength score (0-5).
 */
private fun calculatePasswordStrength(password: String): Int {
    var score = 0
    
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    
    return score
}
