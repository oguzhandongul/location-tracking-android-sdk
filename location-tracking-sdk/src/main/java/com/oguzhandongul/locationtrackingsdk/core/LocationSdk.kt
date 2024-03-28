package com.oguzhandongul.locationtrackingsdk.core

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.location.LocationManager

object LocationSdk {

    private var isInitialized = false
    private lateinit var config: SdkConfig

    fun initialize(context: Context, sdkConfig: SdkConfig) {
        this.config = sdkConfig
        LocationManager.initialize(context, sdkConfig)
        isInitialized = true
    }

    fun isInitialized(): Boolean = isInitialized

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startTracking() {
        if (!isInitialized) {
            Log.e("LocationSDK", "SDK is not initialized. Call initialize(context, sdkConfig) first.")
            return
        }
        LocationManager.startLocationTracking()
    }

    fun stopTracking() {
        if (!isInitialized) {
            Log.e("LocationSDK", "SDK is not initialized. Cannot stop tracking.")
            return
        }
        LocationManager.stopLocationTracking()
    }

    // Future implementation for a one-time location update
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate() {
        // TODO: Implement one-time location update logic
    }
}