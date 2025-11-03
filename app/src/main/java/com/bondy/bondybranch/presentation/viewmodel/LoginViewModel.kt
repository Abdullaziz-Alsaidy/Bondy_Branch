package com.bondy.bondybranch.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.AuthSession
import com.bondy.bondybranch.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events

    private var loginJob: Job? = null

    fun onUsernameChanged(value: String) {
        uiState = uiState.copy(username = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    fun submitCredentials() {
        val username = uiState.username.trim()
        val password = uiState.password

        if (username.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please provide both email and password.")
            return
        }

        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            loginUseCase(username, password).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading ->
                        uiState = uiState.copy(isLoading = true, errorMessage = null)

                    is NetworkResult.Success -> handleLoginSuccess(result.data)

                    is NetworkResult.Error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = result.message.ifBlank { "Login failed. Please try again." }
                        )
                }
            }
        }
    }

    private suspend fun handleLoginSuccess(session: AuthSession) {
        uiState = uiState.copy(isLoading = false, errorMessage = null)
        _events.emit(LoginEvent.Success(session))
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginEvent {
    data class Success(val session: AuthSession) : LoginEvent
}
