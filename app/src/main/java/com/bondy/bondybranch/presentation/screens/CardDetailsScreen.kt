package com.bondy.bondybranch.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bondy.bondybranch.presentation.viewmodel.CardDetailsViewModel
import com.bondy.bondybranch.presentation.viewmodel.LoginEvent
import com.bondy.bondybranch.presentation.viewmodel.LoginViewModel
import com.google.android.play.core.integrity.v
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    cardNumber: String,
    onBack: () -> Unit,
    viewModel: CardDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        //viewModel.logCardDetails(card = cardNumber)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card details") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Card $cardNumber",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Detailed card view coming soon",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = {
                    viewModel.logCardDetails(card = cardNumber)
                    onBack },
                modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}
