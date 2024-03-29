package io.github.oguzhandongul.locationtrackingsdk.domain.repository

import io.github.oguzhandongul.locationtrackingsdk.data.remote.models.requests.LocationUpdateRequest

interface NetworkRepository {

    suspend fun getInitialTokens()

    suspend fun refreshAccessToken(refreshToken: String)

    suspend fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest
    )
}