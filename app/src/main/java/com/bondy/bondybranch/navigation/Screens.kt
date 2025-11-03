package com.bondy.bondybranch.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object Login

    @Serializable
    data object Dashboard

    @Serializable
    data object Scan

    @Serializable
    data class CardDetails(val cardNumber: String)

    @Serializable
    data object History

    @Serializable
    data object Settings
}
