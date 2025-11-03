package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.LoyaltyCard
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FetchCardUseCase @Inject constructor(
    private val repository: BondyRepository
) {
    operator fun invoke(cardNumber: String): Flow<NetworkResult<LoyaltyCard>> =
        repository.fetchLoyaltyCard(cardNumber)
}
