package com.bondy.bondybranch.data.remote.api

import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.UserInfo
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BondyApiService {
    @POST("auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): ApiResponse<LoginPayload>


    @GET("auth/profile")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): ApiResponse<UserInfo>

    @GET("getCard/{cardNumber}")
    suspend fun getCard(@Path("cardNumber") cardNumber: String): ApiResponse<LoyaltyCard>

    @POST("processSale")
    suspend fun processSale(@Body request: SaleRequest): ApiResponse<Transaction>

    @POST("processRedeem")
    suspend fun processRedeem(@Body request: RedeemRequest): ApiResponse<Transaction>

    @GET("brand")
    suspend fun getBrand(): ApiResponse<Brand>

    @GET("branch/{branchId}")
    suspend fun getBranch(@Path("branchId") branchId: Int): ApiResponse<Branch>

    @GET("stats/today")
    suspend fun getDailyStats(): ApiResponse<BranchDailyStats>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): ApiResponse<List<Transaction>>
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val payload: T
)

@Serializable
data class LoginPayload(
    val access_token: String
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
