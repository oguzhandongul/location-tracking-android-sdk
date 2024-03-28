package com.oguzhandongul.locationtrackingsdk.core.utils

import com.oguzhandongul.locationtrackingsdk.data.remote.ApiService
import com.oguzhandongul.locationtrackingsdk.data.remote.NetworkInterceptor
import com.oguzhandongul.locationtrackingsdk.data.remote.TokenHelper
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BACKEND_URL = "https://dummy-api-mobile.api.sandbox.bird.one/"

internal object RetrofitHelper {
    private fun getRetrofit(authRepository: AuthRepository): Retrofit {
        val tokenHelper = TokenHelper(authRepository)
        val networkInterceptor = NetworkInterceptor(tokenHelper)
        val client = OkHttpClient.Builder().addInterceptor(networkInterceptor).build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    fun getApiService(authRepository: AuthRepository): ApiService =
        getRetrofit(authRepository).create(ApiService::class.java)

}