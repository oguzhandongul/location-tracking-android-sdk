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

    fun initialize(context: Context, config: SdkConfig) {
        LocationManager.initialize(context)
        this.config = config
        isInitialized = true
    }

    fun getConfig() = this.config


    fun isInitialized(): Boolean {
        return isInitialized
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startTracking() {
        // Ensure SDK is initialized before proceeding
        if (!isInitialized) {
            Log.e(
                "LocationSDK",
                "SDK is not initialized. Call initialize(context, configs) first."
            )
            return
        }
        LocationManager.startLocationTracking()
    }

    fun stopTracking(){
        LocationManager.stopLocationTracking()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdate(){
        //TODO request one time location update
    }
}