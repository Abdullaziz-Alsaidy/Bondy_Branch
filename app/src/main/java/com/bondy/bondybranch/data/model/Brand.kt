@file:UseSerializers(DateSerializer::class)

package com.bondy.bondybranch.data.model

import com.bondy.bondybranch.core.serialization.DateSerializer
import java.util.Date
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject

@Serializable
data class Brand(
    val id: Int,
    val name: String,
    val logo: String,
    val branches: List<Int> = emptyList(),
    val subStatus: String? = null,
    val subTier: String? = null,
    val cardDesign: JsonObject? = null,
    val rule: JsonObject? = null,
    val soldCups: Int = 0,
    val redeemedCups: Int = 0,
    val users: List<Int> = emptyList(),
    val lastPayment: Date? = null,
    val welcomingMessage: String? = null
)
