package com.oguzhandongul.locationtrackingsdk.domain.repository

import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest

interface NetworkRepository {

    suspend fun getInitialTokens()

    suspend fun refreshAccessToken(refreshToken: String)

    suspend fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest
    )
}