package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val role: String
)
