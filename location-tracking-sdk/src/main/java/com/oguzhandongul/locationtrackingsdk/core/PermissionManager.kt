package com.oguzhandongul.locationtrackingsdk.core

import android.app.Activity
import android.content.Context

object PermissionManager {
    fun hasLocationPermission(context: Context): Boolean {
        // Check if location permissions are granted
        return true // TODO Default Implementation, change it after implementation
    }

    fun requestLocationPermission(activity: Activity) {
        // Request location permissions from the user
    }
}