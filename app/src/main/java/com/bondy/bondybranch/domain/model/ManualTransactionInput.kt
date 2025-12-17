package com.bondy.bondybranch.domain.model

import com.bondy.bondybranch.data.model.IntegrationType
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType

data class ManualTransactionInput(
    val cardId: Long,
    val transactionType: TransactionType,
    val source: TransactionSource,
    val integrationType: IntegrationType,
    val externalRef: String,
    val cupsCount: Int,
    val items: List<ManualTransactionItem>,
    val amountCents: Int,
    val redeemed: Boolean,
    val processed: Boolean
)

data class ManualTransactionItem(
    val sku: String,
    val quantity: Int,
    val price: Int
)
