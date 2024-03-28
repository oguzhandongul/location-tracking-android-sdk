package com.oguzhandongul.locationtrackingsdk.core

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.core.utils.RetrofitHelper
import com.oguzhandongul.locationtrackingsdk.core.utils.SecurityHelper
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.repository.NetworkRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.location.LocationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

object LocationSdk {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false

    private lateinit var config: SdkConfig

    fun initialize(context: Context, sdkConfig: SdkConfig) {
        this.config = sdkConfig
        val authRepository = AuthRepositoryImpl(SecurityHelper.getSharedPref(context))
        val apiService = RetrofitHelper.getApiService()
        val networkRepo = NetworkRepositoryImpl(sdkConfig, authRepository, apiService)
        LocationManager.initialize(context, sdkConfig, authRepository, networkRepo)
        isInitialized = true

        scope.launch {
            networkRepo.getInitialTokens()
        }
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate() {
        LocationManager.updateLocationSingleTime()
    }
}
