package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.Brand
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FetchBrandUseCase @Inject constructor(
    private val repository: BondyRepository
) {
    operator fun invoke(): Flow<NetworkResult<Brand>> = repository.fetchBrand()
}
