package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    SALE,
    REDEEM
}

@Serializable
enum class TransactionSource {
    POS,
    APP_MANUAL
}

@Serializable
enum class IntegrationType {
    FOODICS,
    MARN
}

@Serializable
enum class TransactionStatus {
    PROCESSED,
    PENDING,
    FAILED
}
