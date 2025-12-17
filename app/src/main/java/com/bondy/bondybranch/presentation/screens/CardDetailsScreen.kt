package com.bondy.bondybranch.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bondy.bondybranch.data.model.IntegrationType
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType
import com.bondy.bondybranch.presentation.viewmodel.CardDetailsUiState
import com.bondy.bondybranch.presentation.viewmodel.CardDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    cardNumber: String,
    onBack: () -> Unit,
    viewModel: CardDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    LaunchedEffect(cardNumber) {
        viewModel.loadCard(cardNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Card $cardNumber",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (uiState.errorMessage != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(uiState.errorMessage) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }

            if (uiState.formError != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(uiState.formError) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }

            if (uiState.statusMessage != null) {
                AssistChip(
                    onClick = { viewModel.dismissStatusMessage() },
                    label = { Text(uiState.statusMessage) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Card snapshot",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    when {
                        uiState.isCardLoading -> {
                            BoxWithCenteredProgress(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        uiState.card != null -> {
                            CardStats(
                                filled = uiState.card.cupsFilled,
                                goal = uiState.card.cupsGoal,
                                redeemed = uiState.card.redeemedCount
                            )
                        }
                        else -> {
                            Text(
                                text = "No card info yet.",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            TransactionFormSection(
                uiState = uiState,
                onExternalRefChange = viewModel::onExternalRefChange,
                onCupsChange = viewModel::onCupsCountChange,
                onAmountChange = viewModel::onAmountChange,
                onTransactionTypeChange = viewModel::onTransactionTypeChange,
                onSourceChange = viewModel::onSourceChange,
                onIntegrationChange = viewModel::onIntegrationTypeChange,
                onRedeemedToggle = viewModel::onRedeemedToggle,
                onProcessedToggle = viewModel::onProcessedToggle,
                onAddItem = viewModel::addItem,
                onRemoveItem = viewModel::removeItem,
                onItemSkuChange = viewModel::updateItemSku,
                onItemQuantityChange = viewModel::updateItemQuantity,
                onItemPriceChange = viewModel::updateItemPrice,
                onSubmit = viewModel::submitTransaction
            )
        }
    }
}

@Composable
private fun BoxWithCenteredProgress(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = color)
    }
}

@Composable
private fun CardStats(filled: Int, goal: Int, redeemed: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Cups",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "$filled / $goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Redeemed",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "$redeemed",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun TransactionFormSection(
    uiState: CardDetailsUiState,
    onExternalRefChange: (String) -> Unit,
    onCupsChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTransactionTypeChange: (TransactionType) -> Unit,
    onSourceChange: (TransactionSource) -> Unit,
    onIntegrationChange: (IntegrationType) -> Unit,
    onRedeemedToggle: (Boolean) -> Unit,
    onProcessedToggle: (Boolean) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onItemSkuChange: (Int, String) -> Unit,
    onItemQuantityChange: (Int, String) -> Unit,
    onItemPriceChange: (Int, String) -> Unit,
    onSubmit: () -> Unit
) {
    val form = uiState.formState
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create transaction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(text = "Transaction type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionType.values().forEach { type ->
                    FilterChip(
                        selected = form.transactionType == type,
                        onClick = { onTransactionTypeChange(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.titlecase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Text(text = "Source", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionSource.values().forEach { source ->
                    FilterChip(
                        selected = form.source == source,
                        onClick = { onSourceChange(source) },
                        label = { Text(source.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }) }
                    )
                }
            }

            Text(text = "Integration", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IntegrationType.values().forEach { integration ->
                    FilterChip(
                        selected = form.integrationType == integration,
                        onClick = { onIntegrationChange(integration) },
                        label = { Text(integration.name.lowercase().replaceFirstChar { it.titlecase() }) }
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.externalRef,
                onValueChange = onExternalRefChange,
                label = { Text("External reference") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = form.cupsCount,
                    onValueChange = onCupsChange,
                    label = { Text("Cups count") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = form.amountCents,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (cents)") },
                    singleLine = true
                )
            }

            ToggleRow(
                title = "Redeemed",
                checked = form.redeemed,
                onCheckedChange = onRedeemedToggle
            )
            ToggleRow(
                title = "Processed",
                checked = form.processed,
                onCheckedChange = onProcessedToggle
            )

            Text(
                text = "Line items",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            form.items.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item ${index + 1}",
                                fontWeight = FontWeight.SemiBold
                            )
                            if (form.items.size > 1) {
                                IconButton(onClick = { onRemoveItem(index) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Remove item"
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = item.sku,
                            onValueChange = { onItemSkuChange(index, it) },
                            label = { Text("SKU") }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = item.quantity,
                                onValueChange = { onItemQuantityChange(index, it) },
                                label = { Text("Quantity") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = item.price,
                                onValueChange = { onItemPriceChange(index, it) },
                                label = { Text("Price (cents)") },
                                singleLine = true
                            )
                        }
                    }
                }
            }

            TextButton(onClick = onAddItem) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add item")
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text("Create transaction")
            }

            uiState.createdTransaction?.let { transaction ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Last transaction",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text("ID: ${transaction.id}")
                        Text("Type: ${transaction.transactionType}")
                        transaction.message?.let {
                            if (it.isNotBlank()) {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
