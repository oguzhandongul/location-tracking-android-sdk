package io.github.oguzhandongul.locationtrackingsdk.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Helper object for creating and retrieving secure SharedPreferences instances
 * using Android's EncryptedSharedPreferences. This provides an additional layer
 * of security for sensitive data stored on the device.
 */

internal object SecurityHelper {

    /**
     *  Name of the SharedPreferences file.
     */
    private const val PREF_FILENAME = "token_prefs"

    /**
     * Obtains an EncryptedSharedPreferences instance for secure data storage.
     *
     * @param context The application Context.
     * @return An EncryptedSharedPreferences object ready for secure storage of key-value pairs.
     */
    fun getSharedPref(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}