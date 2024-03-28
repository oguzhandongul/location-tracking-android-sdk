package com.oguzhandongul.locationtrack

import android.app.Application
import com.oguzhandongul.locationtrackingsdk.core.LocationSdk
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import timber.log.Timber

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationSdk.initialize(
            this,
            SdkConfig(apiKey = "xdk8ih3kvw2c66isndihzke5", debugMode = true)
        )
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}