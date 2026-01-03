package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.IntegrationType
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType
import com.bondy.bondybranch.domain.model.ManualTransactionInput
import com.bondy.bondybranch.domain.model.ManualTransactionItem
import com.bondy.bondybranch.domain.usecase.CreateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class FloatingWindowViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FloatingWindowUiState())
    val uiState = _uiState.asStateFlow()

    private var createTransactionJob: Job? = null

    fun onSendClicked(cardNumber: String) {
        viewModelScope.launch {
            Log.d("FloatingWindow", "Send clicked with cardNumber=$cardNumber")
            val trimmedCardNumber = cardNumber.trim()
            val cardId = trimmedCardNumber.toLongOrNull()
            if (cardId == null) {
                Log.d("FloatingWindow", "Invalid card number: $trimmedCardNumber")
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    snackbarMessage = "Invalid card number.",
                    snackbarSuccess = false
                )
                return@launch
            }

            val input = ManualTransactionInput(
                cardId = cardId,
                transactionType = TransactionType.REDEEM,
                source = TransactionSource.POS,
                integrationType = IntegrationType.INTERNAL,
                externalRef = "FloatingWindow-${System.currentTimeMillis()}",
                cupsCount = 2,
                items = listOf(
                    ManualTransactionItem(
                        sku = "test-item",
                        quantity = 1,
                        price = 500
                    )
                ),
                amountCents = 500,
                redeemed = false,
                processed = true
            )
            Log.d("FloatingWindow", "Submitting transaction with cardId=$cardId")

            createTransactionJob?.cancel()
            createTransactionJob = viewModelScope.launch {
                createTransactionUseCase(input).collectLatest { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            Log.d("FloatingWindow", "Transaction loading")
                            _uiState.value = _uiState.value.copy(
                                isSubmitting = true,
                                snackbarMessage = null
                            )
                        }
                        is NetworkResult.Success -> {
                            Log.d("FloatingWindow", "Transaction success: ${result.data.id}")
                            _uiState.value = _uiState.value.copy(
                                isSubmitting = false,
                                snackbarMessage = "Success",
                                snackbarSuccess = true
                            )
                        }
                        is NetworkResult.Error -> {
                            Log.d("FloatingWindow", "Transaction error: ${result.message}")
                            _uiState.value = _uiState.value.copy(
                                isSubmitting = false,
                                snackbarMessage = result.message.ifBlank { "Error" },
                                snackbarSuccess = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun consumeSnackbar() {
        if (_uiState.value.snackbarMessage != null) {
            _uiState.value = _uiState.value.copy(snackbarMessage = null)
        }
    }
}

data class FloatingWindowUiState(
    val isSubmitting: Boolean = false,
    val snackbarMessage: String? = null,
    val snackbarSuccess: Boolean = false
)
