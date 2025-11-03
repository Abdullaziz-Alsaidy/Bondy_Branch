package com.bondy.bondybranch.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.domain.usecase.FetchBrandUseCase
import com.bondy.bondybranch.domain.usecase.FetchBranchUseCase
import com.bondy.bondybranch.domain.usecase.ObserveBranchStatsUseCase
import com.bondy.bondybranch.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchBrandUseCase: FetchBrandUseCase,
    private val fetchBranchUseCase: FetchBranchUseCase,
    private val observeBranchStatsUseCase: ObserveBranchStatsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    private var statsJob: Job? = null
    private var transactionsJob: Job? = null

    private val defaultBranchId = 10 // TODO: replace with persisted branch selection

    init {
        observeStats()
        observeTransactions()
        refreshReferenceData()
    }

    fun refreshReferenceData() {
        fetchBrand()
        fetchBranch(defaultBranchId)
    }

    private fun fetchBrand() {
        viewModelScope.launch {
            fetchBrandUseCase().collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isBrandLoading = true)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isBrandLoading = false,
                            brandName = result.data.name,
                            welcomingMessage = result.data.welcomingMessage
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isBrandLoading = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }

    private fun fetchBranch(branchId: Int) {
        viewModelScope.launch {
            fetchBranchUseCase(branchId).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isBranchLoading = true)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isBranchLoading = false,
                            branchLocation = result.data.location
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isBranchLoading = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }

    private fun observeStats() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            observeBranchStatsUseCase().collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isStatsLoading = true)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isStatsLoading = false,
                            dailyStats = result.data,
                            errorMessage = null
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isStatsLoading = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }

    private fun observeTransactions() {
        transactionsJob?.cancel()
        transactionsJob = viewModelScope.launch {
            observeTransactionsUseCase().collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isTransactionsLoading = true)

                    is NetworkResult.Success ->
                        uiState = uiState.copy(
                            isTransactionsLoading = false,
                            recentTransactions = result.data,
                            errorMessage = null
                        )

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isTransactionsLoading = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }
}

data class DashboardUiState(
    val brandName: String = "",
    val welcomingMessage: String? = null,
    val branchLocation: String = "",
    val dailyStats: BranchDailyStats? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val isBrandLoading: Boolean = false,
    val isBranchLoading: Boolean = false,
    val isStatsLoading: Boolean = false,
    val isTransactionsLoading: Boolean = false,
    val errorMessage: String? = null
)
