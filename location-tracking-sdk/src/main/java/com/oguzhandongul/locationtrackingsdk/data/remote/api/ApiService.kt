package com.oguzhandongul.locationtrackingsdk.data.remote.api

import com.oguzhandongul.locationtrackingsdk.data.remote.models.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.data.remote.utils.Endpoints
import com.oguzhandongul.locationtrackingsdk.data.remote.utils.Headers.HEADER_AUTH
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response

interface ApiService {

    @POST(Endpoints.AUTH)
    suspend fun getNewTokens(
        @Header(HEADER_AUTH) apiKey: String
    ): Response<TokensResponse>

    @POST(Endpoints.REFRESH)
    suspend fun refreshAccessToken(@Header(HEADER_AUTH) refreshToken: String?): Response<TokensResponse>

    @POST(Endpoints.LOCATION)
    suspend fun updateLocation(@Header(HEADER_AUTH) accessToken: String?,
                       @Body locationUpdateRequest: LocationUpdateRequest
    ): Response<Void>
}