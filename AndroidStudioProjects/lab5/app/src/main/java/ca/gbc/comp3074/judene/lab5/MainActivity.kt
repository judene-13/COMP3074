package ca.gbc.comp3074.judene.lab5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ca.gbc.comp3074.judene.lab5.ui.theme.Lab5Theme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Lab5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationComponent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationComponent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val fusedClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val location = remember { mutableStateOf<Location?>(null) }
    val status = remember { mutableStateOf("Ready") }
    val tracking = remember { mutableStateOf(false) }
    val pendingAction = remember { mutableStateOf("") }

    val currentRequest = remember {
        mutableStateOf<CancellationTokenSource?>(null)
    }

    val cameraPositionState = rememberCameraPositionState()
    val markerState = remember { MarkerState(position = LatLng(0.0, 0.0)) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (!tracking.value) return

                result.lastLocation?.let {
                    location.value = it
                    status.value = "Tracking location..."
                }
            }
        }
    }

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fine == PackageManager.PERMISSION_GRANTED ||
                coarse == PackageManager.PERMISSION_GRANTED
    }

    fun stopTracking() {
        tracking.value = false
        fusedClient.removeLocationUpdates(locationCallback)
    }

    fun cancelCurrentRequest() {
        val request = currentRequest.value
        currentRequest.value = null
        request?.cancel()
    }

    fun fetchLocation() {
        if (!hasPermission()) {
            status.value = "Location permission not granted"
            return
        }

        stopTracking()
        cancelCurrentRequest()

        val tokenSource = CancellationTokenSource()
        currentRequest.value = tokenSource
        status.value = "Fetching location..."

        try {
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                tokenSource.token
            ).addOnSuccessListener { currentLocation ->
                if (currentRequest.value !== tokenSource) {
                    return@addOnSuccessListener
                }

                currentRequest.value = null

                if (currentLocation == null) {
                    status.value = "Location not found. Check device location."
                } else {
                    location.value = currentLocation
                    status.value = "Location found"
                }
            }.addOnFailureListener { error ->
                if (currentRequest.value === tokenSource) {
                    currentRequest.value = null
                    status.value = error.message ?: "Unable to get location"
                }
            }
        } catch (error: SecurityException) {
            cancelCurrentRequest()
            status.value = "Location permission not granted"
        }
    }

    fun startTracking() {
        if (!hasPermission()) {
            status.value = "Location permission not granted"
            return
        }

        if (tracking.value) return

        cancelCurrentRequest()

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateIntervalMillis(2000L).build()

        try {
            tracking.value = true
            status.value = "Tracking location..."

            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnFailureListener { error ->
                stopTracking()
                status.value = error.message ?: "Unable to start tracking"
            }
        } catch (error: SecurityException) {
            stopTracking()
            status.value = "Location permission not granted"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val action = pendingAction.value
        pendingAction.value = ""

        if (results.values.any { it }) {
            if (action == "track") {
                startTracking()
            } else if (action == "get") {
                fetchLocation()
            }
        } else {
            status.value = "Location permission not granted"
        }
    }

    DisposableEffect(lifecycleOwner, fusedClient, locationCallback) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                stopTracking()
                cancelCurrentRequest()
                status.value = "Location updates stopped"
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopTracking()
            cancelCurrentRequest()
        }
    }

    val currentLocation = location.value

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            val position = LatLng(it.latitude, it.longitude)
            markerState.position = position
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(position, 15f)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = status.value,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Latitude: ${currentLocation?.latitude ?: "—"}",
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Longitude: ${currentLocation?.longitude ?: "—"}",
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (hasPermission()) {
                    fetchLocation()
                } else {
                    pendingAction.value = "get"
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Get Location")
        }

        Button(
            onClick = {
                if (hasPermission()) {
                    startTracking()
                } else {
                    pendingAction.value = "track"
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = !tracking.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Start Tracking")
        }

        Button(
            onClick = {
                stopTracking()
                cancelCurrentRequest()
                status.value = "Location tracking stopped"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Stop Tracking")
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            if (currentLocation != null) {
                Marker(
                    state = markerState,
                    title = "Your location"
                )
            }
        }
    }
}