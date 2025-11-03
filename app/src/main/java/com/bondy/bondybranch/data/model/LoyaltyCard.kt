package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyCard(
    val id: String,
    val cupsFilled: Int,
    val cupsGoal: Int,
    val redeemedCount: Int,
    val lastActivity: String
)
