package com.oguzhandongul.locationtrackingsdk.data.remote

import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response

interface ApiService {

    @POST("/auth")
    suspend fun getNewTokens(
        @Header("Authorization") apiKey: String
    ): Response<TokensResponse>

    @POST("/auth/refresh")
    suspend fun refreshAccessToken(@Header("Authorization") refreshToken: String?): Response<TokensResponse>

    @POST("/location")
    suspend fun updateLocation(@Header("Authorization") accessToken: String?,
                       @Body locationUpdateRequest: LocationUpdateRequest
    ): Response<Void>
}