@file:UseSerializers(DateSerializer::class)

package com.bondy.bondybranch.data.model

import com.bondy.bondybranch.core.serialization.DateSerializer
import java.util.Date
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class BranchDailyStats(
    val totalSales: Int = 0,
    val totalRedemptions: Int = 0,
    val lastUpdated: Date? = null
)
