package com.bondy.bondybranch.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onCardDetected: (String) -> Unit
) {
    var manualCardNumber by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Scan or enter card") })
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
                text = "Scanning functionality coming soon.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = manualCardNumber,
                onValueChange = { manualCardNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Card number") }
            )
            Button(
                onClick = {
                    if (manualCardNumber.isNotBlank()) {
                        onCardDetected(manualCardNumber.trim())
                    }
                },
                enabled = manualCardNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}
