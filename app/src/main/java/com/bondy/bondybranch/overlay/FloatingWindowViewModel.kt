package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.remote.api.EchoResponse
import com.bondy.bondybranch.domain.usecase.FetchBranchUseCase
import com.bondy.bondybranch.domain.usecase.FetchBrandUseCase
import com.bondy.bondybranch.domain.usecase.ObserveBranchStatsUseCase
import com.bondy.bondybranch.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FloatingWindowViewModel @Inject constructor(
    private val fetchBrandUseCase: FetchBrandUseCase,
    private val fetchBranchUseCase: FetchBranchUseCase,
    private val observeBranchStatsUseCase: ObserveBranchStatsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel(){
    private val _state = MutableStateFlow<NetworkResult<EchoResponse>>(NetworkResult.Loading)
    val state = _state.asStateFlow()

    fun onSendClicked(cardNumber: String) {
        viewModelScope.launch {
            Log.d("FloatingWindow", "Clicked (VM)")
            fetchBrandUseCase.invoke1(
                mapOf(
                    "cardNumber" to cardNumber,
                    "action" to "redeem"
                )
            ).collect { result ->
                _state.value = result
            }
        }
    }
}
