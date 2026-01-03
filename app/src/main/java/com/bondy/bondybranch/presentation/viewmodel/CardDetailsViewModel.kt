package com.bondy.bondybranch.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.IntegrationType
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType
import com.bondy.bondybranch.domain.model.ManualTransactionInput
import com.bondy.bondybranch.domain.model.ManualTransactionItem
import com.bondy.bondybranch.domain.usecase.CreateTransactionUseCase
import com.bondy.bondybranch.domain.usecase.FetchCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val fetchCardUseCase: FetchCardUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase
) : ViewModel() {
    var uiState by mutableStateOf(CardDetailsUiState())
        private set

    private var fetchCardJob: Job? = null
    private var createTransactionJob: Job? = null

    fun loadCard(cardNumber: String) {
        if (uiState.cardNumber == cardNumber && uiState.card != null) return
        uiState = uiState.copy(cardNumber = cardNumber)
        fetchCardJob?.cancel()
        fetchCardJob = viewModelScope.launch {
            fetchCardUseCase(cardNumber).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isCardLoading = true, errorMessage = null)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isCardLoading = false,
                            card = result.data,
                            errorMessage = null
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isCardLoading = false,
                            errorMessage = result.message.ifBlank { "Unable to load card." }
                        )
                }
            }
        }
    }

    fun onExternalRefChange(value: String) = updateForm { copy(externalRef = value) }

    fun onCupsCountChange(value: String) = updateForm { copy(cupsCount = value) }

    fun onAmountChange(value: String) = updateForm { copy(amountCents = value) }

    fun onTransactionTypeChange(type: TransactionType) =
        updateForm { copy(transactionType = type) }

    fun onSourceChange(source: TransactionSource) = updateForm { copy(source = source) }

    fun onIntegrationTypeChange(type: IntegrationType) =
        updateForm { copy(integrationType = type) }

    fun onRedeemedToggle(value: Boolean) = updateForm { copy(redeemed = value) }

    fun onProcessedToggle(value: Boolean) = updateForm { copy(processed = value) }

    fun addItem() = updateForm {
        copy(items = items + TransactionItemState())
    }

    fun removeItem(index: Int) = updateForm {
        if (items.size <= 1) this else copy(items = items.toMutableList().also { it.removeAt(index) })
    }

    fun updateItemSku(index: Int, value: String) = updateForm {
        copy(items = items.toMutableList().apply {
            this[index] = this[index].copy(sku = value)
        })
    }

    fun updateItemQuantity(index: Int, value: String) = updateForm {
        copy(items = items.toMutableList().apply {
            this[index] = this[index].copy(quantity = value)
        })
    }

    fun updateItemPrice(index: Int, value: String) = updateForm {
        copy(items = items.toMutableList().apply {
            this[index] = this[index].copy(price = value)
        })
    }

    fun dismissStatusMessage() {
        uiState = uiState.copy(statusMessage = null)
    }

    fun submitTransaction() {
        val cardId = uiState.cardNumber.toLongOrNull()
        if (cardId == null) {
            uiState = uiState.copy(formError = "Card number must be numeric to create a transaction.")
            return
        }

        val form = uiState.formState
        val cupsCount = form.cupsCount.toIntOrNull()
        if (cupsCount == null || cupsCount <= 0) {
            uiState = uiState.copy(formError = "Enter a valid cups count.")
            return
        }

        val amountCents = form.amountCents.toIntOrNull()
        if (amountCents == null || amountCents <= 0) {
            uiState = uiState.copy(formError = "Enter a valid amount in cents.")
            return
        }

        val items = form.items.mapIndexed { index, item ->
            val quantity = item.quantity.toIntOrNull()
            val price = item.price.toIntOrNull()
            when {
                item.sku.isBlank() -> {
                    uiState = uiState.copy(formError = "Item ${index + 1} is missing a SKU.")
                    return
                }
                quantity == null || quantity <= 0 -> {
                    uiState = uiState.copy(formError = "Item ${index + 1} needs a valid quantity.")
                    return
                }
                price == null || price < 0 -> {
                    uiState = uiState.copy(formError = "Item ${index + 1} needs a valid price.")
                    return
                }
                else -> ManualTransactionItem(
                    sku = item.sku,
                    quantity = quantity,
                    price = price
                )
            }
        }

        if (items.isEmpty()) {
            uiState = uiState.copy(formError = "Provide at least one line item.")
            return
        }

        createTransactionJob?.cancel()
        createTransactionJob = viewModelScope.launch {
            createTransactionUseCase(
                ManualTransactionInput(
                    cardId = cardId,
                    transactionType = form.transactionType,
                    source = form.source,
                    integrationType = form.integrationType,
                    externalRef = form.externalRef.ifBlank { "ManualRef-${System.currentTimeMillis()}" },
                    cupsCount = cupsCount,
                    items = items,
                    amountCents = amountCents,
                    redeemed = form.redeemed,
                    processed = form.processed
                )
            ).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isSubmitting = true, formError = null, statusMessage = null)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isSubmitting = false,
                            statusMessage = "Transaction created.",
                            createdTransaction = result.data,
                            formError = null
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isSubmitting = false,
                            formError = result.message.ifBlank { "Unable to create transaction." }
                        )
                }
            }
        }
    }

    private fun updateForm(updater: TransactionFormState.() -> TransactionFormState) {
        uiState = uiState.copy(
            formState = uiState.formState.updater(),
            formError = null
        )
    }
}

data class CardDetailsUiState(
    val cardNumber: String = "",
    val card: LoyaltyCard? = null,
    val isCardLoading: Boolean = false,
    val errorMessage: String? = null,
    val formState: TransactionFormState = TransactionFormState(),
    val formError: String? = null,
    val statusMessage: String? = null,
    val isSubmitting: Boolean = false,
    val createdTransaction: Transaction? = null
)

data class TransactionFormState(
    val transactionType: TransactionType = TransactionType.REDEEM,
    val source: TransactionSource = TransactionSource.POS,
    val integrationType: IntegrationType = IntegrationType.INTERNAL,
    val externalRef: String = "",
    val cupsCount: String = "3",
    val items: List<TransactionItemState> = listOf(
        TransactionItemState(sku = "coffee", quantity = "2", price = "450"),
        TransactionItemState(sku = "tea", quantity = "1", price = "300")
    ),
    val amountCents: String = "1200",
    val redeemed: Boolean = false,
    val processed: Boolean = false
)

data class TransactionItemState(
    val sku: String = "",
    val quantity: String = "",
    val price: String = ""
)
