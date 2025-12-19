package com.bondy.bondybranch.presentation.screens

import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.overlay.DialogPermission
import com.bondy.bondybranch.overlay.FloatingWindowContent
import com.bondy.bondybranch.overlay.FloatingWindowViewModel
import com.bondy.bondybranch.overlay.rememberFloatingWindow
import com.bondy.bondybranch.presentation.viewmodel.DashboardUiState
import com.bondy.bondybranch.presentation.viewmodel.DashboardViewModel
import com.github.only52607.compose.window.dragFloatingWindow
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onCardSelected: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    floatingWindowViewModel: FloatingWindowViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState
    val context = LocalContext.current
    val appContext = context.applicationContext

    // If you already have a StateFlow in your VM, use it. If not, keep it local like this:
    val showingState = remember { MutableStateFlow(false) }
    val showing by showingState.collectAsStateWithLifecycle()

    val showDialogPermission = remember { mutableStateOf(false) }

    val floatingWindow = rememberFloatingWindow(appContext) {
        FloatingWindowContent(
            model = floatingWindowViewModel,
            stats = uiState.dailyStats,
            isLoading = uiState.isStatsLoading
        )
    }

    fun showOverlay() {
        if (showingState.value) return
        if (Settings.canDrawOverlays(context)) {
            floatingWindow.show()
            showingState.value = true
        } else {
            showDialogPermission.value = true
        }
    }

    fun hideOverlay() {
        if (!showingState.value) return
        floatingWindow.hide()
        showingState.value = false
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.brandName.ifBlank { "Bondy Dashboard" }
                    )
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                    }
                    IconButton(onClick = { if (showing) hideOverlay() else showOverlay() }) {
                        Icon(
                            imageVector = Icons.Filled.PictureInPictureAlt,
                            contentDescription = if (showing) "Hide overlay" else "Show overlay"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScanClick,
                icon = { Icon(imageVector = Icons.Filled.QrCodeScanner, contentDescription = null) },
                text = { Text("Scan Card") }
            )
        }
    ) { innerPadding ->
        DashboardContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            uiState = uiState,
            onManualLookup = onCardSelected
        )
        DialogPermission(showDialogState = showDialogPermission)
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier,
    uiState: DashboardUiState,
    onManualLookup: (String) -> Unit
) {
    var manualCardNumber by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (uiState.welcomingMessage != null) {
                Text(
                    text = uiState.welcomingMessage,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = uiState.branchLocation.ifBlank { "" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            StatsCard(
                stats = uiState.dailyStats,
                isLoading = uiState.isStatsLoading
            )
        }

        item {
            ManualLookupCard(
                cardNumber = manualCardNumber,
                onCardNumberChange = { manualCardNumber = it },
                onLookup = {
                    if (manualCardNumber.isNotBlank()) {
                        onManualLookup(manualCardNumber.trim())
                    }
                },
                isLoading = uiState.isBranchLoading || uiState.isBrandLoading
            )
        }

        if (uiState.errorMessage != null) {
            item {
                AssistChip(
                    onClick = {},
                    label = { Text(uiState.errorMessage) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        }

        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (uiState.isTransactionsLoading && uiState.recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.recentTransactions.isEmpty()) {
            item {
                Text(
                    text = "No transactions yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.recentTransactions.take(10)) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
 fun StatsCard(stats: BranchDailyStats?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's performance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else if (stats != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatBlock(label = "Sales", value = stats.totalSales.toString())
                    StatBlock(label = "Redemptions", value = stats.totalRedemptions.toString())
                }
            } else {
                Text(
                    text = "No stats available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ManualLookupCard(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    onLookup: () -> Unit,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Look up a card",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Card number") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onLookup,
                enabled = cardNumber.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View card")
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transaction.transactionType.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cups: ${transaction.cupsCount} | Rewards: ${transaction.rewardsEarned}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            transaction.message?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@Composable
fun StatsCard1(
    stats: BranchDailyStats?,
    isLoading: Boolean,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.dragFloatingWindow(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's performance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else if (stats != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatBlock(label = "Sales", value = stats.totalSales.toString())
                    StatBlock(label = "Redemptions", value = stats.totalRedemptions.toString())
                }
            } else {
                Text(
                    text = "No stats available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Card number") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSend,
                enabled = cardNumber.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
            }
        }
    }
}
