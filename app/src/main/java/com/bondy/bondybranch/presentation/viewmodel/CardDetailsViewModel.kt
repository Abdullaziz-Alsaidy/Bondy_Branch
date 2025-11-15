package com.bondy.bondybranch.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType
import com.bondy.bondybranch.domain.usecase.FetchBranchUseCase
import com.bondy.bondybranch.domain.usecase.FetchBrandUseCase
import com.bondy.bondybranch.domain.usecase.ObserveBranchStatsUseCase
import com.bondy.bondybranch.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val fetchBrandUseCase: FetchBrandUseCase,
    private val fetchBranchUseCase: FetchBranchUseCase,
    private val observeBranchStatsUseCase: ObserveBranchStatsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {
    private var statsJob: Job? = null
    private var transactionsJob: Job? = null
    init {
        Log.d("CardDetailsViewModel", "CardDetailsViewModel initialized")

    }

    fun logCardDetails(card: String) {
        Log.d("CardDetailsViewModel", "Card $card")
    }
}


