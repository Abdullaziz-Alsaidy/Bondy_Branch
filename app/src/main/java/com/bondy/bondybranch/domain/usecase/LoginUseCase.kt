package com.bondy.bondybranch.domain.usecase

import com.bondy.bondybranch.core.network.NetworkResult
import com.bondy.bondybranch.data.model.AuthSession
import com.bondy.bondybranch.data.model.UserInfo
import com.bondy.bondybranch.domain.repository.BondyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LoginUseCase @Inject constructor(
    private val repository: BondyRepository
) {
     fun logIn(username: String, password: String): Flow<NetworkResult<AuthSession>> =
        repository.login(username, password)

    fun getUserInfo(token: String): Flow<NetworkResult<UserInfo>> =
        repository.getUserInfo(token)
}
