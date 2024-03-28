package com.oguzhandongul.locationtrackingsdk.core.utils

import com.oguzhandongul.locationtrackingsdk.data.remote.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object RetrofitHelper {

    private const val BACKEND_URL = "https://dummy-api-mobile.api.sandbox.bird.one/"
    private fun getRetrofit(): Retrofit {
        val client = OkHttpClient.Builder().build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

     fun getApiService(): ApiService = getRetrofit().create(ApiService::class.java)

}