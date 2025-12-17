package com.bondy.bondybranch.data.repository

import android.util.Log
import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.AuthSession
import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.data.model.UserInfo
import com.bondy.bondybranch.data.remote.api.ApiResponse
import com.bondy.bondybranch.data.remote.api.CreateTransactionRequest
import com.bondy.bondybranch.data.remote.api.LoginRequest
import com.bondy.bondybranch.data.remote.api.RedeemRequest
import com.bondy.bondybranch.data.remote.api.SaleRequest
import com.bondy.bondybranch.data.remote.api.TransactionItemRequest
import com.bondy.bondybranch.data.remote.source.BondyRemoteDataSource
import com.bondy.bondybranch.di.NetworkModule
import com.bondy.bondybranch.domain.model.ManualTransactionInput
import com.bondy.bondybranch.domain.repository.BondyRepository
import com.bondy.bondybranch.utility.PreferenceStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.HttpException

@Singleton
class BondyRepositoryImpl @Inject constructor(
    private val remoteDataSource: BondyRemoteDataSource,
    private val prefs: PreferenceStorage,
    @NetworkModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BondyRepository {

    //val token = prefs.getAuthToken().orEmpty()
    val token = "Bearer ${prefs.getAuthToken()}".orEmpty()
    override fun login(username: String, password: String): Flow<NetworkResult<AuthSession>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.login(LoginRequest(username, password))
                emit(response.toNetworkResult { payload ->
                    // Backend currently omits user id; keep placeholder until provided.
                    AuthSession(payload.access_token, userId = 1)
                })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchLoyaltyCard(cardNumber: String): Flow<NetworkResult<LoyaltyCard>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getCard(cardNumber)
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun getUserInfo(token: String): Flow<NetworkResult<UserInfo>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getUserInfo(token)
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun processSale(cardNumber: String, cups: Int): Flow<NetworkResult<Transaction>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.processSale(SaleRequest(cardNumber, cups))
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun processRedeem(cardNumber: String): Flow<NetworkResult<Transaction>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.processRedeem(RedeemRequest(cardNumber))
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun createTransaction(input: ManualTransactionInput): Flow<NetworkResult<Transaction>> =
        flow {
            emit(NetworkResult.Loading)
            try {

                val request = CreateTransactionRequest(
                    transactionType = input.transactionType,
                    source = input.source,
                    integrationType = input.integrationType,
                    externalRef = input.externalRef,
                    cupsCount = input.cupsCount,
                    items = input.items.map {
                        TransactionItemRequest(
                            sku = it.sku,
                            quantity = it.quantity,
                            price = it.price
                        )
                    },
                    amountCents = input.amountCents,
                    redeemed = input.redeemed,
                    processed = input.processed,
                    cardId = input.cardId
                )
                val response = remoteDataSource.createTransaction(
                    request = request,
                    token = token
                )
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchBrand(): Flow<NetworkResult<Brand>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getBrand()
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun fetchBranch(branchId: Int): Flow<NetworkResult<Branch>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getBranch(branchId)
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun observeDailyStats(): Flow<NetworkResult<BranchDailyStats>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getDailyStats()
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)

    override fun observeTransactions(): Flow<NetworkResult<List<Transaction>>> =
        flow {
            emit(NetworkResult.Loading)
            try {
                val response = remoteDataSource.getTransactions(token = token)
                emit(response.toNetworkResult { it })
            } catch (throwable: Throwable) {
                emit(NetworkResult.Error(parseServerMessage(throwable), throwable))
            }
        }.flowOn(ioDispatcher)
}

private fun parseServerMessage(throwable: Throwable): String {
    if (throwable is HttpException) {
        val fallback = throwable.message.orEmpty()
        val errorBody = throwable.response()?.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            return try {
                val json = JSONObject(errorBody)
                json.optString("message").ifBlank { fallback.ifBlank { "Request failed." } }
            } catch (_: Exception) {
                fallback.ifBlank { "Request failed." }
            }
        }
        return fallback.ifBlank { "Request failed." }
    }
    return throwable.message.orEmpty().ifBlank { "Request failed." }
}

private fun <T, R> ApiResponse<T>.toNetworkResult(mapper: (T) -> R): NetworkResult<R> =
    if (status in 200..299) {
        NetworkResult.Success(mapper(payload))
    } else {
        NetworkResult.Error(message.ifBlank { "Request failed." })
    }
