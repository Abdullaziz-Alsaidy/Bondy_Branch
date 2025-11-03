package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.Transaction
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ProcessRedeemUseCase @Inject constructor(
    private val repository: BondyRepository
) {
    operator fun invoke(cardNumber: String): Flow<NetworkResult<Transaction>> =
        repository.processRedeem(cardNumber)
}
