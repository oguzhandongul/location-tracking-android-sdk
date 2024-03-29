package com.oguzhandongul.locationtrackingsdk.core.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class LocationServicesHelper(private val context: Context) {

    /**
     * Checks if all required services (Google Play Services, GPS, Internet) are available.
     * If all are available, executes the provided onSuccess action. Otherwise, displays a Toast.
     *
     * @param onSuccess A function (lambda) to be executed if all services are available.
     */
    fun checkServiceAvailabilities(onSuccess: () -> Unit) {
        if (isGooglePlayServicesAvailable() && isGpsEnabled() && isInternetConnected()) {
            onSuccess() // Call the success action
        } else {
            Toast.makeText(
                context,
                "Please Enable Internet, GPS and Google Play Services",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Checks if Google Play Services are available on the device.
     * If not, attempts to show an error dialog to resolve the issue.
     *
     * @return true if Google Play Services are available, false otherwise.
     */
    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return if (resultCode == ConnectionResult.SUCCESS) {
            true
        } else {
            showGooglePlayServicesErrorDialog(resultCode)
            false
        }
    }

    /**
     * Checks if GPS is enabled on the device. If not, prompts the user to enable it.
     *
     * @return true if GPS is enabled, false otherwise.
     */
    private fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else {
            promptUserToEnableGps()
            false
        }
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @return true if there is an internet connection, false otherwise.
     */
    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // For modern Android versions:
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            // For older Android versions
            val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
            return activeNetwork.isConnectedOrConnecting
        }
    }

    /**
     * Attempts to display a dialog to resolve a Google Play Services error.
     * If the error is user-resolvable, the built-in error dialog is shown.
     * Otherwise, a generic error message is presented.
     *
     * @param errorCode The error code returned by GoogleApiAvailability.
     */
    private fun showGooglePlayServicesErrorDialog(errorCode: Int) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()

        // Check if the error is resolvable
        if (googleApiAvailability.isUserResolvableError(errorCode)) {
            val dialog = googleApiAvailability.getErrorDialog(
                context as Activity,  // Ensure the context is an Activity
                errorCode,
                REQUEST_CODE_GOOGLE_PLAY_SERVICES
            )
            dialog?.show()
        } else {
            // Handle non-resolvable error (e.g., device doesn't support Google Play Services)
            AlertDialog.Builder(context)
                .setMessage("This device does not support Google Play Services.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    /**
     * Displays a dialog prompting the user to enable GPS (location services).
     * The dialog guides the user to the appropriate settings screen if they choose to enable it.
     */
    private fun promptUserToEnableGps() {
        AlertDialog.Builder(context)
            .setMessage("GPS is disabled. Please enable it in settings.")
            .setPositiveButton("Settings") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1001
    }
}