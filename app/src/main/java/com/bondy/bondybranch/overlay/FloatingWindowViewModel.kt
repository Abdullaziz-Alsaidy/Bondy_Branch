package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.remote.api.EchoResponse
import com.bondy.bondybranch.domain.usecase.FetchBranchUseCase
import com.bondy.bondybranch.domain.usecase.FetchBrandUseCase
import com.bondy.bondybranch.domain.usecase.ObserveBranchStatsUseCase
import com.bondy.bondybranch.domain.usecase.ObserveTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FloatingWindowViewModel @Inject constructor(
    private val fetchBrandUseCase: FetchBrandUseCase,
    private val fetchBranchUseCase: FetchBranchUseCase,
    private val observeBranchStatsUseCase: ObserveBranchStatsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel(){
    private var _dialogVisible by mutableStateOf(false)
    val dialogVisible: Boolean get() = _dialogVisible
    private val _state = MutableStateFlow<NetworkResult<EchoResponse>>(NetworkResult.Loading)
    val state = _state.asStateFlow()
    fun showDialog() {
        _dialogVisible = true
    }

    fun dismissDialog() {
        _dialogVisible = false
    }


    fun onSendClicked() {
        viewModelScope.launch {
            Log.d("FloatingWindow", "Clicked (VM)")
            fetchBrandUseCase.invoke1(
                mapOf(
                    "cardNumber" to "123456",
                    "action" to "redeem"
                )
            ).collect { result ->
                _state.value = result
            }
        }
    }
}