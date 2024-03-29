package com.oguzhandongul.locationtrackingsdk.core

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.core.utils.LocationServicesHelper
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

/**
 * Singleton Object for managing the lifecycle and functionality of the Location Tracking SDK.
 * Responsible for initialization, starting/stopping tracking, and providing access
 * to location updates.
 */
object LocationSdk {

    /**
     *  CoroutineScope for handling background tasks within the SDK.
     */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     *  Indicates whether the SDK has been initialized.
     */
    private var isInitialized = false
    private var isTracking = false

    /**
     *  SDK configuration object.
     */
    private lateinit var config: SdkConfig
    private lateinit var serviceHelper: LocationServicesHelper

    /**
     *  The main LocationRepository instance used for interactions with location services.
     */
    private lateinit var locationRepository: LocationRepository

    /**
     *  Initializes the SDK with the provided configuration and application context.
     *  This function creates the necessary dependencies and fetches initial tokens.
     *
     *  @param context Application context, used for accessing shared preferences and other system services.
     *  @param sdkConfig The configuration object for the SDK.
     */
    fun initialize(context: Context, sdkConfig: SdkConfig) {
        this.config = sdkConfig
        serviceHelper = LocationServicesHelper(context = context)
        val authRepo = AuthRepositoryImpl(sharedPrefs = SecurityHelper.getSharedPref(context))
        val apiService = RetrofitHelper.getApiService(authRepository = authRepo)
        val networkRepo = NetworkRepositoryImpl(
            config = sdkConfig,
            authRepository = authRepo,
            apiService = apiService
        )
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        locationRepository = LocationRepositoryImpl(
            fusedLocationClient = fusedClient,
            config = sdkConfig,
            authRepository = authRepo,
            networkRepository = networkRepo
        )
        isInitialized = true
        serviceHelper.checkServiceAvailabilities {
            scope.launch {
                networkRepo.getInitialTokens()
            }
        }
    }

    /**
     *  Starts continuous location tracking.  Requires location permissions.
     *  Throws an IllegalStateException if the SDK has not been initialized.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startTracking() {
        if (!isInitialized) {
            throw IllegalStateException("SDK is not initialized. Call initialize(context, sdkConfig) first.")
        }
        serviceHelper.checkServiceAvailabilities {
            locationRepository.startLocationTracking()
            isTracking = true
        }
    }

    /**
     *  Stops continuous location tracking.
     *  Throws an IllegalStateException if the SDK has not been initialized.
     */
    fun stopTracking() {
        if (!isInitialized) {
            throw IllegalStateException("SDK is not initialized. Cannot stop tracking.")
        }
        serviceHelper.checkServiceAvailabilities {
            locationRepository.stopLocationTracking()
            isTracking = false
        }
    }

    /**
     *  Requests a single location update.  Requires location permissions.
     *  Throws an IllegalStateException if the SDK has not been initialized.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate() {
        if (!isInitialized) {
            throw IllegalStateException("SDK is not initialized. Call initialize(context, sdkConfig) first.")
        }
        serviceHelper.checkServiceAvailabilities {
            locationRepository.updateLocationSingleTime()
        }
    }
}
