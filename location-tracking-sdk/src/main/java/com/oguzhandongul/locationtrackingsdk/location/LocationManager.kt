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
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.data.remote.requests.LocationUpdateRequest
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.repository.NetworkRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import com.oguzhandongul.locationtrackingsdk.domain.repository.NetworkRepository
import timber.log.Timber

object LocationManager {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestTrack: LocationRequest
    private lateinit var requestOneTime: LocationRequest
    private lateinit var config: SdkConfig
    private lateinit var authRepository: AuthRepository
    private lateinit var networkRepository: NetworkRepository

    fun initialize(context: Context, config: SdkConfig, authRepository: AuthRepositoryImpl, networkRepository: NetworkRepositoryImpl) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        this.config = config
        this.authRepository = authRepository
        this.networkRepository = networkRepository
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startLocationTracking() {
        try {
            buildLocationUpdates()
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun updateLocationSingleTime() {
        try {
            buildSingleLocationUpdate()
            requestSingleLocationUpdate()
        } catch (ex: SecurityException) {
            Timber.tag("LocationSDK")
                .e("Location Permissions are missing. Request and get the permissions first.")
        }
    }

    private fun buildLocationUpdates() {
        requestTrack = LocationRequest.Builder(
            config.priority,
            config.updateInterval
        ).apply {
            setMinUpdateDistanceMeters(config.minUpdateDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }

    private fun buildSingleLocationUpdate() {
        requestOneTime = LocationRequest.Builder(
            config.priority,
            config.updateInterval
        ).apply {
            setMaxUpdates(1)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestLocationUpdates() =
        fusedLocationClient.requestLocationUpdates(
            requestTrack,
            locationCallback,
            Looper.getMainLooper()
        )

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestSingleLocationUpdate() =
        fusedLocationClient.requestLocationUpdates(
            requestOneTime,
            locationSingleCallback,
            Looper.getMainLooper()
        )

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                authRepository.getTokens()?.accessToken?.let {
                    val req = LocationUpdateRequest(location.longitude, location.latitude)
                    networkRepository.updateLocation(it, req) {
                        Timber.tag("LocationSDK")
                            .i("Updated location: " + location.latitude + " / " + location.longitude)
                    }
                }
                Timber.tag("LocationSDK")
                    .i("Got the result of location request." + location.latitude + " / " + location.longitude)
            }
        }
    }

    private val locationSingleCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                authRepository.getTokens()?.accessToken?.let {
                    val req = LocationUpdateRequest(location.longitude, location.latitude)
                    networkRepository.updateLocation(it, req) {
                        Timber.tag("LocationSDK")
                            .i("One Time Updated location: " + location.latitude + " / " + location.longitude)
                    }
                }
                Timber.tag("LocationSDK")
                    .i("One Time Got the result of location request." + location.latitude + " / " + location.longitude)
            }
        }
    }
}
