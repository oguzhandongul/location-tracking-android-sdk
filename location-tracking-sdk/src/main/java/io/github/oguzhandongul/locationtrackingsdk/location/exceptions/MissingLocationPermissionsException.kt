package io.github.oguzhandongul.locationtrackingsdk.location.exceptions

/**
 * Exception indicating that the required location permissions are missing.
 *
 * @param message A descriptive message. (Optional, as a default is provided)
 */
class MissingLocationPermissionsException(message: String = "Location permissions are missing") :
    LocationTrackingException(message)