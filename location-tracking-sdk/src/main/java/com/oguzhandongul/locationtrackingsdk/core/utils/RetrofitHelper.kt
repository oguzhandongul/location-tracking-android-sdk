package com.oguzhandongul.locationtrackingsdk.core.utils

import com.oguzhandongul.locationtrackingsdk.data.remote.ApiService
import com.oguzhandongul.locationtrackingsdk.data.remote.NetworkInterceptor
import com.oguzhandongul.locationtrackingsdk.data.remote.TokenHelper
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 *  Object for creating Retrofit instances and providing configured ApiService objects.
 */
internal object RetrofitHelper {
    /**
     *  Base URL of the backend API.
     */
    private const val BACKEND_URL = "https://dummy-api-mobile.api.sandbox.bird.one/"

    /**
     * Creates a Retrofit instance configured with the necessary interceptors and converters.
     *
     * @param authRepository The AuthRepository implementation for accessing auth tokens.
     * @return A configured Retrofit instance.
     */
    private fun getRetrofit(authRepository: AuthRepository): Retrofit {
        val tokenHelper = TokenHelper(authRepository)
        val client = OkHttpClient.Builder()
            .addInterceptor(NetworkInterceptor(tokenHelper)) // Adds the token interceptor
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    /**
     * Creates an ApiService interface using a configured Retrofit instance.
     *
     * @param authRepository The AuthRepository implementation for access auth tokens.
     * @return An ApiService instance ready to make API requests.
     */
    fun getApiService(authRepository: AuthRepository): ApiService =
        getRetrofit(authRepository).create(ApiService::class.java)

}