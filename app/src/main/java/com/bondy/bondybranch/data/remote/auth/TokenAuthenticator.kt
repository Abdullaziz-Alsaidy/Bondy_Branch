package com.bondy.bondybranch.data.remote.auth

import com.bondy.bondybranch.data.remote.api.AuthRefreshApiService
import com.bondy.bondybranch.data.remote.api.RefreshTokenRequest
import com.bondy.bondybranch.utility.PreferenceStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

@Singleton
class TokenAuthenticator @Inject constructor(
    private val prefs: PreferenceStorage,
    private val refreshApi: AuthRefreshApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = prefs.getRefreshToken().orEmpty()
        if (refreshToken.isBlank()) {
            return null
        }

        val newAccessToken = runBlocking {
            runCatching {
                val refreshResponse = refreshApi.refreshToken(RefreshTokenRequest(refreshToken))
                val statusCode = refreshResponse.status.toIntOrNull()
                val isSuccess = refreshResponse.status.equals("success", ignoreCase = true) ||
                    (statusCode in 200..299)
                if (isSuccess) {
                    refreshResponse.payload.access
                } else {
                    null
                }
            }.getOrNull()
        } ?: return null

        prefs.saveAuthToken(newAccessToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}
