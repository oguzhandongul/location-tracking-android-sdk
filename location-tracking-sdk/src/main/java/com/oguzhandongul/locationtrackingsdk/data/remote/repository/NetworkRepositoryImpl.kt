package com.oguzhandongul.locationtrackingsdk.data.remote.repository

import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.ApiService
import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.domain.repository.NetworkRepository
import timber.log.Timber

class NetworkRepositoryImpl(
    private val config: SdkConfig,
    private val authRepository: AuthRepositoryImpl,
    private val apiService: ApiService
) : NetworkRepository {

    override suspend fun getInitialTokens() {
        val response = apiService.getNewTokens("Bearer ${config.apiKey}")
        if (response.isSuccessful) {
            authRepository.saveTokens(response.body())
            printTokens()
        } else {
            Timber.tag("LocationSDK")
                .e("Error fetching initial tokens: %s", response.errorBody()?.string())
        }
    }

    override suspend fun refreshAccessToken(
        refreshToken: String
    ) {
        val response = apiService.refreshAccessToken("Bearer $refreshToken")
        if (response.isSuccessful) {
            authRepository.saveTokens(response.body())
            printTokens()
        } else {
            Timber.tag("LocationSDK")
                .e("Error refreshing access token: %s", response.errorBody()?.string())
        }
    }

    override suspend fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest
    ) {
        val response = apiService.updateLocation("Bearer $accessToken", locationUpdateRequest)
        if (response.isSuccessful) {
            Timber.tag("LocationSDK").i("Successfully Updated location: ")
        } else {
            Timber.tag("LocationSDK").e("Network error updating location")
        }
    }

    private fun printTokens() {
        val accessToken = authRepository.getTokens()?.accessToken
        val refreshToken = authRepository.getTokens()?.refreshToken
        Timber.tag("RESULT").i("AccessToken:%s", accessToken)
        Timber.tag("RESULT").i("RefreshToken:%s", refreshToken)
    }
}