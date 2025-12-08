package com.example.finjan.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import com.example.finjan.ui.components.MessageType

/**
 * Full-screen loading overlay with progress indicator.
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    message: String = "Loading..."
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PoppinsFontFamily,
                            color = PrimaryColor
                        )
                    )
                }
            }
        }
    }
}

/**
 * Network error banner shown when offline.
 */
@Composable
fun NetworkErrorBanner(
    isOffline: Boolean,
    onRetry: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE53935))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "No Internet",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No internet connection",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color.White
                        )
                    )
                }
                onRetry?.let {
                    TextButton(onClick = it) {
                        Text(
                            text = "Retry",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Toast-style message banner.
 */
@Composable
fun MessageBanner(
    message: String,
    type: MessageType = MessageType.INFO,
    isVisible: Boolean,
    onDismiss: () -> Unit = {}
) {
    val backgroundColor = when (type) {
        MessageType.ERROR -> Color(0xFFE53935)
        MessageType.SUCCESS -> Color(0xFF4CAF50)
        MessageType.WARNING -> Color(0xFFFFA000)
        MessageType.INFO -> Color(0xFF1976D2)
    }
    
    val icon = when (type) {
        MessageType.ERROR -> Icons.Filled.Warning
        MessageType.SUCCESS -> Icons.Filled.CheckCircle
        MessageType.WARNING -> Icons.Filled.Warning
        MessageType.INFO -> Icons.Outlined.Info
    }

    AnimatedVisibility(
        visible = isVisible && message.isNotEmpty(),
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable { onDismiss() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Empty state placeholder with icon and message.
 */
@Composable
fun EmptyState(
    message: String,
    icon: Int? = null,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = PrimaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = PrimaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (title != null) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Text(
            text = message,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PoppinsFontFamily,
                color = PrimaryColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            FilledButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

/**
 * Error state with retry button.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PoppinsFontFamily,
                color = PrimaryColor,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.width(200.dp)
        )
    }
}
