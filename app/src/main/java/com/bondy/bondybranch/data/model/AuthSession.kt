package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val token: String,
    val userId: Int
)
