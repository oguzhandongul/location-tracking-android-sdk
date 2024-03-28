package com.oguzhandongul.locationtrackingsdk.location

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig

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
            Log.e(
                "LocationSDK",
                "Location Permissions are missing. Request and get the permissions first."
            )
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
                // TODO Send location data to the Network Module
                Log.e(
                    "LocationSDK",
                    "Got the result of location request."
                )
            }
        }
    }
}
