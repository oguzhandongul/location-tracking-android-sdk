package com.oguzhandongul.locationtrackingsdk
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

import com.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import com.oguzhandongul.locationtrackingsdk.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before

@RunWith(MockitoJUnitRunner::class)
class LocationManagerTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)


    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Mock
    private lateinit var locationRequest: LocationRequest

    @Mock
    private lateinit var locationCallback: LocationCallback

    @Captor
    private lateinit var locationResultCaptor: ArgumentCaptor<LocationResult>

    private lateinit var locationManager: LocationManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        locationManager = LocationManager // Assuming you now inject fusedLocationClient
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset back to the normal Main Dispatcher
    }

    @Test
    fun testStartLocationTrackingWithSuccessfulUpdates() = testScope.runBlockingTest {
        // Sample Config
        val testConfig = SdkConfig(
            updateInterval = 1000L,
            minUpdateDistance = 10F,
            priority = Priority.PRIORITY_HIGH_ACCURACY
        )

        // Sample Location for Mocking
        val mockLocation = Location("test")
        mockLocation.latitude = 52.0
        mockLocation.longitude = 3.0

        // Set up the mock behavior
        `when`(fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, any()))
            .thenReturn(mock(Task::class.java) as Task<Void>?)

        locationManager.initialize(context, testConfig)
        locationManager.startLocationTracking()

        // Capture the LocationResult
        verify(locationCallback).onLocationResult(locationResultCaptor.capture())

        // Assertions
        val capturedLocationResult = locationResultCaptor.value
        assertEquals(1, capturedLocationResult.locations.size)
        assertEquals(52.0, capturedLocationResult.lastLocation?.latitude)
        assertEquals(3.0, capturedLocationResult.lastLocation?.longitude)
    }

    @Test
    fun `test startLocationTracking with missing permission`() = testScope.runBlockingTest {
        // TODO add tests
    }
}