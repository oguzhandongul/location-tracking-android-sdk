package com.oguzhandongul.locationtrackingsdk.data.remote

import com.oguzhandongul.locationtrackingsdk.core.utils.RetrofitHelper
import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import java.io.IOException

internal class TokenHelper(private val authRepository: AuthRepository) {
    suspend fun refresh(): String {
        val refreshToken = "Bearer ${authRepository.getTokens()?.refreshToken}"
        val refreshResponse =
            RetrofitHelper.getApiService(authRepository).refreshAccessToken(refreshToken)

        if (refreshResponse.isSuccessful) {
            var newToken = ""
            refreshResponse.body()?.let {
                authRepository.saveTokens(
                    TokensResponse(
                        accessToken = it.accessToken,
                        refreshToken = it.refreshToken,
                        expiresAt = it.expiresAt
                    )
                )
                newToken = it.accessToken
            }
            return newToken
        } else {
            throw IOException("Refresh token failed")
        }
    }
}