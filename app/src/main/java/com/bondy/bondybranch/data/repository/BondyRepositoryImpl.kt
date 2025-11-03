package com.bondy.bondybranch.data.repository

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.AuthSession
import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.remote.api.LoginRequest
import com.bondy.bondybranch.data.remote.api.RedeemRequest
import com.bondy.bondybranch.data.remote.api.SaleRequest
import com.bondy.bondybranch.data.remote.source.BondyRemoteDataSource
import com.bondy.bondybranch.di.NetworkModule
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class BondyRepositoryImpl @Inject constructor(
    private val remoteDataSource: BondyRemoteDataSource,
    @NetworkModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BondyRepository {

    override fun login(username: String, password: String): Flow<NetworkResult<AuthSession>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.login(LoginRequest(username, password))
                emit(NetworkResult.Success(AuthSession(response.token, response.userId)))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchLoyaltyCard(cardNumber: String): Flow<NetworkResult<LoyaltyCard>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val card = remoteDataSource.getCard(cardNumber)
                emit(NetworkResult.Success(card))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun processSale(cardNumber: String, cups: Int): Flow<NetworkResult<Transaction>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val transaction = remoteDataSource.processSale(SaleRequest(cardNumber, cups))
                emit(NetworkResult.Success(transaction))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun processRedeem(cardNumber: String): Flow<NetworkResult<Transaction>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val transaction = remoteDataSource.processRedeem(RedeemRequest(cardNumber))
                emit(NetworkResult.Success(transaction))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchBrand(): Flow<NetworkResult<Brand>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val brand = remoteDataSource.getBrand()
                emit(NetworkResult.Success(brand))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchBranch(branchId: Int): Flow<NetworkResult<Branch>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val branch = remoteDataSource.getBranch(branchId)
                emit(NetworkResult.Success(branch))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun observeDailyStats(): Flow<NetworkResult<BranchDailyStats>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val stats = remoteDataSource.getDailyStats()
                emit(NetworkResult.Success(stats))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun observeTransactions(): Flow<NetworkResult<List<Transaction>>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val history = remoteDataSource.getTransactions()
                emit(NetworkResult.Success(history))
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(throwable.message.orEmpty(), throwable))
            }
        }.flowOn(ioDispatcher)
}
