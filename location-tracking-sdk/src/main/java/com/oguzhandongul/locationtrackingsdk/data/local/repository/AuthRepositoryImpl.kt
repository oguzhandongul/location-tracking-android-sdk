package com.oguzhandongul.locationtrackingsdk.data.local.repository

import android.content.SharedPreferences
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class AuthRepositoryImpl(private val sharedPrefs: SharedPreferences) : AuthRepository {

    override fun saveTokens(tokens: TokensResponse?) {
        tokens?.let {
            sharedPrefs.edit()
                .putString("access_token", it.accessToken)
                .putString("refresh_token", it.refreshToken)
                .putString("expires_at", it.expiresAt)
                .apply()
        }
    }

    override fun getTokens(): TokensResponse? {
        val accessToken = sharedPrefs.getString("access_token", null)
        val refreshToken = sharedPrefs.getString("refresh_token", null)
        val expiresAt = sharedPrefs.getString("expires_at", null)

        return if (accessToken != null && expiresAt != null && refreshToken != null) {
            TokensResponse(accessToken, expiresAt, refreshToken)
        } else {
            null
        }
    }

    override fun clearTokens() {
        sharedPrefs.edit().clear().apply()
    }

    override fun isTokenExpired(): Boolean {
        val expiresAt = getTokens()?.expiresAt
        val formatter = ISODateTimeFormat.dateTime()
        val expirationDateTime = DateTime.parse(expiresAt, formatter)
        return DateTime.now().isAfter(expirationDateTime)
    }
}