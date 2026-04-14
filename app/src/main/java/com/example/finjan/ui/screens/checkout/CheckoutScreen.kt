package com.example.finjan.ui.screens.checkout

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finjan.BuildConfig
import com.example.finjan.navigation.Route
import com.example.finjan.ui.components.FilledButton
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.SecondaryColor
import com.example.finjan.ui.theme.SuccessColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.viewmodel.CheckoutViewModel
import com.example.finjan.viewmodel.PaymentState
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val orderSuccess by viewModel.orderSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()
    
    var selectedPaymentMethod by remember { mutableStateOf("card") }
    var selectedPickupTime by remember { mutableStateOf("asap") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Tax and fees calculation
    val tax = subtotal * 0.08
    val serviceFee = 0.99
    val total = subtotal + tax + serviceFee

    // Stripe PaymentSheet
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                viewModel.onPaymentSuccess(
                    paymentMethod = selectedPaymentMethod,
                    pickupTime = selectedPickupTime,
                    total = total
                )
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.resetPaymentState()
            }
            is PaymentSheetResult.Failed -> {
                viewModel.onPaymentFailed(result.error.localizedMessage ?: "Payment failed")
            }
        }
    }

    // Launch PaymentSheet when the client secret is ready
    LaunchedEffect(paymentState) {
        val state = paymentState
        if (state is PaymentState.Ready) {
            val config = PaymentSheet.Configuration.Builder("Finjan Coffee")
                .build()
            paymentSheet.presentWithPaymentIntent(state.clientSecret, config)
        }
    }
    
    LaunchedEffect(orderSuccess) {
        if (orderSuccess != null) {
            navController.navigate(Route.OrderTracking(orderSuccess!!)) {
                popUpTo(Route.Cart) { inclusive = true }
            }
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    LaunchedEffect(paymentState) {
        val state = paymentState
        if (state is PaymentState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar(state.message)
            }
            viewModel.resetPaymentState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        style = TextStyle(
                            fontSize = 24.sp,
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
                .padding(horizontal = 16.dp)
        ) {
            // Order Summary Card
            SectionTitle("Order Summary")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.name}",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PoppinsFontFamily,
                                    color = PrimaryColor
                                )
                            )
                            Text(
                                text = "$${String.format("%.2f", item.price * item.quantity)}",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = AccentColor
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Pickup Time
            SectionTitle("Pickup Time")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PickupOption(
                        title = "As Soon As Possible",
                        subtitle = "15-20 minutes",
                        icon = Icons.Default.Schedule,
                        selected = selectedPickupTime == "asap",
                        onClick = { selectedPickupTime = "asap" }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PickupOption(
                        title = "Schedule for Later",
                        subtitle = "Choose a time",
                        icon = Icons.Default.Schedule,
                        selected = selectedPickupTime == "scheduled",
                        onClick = { selectedPickupTime = "scheduled" }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Payment Method
            SectionTitle("Payment Method")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PaymentOption(
                        title = "Credit/Debit Card",
                        subtitle = "•••• 1234",
                        icon = Icons.Default.CreditCard,
                        selected = selectedPaymentMethod == "card",
                        onClick = { selectedPaymentMethod = "card" }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentOption(
                        title = "Pay at Pickup",
                        subtitle = "Cash or card",
                        icon = Icons.Default.LocationOn,
                        selected = selectedPaymentMethod == "pickup",
                        onClick = { selectedPaymentMethod = "pickup" }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Price Breakdown
            SectionTitle("Price Details")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PriceRow("Subtotal", subtotal)
                    PriceRow("Tax (8%)", tax)
                    PriceRow("Service Fee", serviceFee)
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = SecondaryColor.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor
                            )
                        )
                        Text(
                            text = "$${String.format("%.2f", total)}",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AccentColor
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Place Order Button
            if (isLoading || paymentState is PaymentState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                FilledButton(
                    text = "Place Order - $${String.format("%.2f", total)}",
                    onClick = {
                        if (selectedPaymentMethod == "card") {
                            // Convert total to cents for Stripe
                            val amountCents = (total * 100).toLong()
                            viewModel.startPayment(amountCents)
                        } else {
                            // Pay at pickup — place order directly
                            viewModel.placeOrder(
                                paymentMethod = selectedPaymentMethod,
                                pickupTime = selectedPickupTime,
                                total = total
                            )
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 18.sp,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsFontFamily,
                color = SecondaryColor
            )
        )
        Text(
            text = "$${String.format("%.2f", amount)}",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PoppinsFontFamily,
                color = PrimaryColor
            )
        )
    }
}

@Composable
private fun PickupOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = AccentColor,
                unselectedColor = SecondaryColor
            )
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) AccentColor else SecondaryColor,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PoppinsFontFamily,
                    color = SecondaryColor
                )
            )
        }
    }
}

@Composable
private fun PaymentOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = AccentColor,
                unselectedColor = SecondaryColor
            )
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) AccentColor else SecondaryColor,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PoppinsFontFamily,
                    color = SecondaryColor
                )
            )
        }
    }
}
