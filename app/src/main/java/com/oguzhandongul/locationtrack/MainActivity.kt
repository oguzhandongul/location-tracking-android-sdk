package com.oguzhandongul.locationtrack

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.oguzhandongul.locationtrack.ui.theme.LocationtrackTheme
import com.oguzhandongul.locationtrackingsdk.core.LocationSdk
import com.oguzhandongul.locationtrackingsdk.core.extensions.hasLocationPermissions


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationtrackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }

        requestLocationPermission()
    }

    override fun onDestroy() {
        //Stops tracking when the application is killed
        LocationSdk.stopTracking()
        super.onDestroy()
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission request results
        val granted = permissions.entries.all { it.value }
        if (granted) {
            // All permissions granted, proceed
            if (hasLocationPermissions()) {
                LocationSdk.startTracking()
            }
        } else {
            // Some permissions denied, handle it
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as Activity
    ClickableText(
        text = AnnotatedString("Click Here to update Location"),
        modifier = modifier,
        onClick = {
            if (activity.hasLocationPermissions()) {
                LocationSdk.requestLocationUpdate()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LocationtrackTheme {
        Greeting()
    }
}