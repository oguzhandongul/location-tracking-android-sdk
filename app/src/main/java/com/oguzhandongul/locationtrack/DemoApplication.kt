package com.oguzhandongul.locationtrack

import android.app.Application
import com.oguzhandongul.locationtrackingsdk.core.LocationSdk
import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationSdk.initialize(this, SdkConfig())
    }
}