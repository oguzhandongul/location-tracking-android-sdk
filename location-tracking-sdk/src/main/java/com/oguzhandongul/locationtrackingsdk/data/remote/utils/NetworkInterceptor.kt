package com.oguzhandongul.locationtrackingsdk.data.remote.utils

import com.oguzhandongul.locationtrackingsdk.data.remote.utils.HttpErrorCodes.FORBIDDEN
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor for handling HTTP 403 (Forbidden) errors, typically caused by expired
 * or invalid access tokens. It automatically attempts to refresh the access token
 * and retries the original request using the new token.
 */
internal class NetworkInterceptor(
    private val tokenHelper: TokenHelper
) : Interceptor {

    /**
     *  Intercepts outgoing network requests and handles authorization errors:
     *  1. Executes the original request.
     *  2. If a 403 (Forbidden) response is received:
     *     * Closes the current response.
     *     * Refreshes the access token using the TokenHelper (performs this coroutine action synchronously).
     *     * Creates a new request with the refreshed authorization header.
     *     * Retries the original request with the updated authorization header.
     *  3. If the response code is not 403, returns the original response.
     *
     *  @param chain The chain of interceptors responsible for carrying out the request.
     *  @return The HTTP response from the server.
     *  @throws Exception if there's an error during the refresh process or if the retry also fails.
     */
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