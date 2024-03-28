package com.oguzhandongul.locationtrackingsdk.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse

object SecureTokenManager {
    private const val PREF_FILENAME = "token_prefs"

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var masterKey: MasterKey

    fun initialize(context: Context) {
        masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPrefs = EncryptedSharedPreferences.create(
            context,
            PREF_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(tokens: TokensResponse?) {
        tokens?.let {
            sharedPrefs.edit()
                .putString("access_token", it.accessToken)
                .putString("refresh_token", it.refreshToken)
                .apply()
        }

    }

    fun getTokens(): TokensResponse? {
        val accessToken = sharedPrefs.getString("access_token", null)
        val refreshToken = sharedPrefs.getString("refresh_token", null)

        return if (accessToken != null && refreshToken != null) {
            TokensResponse(accessToken, "", refreshToken)
        } else {
            null
        }
    }

    fun clearTokens() {
        sharedPrefs.edit().clear().apply()
    }

}