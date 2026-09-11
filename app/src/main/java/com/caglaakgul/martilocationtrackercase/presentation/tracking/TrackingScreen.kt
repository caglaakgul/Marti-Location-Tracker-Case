package com.caglaakgul.martilocationtrackercase.presentation.tracking

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.hilt.navigation.compose.hiltViewModel
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.ui.theme.MartiLocationTrackerCaseTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationPermissions = locationPermissions()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.onLocationPermissionResult(
            isGranted = permissions.values.any { isGranted -> isGranted }
        )
    }

    LaunchedEffect(Unit) {
        val hasLocationPermission = locationPermissions.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
        }

        if (hasLocationPermission) {
            viewModel.onLocationPermissionResult(isGranted = true)
        } else {
            permissionLauncher.launch(locationPermissions)
        }
    }

    TrackingContent(
        uiState = uiState
    )
}

@Composable
fun TrackingContent(
    uiState: TrackingUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TrackingMap(
                currentLocation = uiState.currentLocation,
                hasLocationPermission = uiState.hasLocationPermission,
                modifier = Modifier.fillMaxSize()
            )

            if (uiState.isLoadingLocation) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun TrackingMap(
    currentLocation: UserLocation?,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(41.0082, 28.9784)
    val mapLocation = currentLocation?.toLatLng() ?: defaultLocation
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapLocation, DEFAULT_ZOOM)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location.toLatLng(), DEFAULT_ZOOM)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
    ) {
        currentLocation?.let { location ->
            Marker(
                state = MarkerState(position = location.toLatLng()),
                title = "Bulunduğun konum"
            )
        }
    }
}

private fun UserLocation.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

private fun locationPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

private const val DEFAULT_ZOOM = 16f

@Preview(showBackground = true)
@Composable
private fun TrackingContentPreview() {
    MartiLocationTrackerCaseTheme {
        TrackingContent(
            uiState = TrackingUiState(
                currentLocation = UserLocation(
                    latitude = 41.0082,
                    longitude = 28.9784
                ),
                hasLocationPermission = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingLoadingPreview() {
    MartiLocationTrackerCaseTheme {
        TrackingContent(
            uiState = TrackingUiState(isLoadingLocation = true)
        )
    }
}