# Location Tracking SDK for Android

The Location Tracking SDK for Android allows developers to easily integrate location tracking features into their applications. With this SDK, you can capture regular location updates and send them to your backend service. Additionally, you can request a location update on demand.

## Features

-   **Regular Location Updates:** Automatically capture location updates at specified intervals and send them to a designated backend service.
-   **On-Demand Location Update:** Allows the user of the SDK to request the current location to be captured and sent to the backend service immediately.

## Getting Started

### Prerequisites

-   Android SDK API level 21 (Android 5.0) or higher
-   Android Studio

### Installation

1.  Add the SDK to your project's `build.gradle` file:

``` gradle
`dependencies {
    implementation 'com.oguzhandongul:locationtrackingsdk:x.y.z'
}` 
```

Replace `x.y.z` with the latest version of the Location Tracking SDK.

2.  Ensure you have the necessary permissions in your `AndroidManifest.xml`:

``` xml
`<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />` 
```

### Initialize the SDK

Initialize the SDK in your application's `Application` class:

``` kotlin
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationSdk.initialize(
            this,
            SdkConfig(apiKey = "YOUR_API_KEY", debugMode = true)
        )
        // Setup Timber for logging in debug mode
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
```

### Usage

To start tracking the location, make sure you request location permissions from the user. Once granted, you can start location tracking:

``` kotlin 
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup your UI here
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        // Request location permissions and handle the result
    }
}
```

To request an on-demand location update:

``` kotlin
LocationSdk.requestLocationUpdate()
```
Make sure to stop the location tracking when it's no longer needed or when your app is destroyed:

``` kotlin
override fun onDestroy() {
    LocationSdk.stopTracking()
    super.onDestroy()
}
``` 

## Permissions

This SDK requires the following permissions:

-   `ACCESS_FINE_LOCATION` for accessing the GPS location.
-   `ACCESS_COARSE_LOCATION` for accessing the network location.

Please ensure you handle these permissions appropriately in your application to respect user privacy and comply with Google Play policies.

## Support

For support, please open an issue on the GitHub repository or contact the development team.

## License

This SDK is licensed under the Apache License, Version 2.0. See the LICENSE file for more details.
