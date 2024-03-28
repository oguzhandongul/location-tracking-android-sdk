package com.oguzhandongul.locationtrackingsdk.location

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.oguzhandongul.locationtrackingsdk.core.SecureTokenManager
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.remote.NetworkManager
import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import timber.log.Timber

object LocationManager {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var request: LocationRequest
    private lateinit var config: SdkConfig

    fun initialize(context: Context, config: SdkConfig) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        this.config = config
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startLocationTracking() {
        try {
            createLocationUpdates()
            requestLocationUpdates()
        } catch (ex: SecurityException) {
            Timber.tag("LocationSDK")
                .e("Location Permissions are missing. Request and get the permissions first.")
        }
    }

    fun stopLocationTracking() {
        fusedLocationClient.flushLocations()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationUpdates() {
        request = LocationRequest.Builder(
            config.priority,
            config.updateInterval
        ).apply {
            setMinUpdateDistanceMeters(config.minUpdateDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestLocationUpdates() =
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                SecureTokenManager.getTokens()?.accessToken?.let {
                    val req = LocationUpdateRequest(location.longitude, location.latitude)
                    NetworkManager.updateLocation(it, req) {
                        Timber.tag("LocationSDK")
                            .i("Updated location: " + location.latitude + " / " + location.longitude)
                    }
                }
                Timber.tag("LocationSDK")
                    .i("Got the result of location request." + location.latitude + " / " + location.longitude)
            }
        }
    }
}
