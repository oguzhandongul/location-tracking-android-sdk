package com.oguzhandongul.locationtrackingsdk.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class NetworkInterceptor(
    private val tokenHelper: TokenHelper
) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == CODE_UNAUTHORIZED) {
            response.close()
            val newAccessToken = runBlocking { tokenHelper.refresh() }
            val newAuthRequest: Request = request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
            return chain.proceed(newAuthRequest)
        }
        return response
    }

    companion object {
        const val CODE_UNAUTHORIZED = 403
    }
}