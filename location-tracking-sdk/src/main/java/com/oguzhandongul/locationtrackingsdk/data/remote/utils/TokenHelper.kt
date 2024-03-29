package com.oguzhandongul.locationtrackingsdk.data.remote.utils

import com.oguzhandongul.locationtrackingsdk.core.utils.RetrofitHelper
import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import java.io.IOException

/**
 *  Helper class responsible for refreshing access tokens using the AuthRepository
 *  and a Retrofit-based API service.
 */
internal class TokenHelper(private val authRepository: AuthRepository) {


    /**
     *  Attempts to refresh an access token using the current refresh token:
     *  1. Retrieves the refresh token from the AuthRepository.
     *  2. Makes a network call to refresh the access token.
     *  3. If successful:
     *     * Saves the new tokens (access, refresh, expiration) using the AuthRepository.
     *     * Returns the new access token.
     *  4. If the refresh fails, throws an IOException.
     *
     *  @return The new access token.
     *  @throws IOException If the refresh process fails.
     */
    @Throws(Exception::class)
    suspend fun refresh(): String {
        val refreshToken = "Bearer ${authRepository.getTokens()?.refreshToken}"
        val refreshResponse =
            RetrofitHelper.getApiService(authRepository = authRepository)
                .refreshAccessToken(refreshToken = refreshToken)

        if (refreshResponse.isSuccessful) {
            var newAccessToken = ""
            refreshResponse.body()?.let {
                authRepository.saveTokens(
                    TokensResponse(
                        accessToken = it.accessToken,
                        refreshToken = it.refreshToken,
                        expiresAt = it.expiresAt
                    )
                )
                newAccessToken = it.accessToken
            }
            return newAccessToken
        } else {
            throw Exception("Refresh token failed")
        }
    }
}