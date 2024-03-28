package com.oguzhandongul.locationtrackingsdk.core

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.remote.NetworkManager
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.location.LocationManager
import timber.log.Timber

object LocationSdk {

    private var isInitialized = false
    private lateinit var config: SdkConfig
    private lateinit var authRepository: AuthRepositoryImpl

    private const val PREF_FILENAME = "token_prefs"

    fun initialize(context: Context, sdkConfig: SdkConfig) {
        this.config = sdkConfig
        initAuthRepo(context = context)
        LocationManager.initialize(context, sdkConfig, authRepository)
        NetworkManager.initialize(context, sdkConfig, authRepository)

        isInitialized = true
    }

    private fun initAuthRepo(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPrefs = EncryptedSharedPreferences.create(
            context,
            PREF_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        authRepository = AuthRepositoryImpl(sharedPrefs)
    }

    fun isInitialized(): Boolean = isInitialized

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startTracking() {
        if (!isInitialized) {
            Timber.tag("LocationSDK")
                .e("SDK is not initialized. Call initialize(context, sdkConfig) first.")
            return
        }
        LocationManager.startLocationTracking()
    }

    fun stopTracking() {
        if (!isInitialized) {
            Timber.tag("LocationSDK").e("SDK is not initialized. Cannot stop tracking.")
            return
        }
        LocationManager.stopLocationTracking()
    }

    // Future implementation for a one-time location update
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate() {
        LocationManager.updateLocationSingleTime()
    }
}