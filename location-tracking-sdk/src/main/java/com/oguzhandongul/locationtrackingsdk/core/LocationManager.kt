package com.oguzhandongul.locationtrackingsdk.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig

@SuppressLint("MissingPermission")
object LocationManager {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var request: LocationRequest

    fun initialize(context: Context, config: SdkConfig) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (PermissionManager.hasLocationPermission(context)) {
            createLocationUpdates(config)
            startLocationTracking()
        }
    }

    private fun createLocationUpdates(config: SdkConfig) {
        request = LocationRequest.Builder(
            config.priority,
            config.updateInterval
        ).apply {
                setMinUpdateDistanceMeters(config.minUpdateDistance)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
    }

    private fun startLocationTracking() =
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())


    fun stopLocationTracking() {
        fusedLocationClient.flushLocations()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // Send location data to the Network Module
                //NetworkManager.sendLocation(location)
            }
        }
    }
}