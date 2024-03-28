package com.oguzhandongul.locationtrackingsdk.data.remote

import com.oguzhandongul.locationtrackingsdk.data.remote.HttpErrorCodes.FORBIDDEN
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class NetworkInterceptor(
    private val tokenHelper: TokenHelper
) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == FORBIDDEN) {
            response.close()
            val newAccessToken = runBlocking { tokenHelper.refresh() }
            val newAuthRequest: Request = request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
            return chain.proceed(newAuthRequest)
        }
        return response
    }
}