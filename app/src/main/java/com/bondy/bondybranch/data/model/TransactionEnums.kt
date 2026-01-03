package com.bondy.bondybranch.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    @SerialName("earn")
    @SerializedName("earn")
    EARN,
    @SerialName("redeem")
    @SerializedName("redeem")
    REDEEM

}

@Serializable
enum class TransactionSource {
    @SerialName("pos")
    @SerializedName("pos")
    POS,
    @SerialName("app_manual")
    @SerializedName("app_manual")
    APP_MANUAL
}

@Serializable
enum class IntegrationType {
    @SerializedName("foodics")
    FOODICS,
    @SerializedName("marn")
    MARN,
    @SerialName("internal")
    @SerializedName("internal")
    INTERNAL
}

@Serializable
enum class TransactionStatus {
    PROCESSED,
    PENDING,
    FAILED
}
