package com.bondy.bondybranch.data.remote.source

import com.bondy.bondybranch.data.remote.api.BondyApiService
import com.bondy.bondybranch.data.remote.api.LoginRequest
import com.bondy.bondybranch.data.remote.api.RedeemRequest
import com.bondy.bondybranch.data.remote.api.SaleRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BondyRemoteDataSource @Inject constructor(
    private val apiService: BondyApiService
) {
    suspend fun login(request: LoginRequest) =
        apiService.login(
            email = request.username,
            password = request.password
        ).payload

    suspend fun getCard(cardNumber: String) =
        apiService.getCard(cardNumber).payload

    suspend fun processSale(request: SaleRequest) =
        apiService.processSale(request).payload

    suspend fun processRedeem(request: RedeemRequest) =
        apiService.processRedeem(request).payload

    suspend fun getBrand() =
        apiService.getBrand().payload

    suspend fun getBranch(branchId: Int) =
        apiService.getBranch(branchId).payload

    suspend fun getDailyStats() =
        apiService.getDailyStats().payload

    suspend fun getTransactions() =
        apiService.getTransactions().payload
}
