package com.example.finjan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.finjan.R
import com.example.finjan.ui.theme.AccentColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.TextColor
import com.example.finjan.utils.locale.LocaleManager

/**
 * Dialog for selecting app language with RTL indicator.
 */
@Composable
fun LanguageSelectionDialog(
    currentLanguage: LocaleManager.AppLanguage,
    useSystemLanguage: Boolean,
    onLanguageSelected: (LocaleManager.AppLanguage) -> Unit,
    onUseSystemLanguage: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    var useSystem by remember { mutableStateOf(useSystemLanguage) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.select_language),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // System language option
                LanguageOption(
                    displayName = stringResource(R.string.use_system_language),
                    nativeName = "",
                    isRtl = false,
                    isSelected = useSystem,
                    showRtlIndicator = false,
                    onClick = {
                        useSystem = true
                    }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = TextColor.copy(alpha = 0.1f)
                )
                
                // Language options
                LocaleManager.AppLanguage.entries.forEach { language ->
                    LanguageOption(
                        displayName = language.displayName,
                        nativeName = language.nativeName,
                        isRtl = language.isRtl,
                        isSelected = !useSystem && selectedLanguage == language,
                        showRtlIndicator = true,
                        onClick = {
                            useSystem = false
                            selectedLanguage = language
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = PoppinsFontFamily,
                            color = TextColor.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            if (useSystem) {
                                onUseSystemLanguage()
                            } else {
                                onLanguageSelected(selectedLanguage)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    displayName: String,
    nativeName: String,
    isRtl: Boolean,
    isSelected: Boolean,
    showRtlIndicator: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) PrimaryColor.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryColor,
                unselectedColor = TextColor.copy(alpha = 0.5f)
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = PoppinsFontFamily,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = TextColor
            )
            if (nativeName.isNotEmpty() && nativeName != displayName) {
                Text(
                    text = nativeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PoppinsFontFamily,
                    color = TextColor.copy(alpha = 0.6f)
                )
            }
        }
        
        if (showRtlIndicator && isRtl) {
            Text(
                text = "RTL",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                color = AccentColor,
                modifier = Modifier
                    .background(
                        AccentColor.copy(alpha = 0.15f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }
}
