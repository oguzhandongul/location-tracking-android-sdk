package com.oguzhandongul.locationtrackingsdk.domain.repository

import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse

interface NetworkRepository {

    fun getInitialTokens(onResult: (TokensResponse?) -> Unit)

    fun refreshAccessToken(refreshToken: String, onResult: (TokensResponse?) -> Unit)

    fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest,
        onResult: (Boolean) -> Unit
    )
}