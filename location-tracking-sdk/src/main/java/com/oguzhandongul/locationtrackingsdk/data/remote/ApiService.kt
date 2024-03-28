package com.oguzhandongul.locationtrackingsdk.data.remote

import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Call

interface ApiService {

    @POST("/auth")
    fun getNewTokens(@Header("Authorization") apiKey: String): Call<TokensResponse>

    @POST("/auth/refresh")
    fun refreshAccessToken(@Header("Authorization") refreshToken: String): Call<TokensResponse>

    @POST("/location")
    fun updateLocation(@Header("Authorization") accessToken: String,
                       @Body locationUpdateRequest: LocationUpdateRequest
    ): Call<Void>
}