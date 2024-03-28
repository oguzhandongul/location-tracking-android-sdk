package com.oguzhandongul.locationtrackingsdk.data.local.repository

import android.content.SharedPreferences
import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

/**
 * Implementation of the AuthRepository interface responsible for managing access
 * and refresh tokens locally using SharedPreferences.
 */
class AuthRepositoryImpl(private val sharedPrefs: SharedPreferences) : AuthRepository {

    /**
     *  Contains constants for keys used in SharedPreferences to store authentication tokens.
     */
    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
    }

    /**
     * Stores access, refresh, and expiration information from a TokensResponse object
     * into SharedPreferences. Falls back to using the accessToken as refreshToken
     * if the refreshToken is not provided in the response.
     *
     * @param tokens The TokensResponse object containing tokens and expiration data.
     */
    override fun saveTokens(tokens: TokensResponse?) {
        tokens?.let {
            sharedPrefs.edit()
                .putString(KEY_ACCESS_TOKEN, it.accessToken)
                .putString(KEY_REFRESH_TOKEN, it.refreshToken ?: it.accessToken)
                .putString(KEY_EXPIRES_AT, it.expiresAt)
                .apply()
        }
    }

    /**
     * Retrieves tokens from SharedPreferences. Parses them into a TokensResponse object
     * if all required values (accessToken, refreshToken, expiresAt) are present.
     *
     * @return A TokensResponse object if valid tokens are found, otherwise null.
     */
    override fun getTokens(): TokensResponse? {
        val accessToken = sharedPrefs.getString(KEY_ACCESS_TOKEN, null)
        val refreshToken = sharedPrefs.getString(KEY_REFRESH_TOKEN, null)
        val expiresAt = sharedPrefs.getString(KEY_EXPIRES_AT, null)

        return if (accessToken != null && expiresAt != null && refreshToken != null) {
            TokensResponse(accessToken, expiresAt, refreshToken)
        } else {
            null
        }
    }

    /**
     * Removes all saved token data from SharedPreferences.
     */
    override fun clearTokens() {
        sharedPrefs.edit().clear().apply()
    }

    /**
     * Checks if the stored access token has expired. Parses the stored expiration time
     * using Joda-Time and compares it to the current time.
     *
     * @return True if the access token has expired, false otherwise.
     */
    override fun isTokenExpired(): Boolean {
        // Handle null expiresAt
        val expiresAt = getTokens()?.expiresAt ?: return false

        // Proceed with parsing and comparison if expiresAt exists
        val formatter = ISODateTimeFormat.dateTime()
        val expirationDateTime = DateTime.parse(expiresAt, formatter)
        return DateTime.now().isAfter(expirationDateTime)
    }
}