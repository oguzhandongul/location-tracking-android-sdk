package com.oguzhandongul.locationtrackingsdk.data.remote.repository

import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.ApiService
import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.domain.repository.NetworkRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class NetworkRepositoryImpl(
    private val config: SdkConfig,
    private val authRepository: AuthRepositoryImpl,
    private val apiService: ApiService
) : NetworkRepository {

    override fun getInitialTokens(onResult: (TokensResponse?) -> Unit) {
        val call = apiService.getNewTokens("Bearer ${config.apiKey}")
        call.enqueue(object : Callback<TokensResponse> {
            override fun onResponse(
                call: Call<TokensResponse>,
                response: Response<TokensResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                    authRepository.saveTokens(response.body())
                } else {
                    Timber.tag("NetworkManager")
                        .e("Error fetching initial tokens: %s", response.errorBody()?.string())
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<TokensResponse>, t: Throwable) {
                Timber.tag("NetworkManager").e(t, "Network error fetching initial tokens")
                onResult(null)
            }
        })
    }

    override fun refreshAccessToken(refreshToken: String, onResult: (TokensResponse?) -> Unit) {
        val call = apiService.refreshAccessToken("Bearer $refreshToken")
        call.enqueue(object : Callback<TokensResponse> {
            override fun onResponse(
                call: Call<TokensResponse>,
                response: Response<TokensResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Timber.tag("NetworkManager")
                        .e("Error refreshing access token: %s", response.errorBody()?.string())
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<TokensResponse>, t: Throwable) {
                Timber.tag("NetworkManager").e(t, "Network error refreshing access token")
                onResult(null)
            }
        })
    }

    override fun updateLocation(
        accessToken: String,
        locationUpdateRequest: LocationUpdateRequest,
        onResult: (Boolean) -> Unit
    ) {
        val call = apiService.updateLocation("Bearer $accessToken", locationUpdateRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Timber.tag("NetworkManager").e(t, "Network error updating location")
                onResult(false)
            }
        })
    }
}