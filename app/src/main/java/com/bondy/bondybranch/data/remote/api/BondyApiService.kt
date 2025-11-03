package com.bondy.bondybranch.data.remote.api

import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BondyApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @GET("getCard/{cardNumber}")
    suspend fun getCard(@Path("cardNumber") cardNumber: String): LoyaltyCard

    @POST("processSale")
    suspend fun processSale(@Body request: SaleRequest): Transaction

    @POST("processRedeem")
    suspend fun processRedeem(@Body request: RedeemRequest): Transaction

    @GET("brand")
    suspend fun getBrand(): Brand

    @GET("branch/{branchId}")
    suspend fun getBranch(@Path("branchId") branchId: Int): Branch

    @GET("stats/today")
    suspend fun getDailyStats(): BranchDailyStats

    @GET("transactions")
    suspend fun getTransactions(): List<Transaction>
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class TokenResponse(
    val token: String,
    val userId: Int
)

@Serializable
data class SaleRequest(
    val cardNumber: String,
    val cups: Int
)

@Serializable
data class RedeemRequest(
    val cardNumber: String
)
