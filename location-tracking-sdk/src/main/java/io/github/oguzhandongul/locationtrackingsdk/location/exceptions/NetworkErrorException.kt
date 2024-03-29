package io.github.oguzhandongul.locationtrackingsdk.location.exceptions

/**
 * Exception indicating a network-related error occurred while communicating
 * with the backend server during a location update.
 *
 * @param message A descriptive message explaining the situation.
 * @param cause An optional underlying cause (e.g., an IOException or HttpException).
 */
class NetworkErrorException(
    message: String = "Network error during location update",
    cause: Throwable? = null
) : LocationTrackingException(message, cause)