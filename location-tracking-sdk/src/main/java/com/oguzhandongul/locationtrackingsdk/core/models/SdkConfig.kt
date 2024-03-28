package com.oguzhandongul.locationtrackingsdk.core.models

import com.google.android.gms.location.Priority

data class SdkConfig(
    val updateInterval: Long = 5000, // Default update interval in milliseconds
    val fastestInterval: Long = 5000, // Fastest interval for location updates in milliseconds
    val minUpdateDistance: Float = 50F, // Minimal Distance change for location updates in meters
    val priority: Int = Priority.PRIORITY_HIGH_ACCURACY, // Default priority of location requests
    val backendUrl: String = "", // URL of the backend server to which locations are sent
    val apiKey: String = "", // API key for authentication with the backend server
    val debugMode: Boolean = false // Toggle for enabling debug logging
)