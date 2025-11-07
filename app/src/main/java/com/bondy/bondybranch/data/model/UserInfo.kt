package com.bondy.bondybranch.data.model

import kotlinx.serialization.Serializable


@Serializable
data class UserInfo(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val phone: String,
    val type: String,
    val brand_branch_id: String
)
