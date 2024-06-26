package io.github.oguzhandongul.locationtrackingsdk.core.models

import com.google.android.gms.location.Priority

data class SdkConfig(
    val updateInterval: Long = 5000, // Default update interval in milliseconds
    val fastestInterval: Long = 5000, // Fastest interval for location updates in milliseconds
    val minUpdateDistance: Float = 50F, // Minimal Distance change for location updates in meters
    val priority: Int = Priority.PRIORITY_HIGH_ACCURACY, // Default priority of location requests
    val apiKey: String = "xdk8ih3kvw2c66isndihzke5", // API key for authentication with the backend server
    val debugMode: Boolean = false // Toggle for enabling debug logging
)