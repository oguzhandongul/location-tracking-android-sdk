package com.oguzhandongul.locationtrackingsdk.data.remote

import android.content.Context
import com.oguzhandongul.locationtrackingsdk.core.SecureTokenManager
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

object NetworkManager {
    private lateinit var config: SdkConfig
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    private var accessToken: String? = null
    private var refreshToken: String? = null

    fun initialize(context: Context, config: SdkConfig) {
        this.config = config
        retrofit = buildRetrofit()
        apiService = buildApiService()
        getInitialTokens {
            accessToken = it?.accessToken
            refreshToken = it?.refreshToken
            Timber.tag("RESULT").i("AccessToken:%s", accessToken)
            Timber.tag("RESULT").i("RefreshToken:%s", refreshToken)
        }
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.backendUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private fun buildApiService() = retrofit.create(ApiService::class.java)

    private fun getInitialTokens(onResult: (TokensResponse?) -> Unit) {
        val call = apiService.getNewTokens("Bearer ${config.apiKey}")
        call.enqueue(object : Callback<TokensResponse> {
            override fun onResponse(
                call: Call<TokensResponse>,
                response: Response<TokensResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                    SecureTokenManager.saveTokens(response.body())
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

    fun refreshAccessToken(refreshToken: String, onResult: (TokensResponse?) -> Unit) {
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

    fun updateLocation(
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