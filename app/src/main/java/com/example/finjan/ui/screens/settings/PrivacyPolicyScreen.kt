package com.example.finjan.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finjan.R
import com.example.finjan.ui.theme.BackgroundColor
import com.example.finjan.ui.theme.PoppinsFontFamily
import com.example.finjan.ui.theme.PrimaryColor
import com.example.finjan.ui.theme.TextColor

/**
 * Privacy Policy screen displaying data collection and usage policies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Last updated
            Text(
                text = stringResource(R.string.privacy_last_updated),
                style = MaterialTheme.typography.bodySmall,
                color = TextColor.copy(alpha = 0.6f),
                fontFamily = PoppinsFontFamily
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Introduction
            PolicySection(
                title = stringResource(R.string.privacy_intro_title),
                content = stringResource(R.string.privacy_intro_content)
            )
            
            // Data Collection
            PolicySection(
                title = stringResource(R.string.privacy_data_collection_title),
                content = stringResource(R.string.privacy_data_collection_content)
            )
            
            // Data Usage
            PolicySection(
                title = stringResource(R.string.privacy_data_usage_title),
                content = stringResource(R.string.privacy_data_usage_content)
            )
            
            // Data Sharing
            PolicySection(
                title = stringResource(R.string.privacy_data_sharing_title),
                content = stringResource(R.string.privacy_data_sharing_content)
            )
            
            // Security
            PolicySection(
                title = stringResource(R.string.privacy_security_title),
                content = stringResource(R.string.privacy_security_content)
            )
            
            // Your Rights
            PolicySection(
                title = stringResource(R.string.privacy_rights_title),
                content = stringResource(R.string.privacy_rights_content)
            )
            
            // Contact
            PolicySection(
                title = stringResource(R.string.privacy_contact_title),
                content = stringResource(R.string.privacy_contact_content)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = PoppinsFontFamily,
                color = TextColor.copy(alpha = 0.8f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
            )
        }
    }
}

/**
 * Terms of Service screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.terms_of_service),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Last updated
            Text(
                text = stringResource(R.string.terms_last_updated),
                style = MaterialTheme.typography.bodySmall,
                color = TextColor.copy(alpha = 0.6f),
                fontFamily = PoppinsFontFamily
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Acceptance
            PolicySection(
                title = stringResource(R.string.terms_acceptance_title),
                content = stringResource(R.string.terms_acceptance_content)
            )
            
            // Account
            PolicySection(
                title = stringResource(R.string.terms_account_title),
                content = stringResource(R.string.terms_account_content)
            )
            
            // Orders
            PolicySection(
                title = stringResource(R.string.terms_orders_title),
                content = stringResource(R.string.terms_orders_content)
            )
            
            // Payments
            PolicySection(
                title = stringResource(R.string.terms_payments_title),
                content = stringResource(R.string.terms_payments_content)
            )
            
            // Liability
            PolicySection(
                title = stringResource(R.string.terms_liability_title),
                content = stringResource(R.string.terms_liability_content)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
