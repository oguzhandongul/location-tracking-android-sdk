package io.github.oguzhandongul.locationtrackingsdk.location.repository

import android.Manifest
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.github.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import io.github.oguzhandongul.locationtrackingsdk.data.remote.models.requests.LocationUpdateRequest
import io.github.oguzhandongul.locationtrackingsdk.domain.repository.AuthRepository
import io.github.oguzhandongul.locationtrackingsdk.domain.repository.LocationRepository
import io.github.oguzhandongul.locationtrackingsdk.domain.repository.NetworkRepository
import io.github.oguzhandongul.locationtrackingsdk.location.exceptions.LocationServicesUnavailableException
import io.github.oguzhandongul.locationtrackingsdk.location.exceptions.MissingLocationPermissionsException
import io.github.oguzhandongul.locationtrackingsdk.location.exceptions.NetworkErrorException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class LocationRepositoryImpl(
    private var fusedLocationClient: FusedLocationProviderClient,
    private var config: SdkConfig,
    private var authRepository: AuthRepository,
    private var networkRepository: NetworkRepository
) : LocationRepository {

    /**
     * CoroutineScope for managing background location updates. Using SupervisorJob ensures
     * that failures in individual location update tasks don't cancel the entire scope.
     */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Builds a LocationRequest object based on the provided SDK configuration.
     *
     * @param maxUpdates The maximum number of location updates to receive.
     *                   Defaults to unlimited updates for continuous tracking.
     * @return A LocationRequest object configured for location updates.
     */
    private fun buildLocationRequest(maxUpdates: Int = -1): LocationRequest {
        return LocationRequest.Builder(config.priority, config.updateInterval)
            .apply {
                if (maxUpdates <= 0) setMinUpdateDistanceMeters(config.minUpdateDistance)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
                if (maxUpdates > 0) setMaxUpdates(maxUpdates)
            }.build()
    }

    /**
     * Initializes continuous location tracking. Builds the necessary LocationRequest and
     * requests location updates from the FusedLocationProviderClient. Handles potential
     * SecurityExceptions.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun startLocationTracking() {
        try {
            fusedLocationClient.requestLocationUpdates(
                buildLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            throw MissingLocationPermissionsException()
        } catch (e: ApiException) {
            when (e.statusCode) {
                RESOLUTION_REQUIRED -> throw LocationServicesUnavailableException(
                    "Location services are not enabled or available. Enable location in settings",
                    e
                )

                else -> throw LocationServicesUnavailableException(cause = e)
            }
        }
    }

    /**
     * Stops continuous location tracking by removing location updates.
     */
    override fun stopLocationTracking() {
        fusedLocationClient.flushLocations()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Requests a single location update. Handles potential SecurityExceptions.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun updateLocationSingleTime() {
        try {
            fusedLocationClient.requestLocationUpdates(
                buildLocationRequest(1),
                locationSingleCallback,
                Looper.getMainLooper()
            )
        } catch (ex: SecurityException) {
            Timber.tag("LocationSDK")
                .e("Location Permissions are missing. Request and get the permissions first.")
        }
    }

    /**
     * Callback for continuous location updates. Fetches the access token and sends
     * location updates to the server using a coroutine.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                authRepository.getTokens()?.accessToken?.let {
                    scope.launch {
                        try {
                            val req = LocationUpdateRequest(location.longitude, location.latitude)
                            networkRepository.updateLocation(it, req)
                        } catch (e: IOException) {
                            throw NetworkErrorException("Network error during location update", e)
                        } catch (e: HttpException) {
                            // Handle specific HTTP error codes, if needed
                            throw NetworkErrorException("API request failed", e)
                        }
                    }
                }
                Timber.tag("LocationSDK")
                    .i("Got the result of location request." + location.latitude + " / " + location.longitude)
            }
        }
    }

    /**
     * Callback for single location update. Fetches access token, sends location update,
     * and then removes location updates to stop further tracking.
     */
    private val locationSingleCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            fusedLocationClient.removeLocationUpdates(this)
            for (location in locationResult.locations) {
                authRepository.getTokens()?.accessToken?.let {
                    scope.launch {
                        val req = LocationUpdateRequest(location.longitude, location.latitude)
                        networkRepository.updateLocation(it, req)
                    }
                }
                Timber.tag("LocationSDK")
                    .i("One Time Got the result of location request." + location.latitude + " / " + location.longitude)
            }
        }
    }
}