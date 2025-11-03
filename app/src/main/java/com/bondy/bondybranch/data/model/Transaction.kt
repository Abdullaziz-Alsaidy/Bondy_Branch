@file:UseSerializers(DateSerializer::class)

package com.bondy.bondybranch.data.model

import com.bondy.bondybranch.core.serialization.DateSerializer
import java.util.Date
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Transaction(
    val id: Int,
    val userId: Int,
    val brandId: Int,
    val branchId: Int? = null,
    val cardId: Int,
    val transactionType: TransactionType,
    val source: TransactionSource,
    val integrationType: IntegrationType? = null,
    val cupsCount: Int = 0,
    val cupsFilled: Int = 0,
    val redeemedCount: Int = 0,
    val rewardsEarned: Int = 0,
    val carryOver: Int = 0,
    val totalCupsGoal: Int = 0,
    val status: TransactionStatus = TransactionStatus.PROCESSED,
    val createdAt: Date? = null,
    val lastActivity: Date? = null,
    val message: String? = null
)
