package com.oguzhandongul.locationtrack

import android.app.Application
import io.github.oguzhandongul.locationtrackingsdk.core.LocationSdk
import io.github.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import timber.log.Timber

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationSdk.initialize(
            this,
            SdkConfig(
                apiKey = "xdk8ih3kvw2c66isndihzke5",
                debugMode = true,
                fastestInterval = 5000L,
                minUpdateDistance = 50F,
                updateInterval = 5000L
            )
        )
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}