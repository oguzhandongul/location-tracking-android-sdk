package com.oguzhandongul.locationtrackingsdk.data.remote.repository

import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.exceptions.AuthenticationFailureException
import com.oguzhandongul.locationtrackingsdk.data.exceptions.ServerErrorException
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.api.ApiService
import com.oguzhandongul.locationtrackingsdk.data.remote.utils.HttpErrorCodes
import com.oguzhandongul.locationtrackingsdk.data.remote.models.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.utils.Headers
import com.oguzhandongul.locationtrackingsdk.domain.repository.NetworkRepository
import timber.log.Timber

/**
 * Implementation of the NetworkRepository interface. Responsible for handling
 * network communications with the backend API, including token management
 * and location updates.
 *
 * @param config The SDK configuration object containing the API key.
 * @param authRepository The AuthRepository implementation for local token storage and retrieval.
 * @param apiService The Retrofit-based ApiService interface for making API requests.
 */
class NetworkRepositoryImpl(
    private val config: SdkConfig,
    private val authRepository: AuthRepositoryImpl,
    private val apiService: ApiService
) : NetworkRepository {

    /**
     * Fetches initial access and refresh tokens from the backend.
     * - Makes a network call using the ApiService.
     * - If successful, saves the tokens using the AuthRepository.
     * - Logs the retrieved tokens for debugging.
     * - If unsuccessful, logs an error message.
     */
    override suspend fun getInitialTokens() {
        val response = apiService.getNewTokens("${Headers.HEADER_BEARER} ${config.apiKey}")
        if (response.isSuccessful) {
            authRepository.saveTokens(response.body())
            printTokens()
        } else {
            when (response.code()) {
                HttpErrorCodes.FORBIDDEN, HttpErrorCodes.UNAUTHORIZED -> throw AuthenticationFailureException()
                HttpErrorCodes.SERVER_ERROR -> throw ServerErrorException()
                else -> throw Exception("Unexpected error: ${response.errorBody()?.string()}")
            }
        }
    }

    /**
     * Refreshes an access token using the provided refresh token.
     * - Makes a network call using the ApiService.
     * - If successful, saves the new tokens using the AuthRepository.
     * - Logs the retrieved tokens for debugging.
     * - If unsuccessful, logs an error message.
     *
     * @param refreshToken The current refresh token.
     */
    override suspend fun refreshAccessToken(
        refreshToken: String
    ) {
        val response =
            apiService.refreshAccessToken(refreshToken = "${Headers.HEADER_BEARER} $refreshToken")
        if (response.isSuccessful) {
            authRepository.saveTokens(tokens = response.body())
            printTokens()
        } else {
            when (response.code()) {
                HttpErrorCodes.FORBIDDEN, HttpErrorCodes.UNAUTHORIZED -> throw AuthenticationFailureException()
                HttpErrorCodes.SERVER_ERROR -> throw ServerErrorException()
                else -> throw Exception("Unexpected error: ${response.errorBody()?.string()}")
            }
        }
    }

    /**
     * Sends a location update to the backend.
     * - Makes a network call using the ApiService.
     * - Logs a success or error message based on the response.
     *
     * @param accessToken The current access token.
     * @param locationUpdateRequest The location data to be sent.
     */
    override suspend fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest
    ) {
        val response = apiService.updateLocation(
            accessToken = "${Headers.HEADER_BEARER} $accessToken",
            locationUpdateRequest = locationUpdateRequest
        )
        if (response.isSuccessful) {
            Timber.tag("LocationSDK").i("Successfully Updated location: ")
        } else {
            when (response.code()) {
                HttpErrorCodes.FORBIDDEN, HttpErrorCodes.UNAUTHORIZED -> throw AuthenticationFailureException()
                HttpErrorCodes.SERVER_ERROR -> throw ServerErrorException()
                else -> throw Exception("Unexpected error: ${response.errorBody()?.string()}")
            }
        }
    }

    /**
     * Utility function to print the currently stored access and refresh tokens
     * for debugging purposes.
     */
    private fun printTokens() {
        val accessToken = authRepository.getTokens()?.accessToken
        val refreshToken = authRepository.getTokens()?.refreshToken
        Timber.tag("RESULT").i("AccessToken:%s", accessToken)
        Timber.tag("RESULT").i("RefreshToken:%s", refreshToken)
    }
}