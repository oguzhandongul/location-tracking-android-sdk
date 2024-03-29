package io.github.oguzhandongul.locationtrackingsdk.location.exceptions

/**
 * Exception indicating that location services are unavailable on the device.
 * This could be due to location settings being disabled or a more general issue with location providers.
 *
 * @param message A descriptive message explaining the situation.
 * @param cause An optional underlying cause (e.g., an ApiException).
 */
class LocationServicesUnavailableException(
    message: String = "Location services are not available or enabled",
    cause: Throwable? = null
) : LocationTrackingException(message, cause)