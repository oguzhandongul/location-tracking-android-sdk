package com.oguzhandongul.locationtrackingsdk.core

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.core.utils.RetrofitHelper
import com.oguzhandongul.locationtrackingsdk.core.utils.SecurityHelper
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.repository.NetworkRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.domain.repository.LocationRepository
import com.oguzhandongul.locationtrackingsdk.location.repository.LocationRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

object LocationSdk {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false

    private lateinit var config: SdkConfig
    private lateinit var locationRepository: LocationRepository

    fun initialize(context: Context, sdkConfig: SdkConfig) {
        this.config = sdkConfig
        val authRepo = AuthRepositoryImpl(SecurityHelper.getSharedPref(context))
        val apiService = RetrofitHelper.getApiService(authRepo)
        val networkRepo = NetworkRepositoryImpl(sdkConfig, authRepo, apiService)
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        locationRepository = LocationRepositoryImpl(fusedClient, sdkConfig, authRepo, networkRepo)
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
        locationRepository.startLocationTracking()
    }

    fun stopTracking() {
        if (!isInitialized) {
            Timber.tag("LocationSDK").e("SDK is not initialized. Cannot stop tracking.")
            return
        }
        locationRepository.stopLocationTracking()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate() {
        locationRepository.updateLocationSingleTime()
    }
}
