package com.oguzhandongul.locationtrackingsdk.location.exceptions

/**
 * Base exception class for errors encountered within the LocationTracking SDK
 *
 * @param message A descriptive message explaining the error.
 * @param cause An optional underlying cause of the error (another Exception).
 */
open class LocationTrackingException(message: String, cause: Throwable? = null) :
    Exception(message, cause)