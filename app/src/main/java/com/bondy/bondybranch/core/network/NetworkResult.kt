package com.bondy.bondybranch.core.network

/**
 * Represents the outcome of a network or data operation.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
}
