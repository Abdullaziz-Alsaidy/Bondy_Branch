package com.bondy.bondybranch.data.remote.api

import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.IntegrationType
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.TransactionSource
import com.bondy.bondybranch.data.model.TransactionType
import com.bondy.bondybranch.data.model.UserInfo
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BondyApiService {
    @POST("api/auth/login/")
    suspend fun login(
        @Body request: LoginRequest): ApiResponse<LoginPayload>

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

    @POST("/api/transactions/")
    suspend fun createTransaction(
       // @Header("Authorization") token: String,
        @Body request: CreateTransactionRequest): ApiResponse<Transaction>

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

    @POST("post")
    suspend fun send(@Body body: Map<String, String>): Response<EchoResponse>
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class ApiResponse<T>(
    val status: String,
    val message: String,
    val payload: T
)


@Serializable
data class LoginPayload(
    val status: String,
    val message: String,
    val payload: String
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

@Serializable
data class CreateTransactionRequest(
    @SerialName("transaction_type")
    @SerializedName("transaction_type")
    val transactionType: TransactionType,
    val source: String,
    @SerialName("integration_type")
    @SerializedName("integration_type")
    val integrationType: IntegrationType,
    @SerialName("external_ref")
    @SerializedName("external_ref")
    val externalRef: String,
    @SerialName("cups_count")
    @SerializedName("cups_count")
    val cupsCount: Int,
    val items: List<TransactionItemRequest>,
    @SerialName("amount_cents")
    @SerializedName("amount_cents")
    val amountCents: Int,
    val redeemed: Boolean,
    val processed: Boolean,
    @SerialName("card_id")
    @SerializedName("card_id")
    val cardId: Long,
    @SerialName("brand_branch_id")
    @SerializedName("brand_branch_id")
    val brandBranchId: Int = 1,
    @SerialName("user_id")
    @SerializedName("user_id")
    val userId: Int = 1
)
/*
  {
    "transaction_type": "earn",
    "source": "api",
    "integration_type": "api",
    "external_ref": "TXN-0009",
    "cups_count": 3,
    "amount_cents": 1200,
    "card_id": 9,
    "brand_branch_id": 1,
    "user_id": 1,
    "items": {"note": "demo"},
    "redeemed": false,
    "processed": true
  }

 */

@Serializable
data class TransactionItemRequest(
    val sku: String,
    val quantity: Int,
    val price: Int
)
data class SendRequest(
    val cardNumber: String,
    val action: String
)

data class EchoResponse(
    val json: Map<String, Any>? = null,
    val data: String? = null
)
