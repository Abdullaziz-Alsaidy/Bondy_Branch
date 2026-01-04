package com.bondy.bondybranch.data.remote.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRefreshApiService {
    @POST("api/auth/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<TokenRefreshPayload>
}

@Serializable
data class RefreshTokenRequest(
    val refresh: String
)

@Serializable
data class TokenRefreshPayload(
    val access: String
)
