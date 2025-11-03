package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.Branch
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FetchBranchUseCase @Inject constructor(
    private val repository: BondyRepository
) {
    operator fun invoke(branchId: Int): Flow<NetworkResult<Branch>> =
        repository.fetchBranch(branchId)
}
