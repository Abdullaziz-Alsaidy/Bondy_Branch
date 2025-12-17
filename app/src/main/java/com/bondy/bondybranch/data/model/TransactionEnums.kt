package com.bondy.bondybranch.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    SALE,
    REDEEM,
    purchase
}

@Serializable
enum class TransactionSource {
    POS,
    APP_MANUAL
}

@Serializable
enum class IntegrationType {
    FOODICS,
    MARN,
    @SerialName("internal")
    INTERNAL
}

@Serializable
enum class TransactionStatus {
    PROCESSED,
    PENDING,
    FAILED
}
