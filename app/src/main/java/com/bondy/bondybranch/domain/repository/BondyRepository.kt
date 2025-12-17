package com.bondy.bondybranch.domain.repository

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.AuthSession
import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.UserInfo
import com.bondy.bondybranch.domain.model.ManualTransactionInput
import kotlinx.coroutines.flow.Flow

interface BondyRepository {
    fun login(username: String, password: String): Flow<NetworkResult<AuthSession>>

    fun getUserInfo(token: String): Flow<NetworkResult<UserInfo>>
    fun fetchLoyaltyCard(cardNumber: String): Flow<NetworkResult<LoyaltyCard>>
    fun processSale(cardNumber: String, cups: Int): Flow<NetworkResult<Transaction>>
    fun processRedeem(cardNumber: String): Flow<NetworkResult<Transaction>>
    fun createTransaction(input: ManualTransactionInput): Flow<NetworkResult<Transaction>>
    fun fetchBrand(): Flow<NetworkResult<Brand>>
    fun fetchBranch(branchId: Int): Flow<NetworkResult<Branch>>
    fun observeDailyStats(): Flow<NetworkResult<BranchDailyStats>>
    fun observeTransactions(): Flow<NetworkResult<List<Transaction>>>
}
