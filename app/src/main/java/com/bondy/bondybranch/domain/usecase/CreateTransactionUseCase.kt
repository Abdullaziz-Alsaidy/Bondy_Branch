package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.domain.model.ManualTransactionInput
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateTransactionUseCase @Inject constructor(
    private val repository: BondyRepository
) {
    operator fun invoke(input: ManualTransactionInput): Flow<NetworkResult<Transaction>> =
        repository.createTransaction(input)
}
