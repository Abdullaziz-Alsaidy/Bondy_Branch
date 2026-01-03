package com.bondy.bondybranch.data.remote.source

import com.bondy.bondybranch.data.remote.api.BondyApiService
import com.bondy.bondybranch.data.remote.api.CreateTransactionRequest
import com.bondy.bondybranch.data.remote.api.LoginRequest
import com.bondy.bondybranch.data.remote.api.RedeemRequest
import com.bondy.bondybranch.data.remote.api.SaleRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BondyRemoteDataSource @Inject constructor(
    private val apiService: BondyApiService
) {
    suspend fun send(data: Map<String, String>) =
        apiService.send(data)
    suspend fun login(request: LoginRequest) =
        apiService.login(
            email = request.username,
            password = request.password
        )

    suspend fun getUserInfo(token: String) =
        apiService.getUserInfo(
            token = "Bearer $token"
        )

    suspend fun getCard(cardNumber: String) =
        apiService.getCard(cardNumber)

    suspend fun processSale(request: SaleRequest) =
        apiService.processSale(request)

    suspend fun processRedeem(request: RedeemRequest) =
        apiService.processRedeem(request)

    suspend fun createTransaction(request: CreateTransactionRequest, token: String) =
        //apiService.createTransaction(token = token,request = request)
        apiService.createTransaction(request = request)

    suspend fun getBrand() =
        apiService.getBrand()

    suspend fun getBranch(branchId: Int) =
        apiService.getBranch(branchId)

    suspend fun getDailyStats() =
        apiService.getDailyStats()

    suspend fun getTransactions(token: String) =
        apiService.getTransactions(token = token)
}
