package com.oguzhandongul.locationtrack

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oguzhandongul.locationtrack.ui.theme.LocationtrackTheme
import com.oguzhandongul.locationtrackingsdk.core.LocationSdk
import com.oguzhandongul.locationtrackingsdk.core.extensions.askLocationPermission
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
                    LocationTrackerUI()
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
fun LocationTrackerUI() {
    val activity = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (activity.hasLocationPermissions()) {
                    LocationSdk.startTracking()
                } else {
                    activity.askLocationPermission()
                }
            }
        ) {
            Text("Start Tracking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                LocationSdk.stopTracking()
            }
        ) {
            Text("Stop Tracking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(enabled = true,onClick = {if (activity.hasLocationPermissions()) {
            LocationSdk.requestLocationUpdate()
        } else {
            activity.askLocationPermission()
        }}){
            Text("Request One Time Location Update")
        }


    }
}