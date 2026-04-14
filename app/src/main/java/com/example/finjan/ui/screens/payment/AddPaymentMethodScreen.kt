package com.example.finjan.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.viewmodel.PaymentMethodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentMethodScreen(
    navController: NavController,
    viewModel: PaymentMethodViewModel = hiltViewModel()
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryMonth by remember { mutableStateOf("") }
    var expiryYear by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(success) {
        if (success) {
            scope.launch {
                snackbarHostState.showSnackbar("Payment method added successfully!")
            }
            navController.popBackStack()
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }
    
    // Validation
    val isCardNumberValid = cardNumber.length >= 16
    val isExpiryValid = expiryMonth.length == 2 && expiryYear.length == 2
    val isCvvValid = cvv.length >= 3
    val isFormValid = isCardNumberValid && cardHolderName.isNotBlank() && isExpiryValid && isCvvValid
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Payment Method",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = TextColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = detectCardBrand(cardNumber),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = TextColor
                            )
                        )
                    }
                    
                    Text(
                        text = formatCardNumber(cardNumber),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = TextColor,
                            letterSpacing = 2.sp
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "CARD HOLDER",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFontFamily,
                                    color = TextColor.copy(alpha = 0.7f)
                                )
                            )
                            Text(
                                text = cardHolderName.ifBlank { "YOUR NAME" },
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = TextColor
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EXPIRES",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFontFamily,
                                    color = TextColor.copy(alpha = 0.7f)
                                )
                            )
                            Text(
                                text = if (expiryMonth.isNotBlank() || expiryYear.isNotBlank()) 
                                    "${expiryMonth.padStart(2, '0')}/${expiryYear.padStart(2, '0')}" 
                                else "MM/YY",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = TextColor
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Card Number Input
            PaymentTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                label = "Card Number",
                placeholder = "1234 5678 9012 3456",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = SecondaryColor
                    )
                }
            )
            
            // Card Holder Name
            PaymentTextField(
                value = cardHolderName,
                onValueChange = { cardHolderName = it.uppercase() },
                label = "Card Holder Name",
                placeholder = "JOHN DOE"
            )
            
            // Expiry and CVV Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PaymentTextField(
                    value = expiryMonth,
                    onValueChange = { 
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                            val month = it.toIntOrNull() ?: 0
                            if (month <= 12) expiryMonth = it
                        }
                    },
                    label = "MM",
                    placeholder = "12",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                PaymentTextField(
                    value = expiryYear,
                    onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryYear = it },
                    label = "YY",
                    placeholder = "27",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                PaymentTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) cvv = it },
                    label = "CVV",
                    placeholder = "123",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SecondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Security Notice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SecondaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your payment info is secured with encryption",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PoppinsFontFamily,
                        color = SecondaryColor
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Add Card Button
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = PrimaryColor
                )
            } else {
                FilledButton(
                    text = "Add Card",
                    onClick = {
                        viewModel.addPaymentMethod(
                            cardNumber = cardNumber,
                            cardHolderName = cardHolderName,
                            expiryMonth = expiryMonth.toIntOrNull() ?: 0,
                            expiryYear = expiryYear.toIntOrNull() ?: 0,
                            cvv = cvv
                        )
                    },
                    enabled = isFormValid
                )
            }
        }
    }
}

@Composable
private fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = PoppinsFontFamily,
                    color = SecondaryColor
                )
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = PoppinsFontFamily,
                    color = SecondaryColor.copy(alpha = 0.5f)
                )
            )
        },
        leadingIcon = leadingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = SecondaryColor,
            focusedLabelColor = PrimaryColor,
            cursorColor = PrimaryColor,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

private fun formatCardNumber(number: String): String {
    val padded = number.padEnd(16, '•')
    return padded.chunked(4).joinToString(" ")
}

private fun detectCardBrand(number: String): String {
    return when {
        number.startsWith("4") -> "VISA"
        number.startsWith("5") -> "MASTERCARD"
        number.startsWith("3") -> "AMEX"
        number.startsWith("6") -> "DISCOVER"
        else -> "CARD"
    }
}
