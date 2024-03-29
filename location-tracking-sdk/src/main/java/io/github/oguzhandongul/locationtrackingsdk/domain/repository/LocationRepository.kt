package io.github.oguzhandongul.locationtrackingsdk.domain.repository

internal interface LocationRepository {
    fun startLocationTracking()
    fun stopLocationTracking()
    fun updateLocationSingleTime()
}