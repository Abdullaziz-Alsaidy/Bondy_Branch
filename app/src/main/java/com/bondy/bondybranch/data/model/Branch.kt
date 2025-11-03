package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val id: Int,
    val location: String,
    val brandId: Int,
    val foodics: Boolean = false,
    val marn: Boolean = false,
    val manual: Boolean = false,
    val soldCups: Int = 0,
    val redeemedCups: Int = 0
)
