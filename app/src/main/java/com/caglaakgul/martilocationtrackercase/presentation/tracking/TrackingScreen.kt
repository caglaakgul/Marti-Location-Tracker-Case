package com.caglaakgul.martilocationtrackercase.presentation.tracking

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.hilt.navigation.compose.hiltViewModel
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.presentation.tracking.components.AddressPanel
import com.caglaakgul.martilocationtrackercase.presentation.tracking.components.TrackingControls
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.defaultTrackingTexts
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.toLatLng
import com.caglaakgul.martilocationtrackercase.ui.theme.MartiLocationTrackerCaseTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
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
        viewModel.onAction(
            TrackingUiAction.LocationPermissionChanged(
                isGranted = permissions.values.any { isGranted -> isGranted }
            )
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.onAction(TrackingUiAction.StartTrackingClicked)
    }

    LaunchedEffect(Unit) {
        val hasLocationPermission = locationPermissions.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
        }

        if (hasLocationPermission) {
            viewModel.onAction(TrackingUiAction.LocationPermissionChanged(isGranted = true))
        } else {
            permissionLauncher.launch(locationPermissions)
        }
    }

    TrackingContent(
        uiState = uiState,
        onAction = { action ->
            if (action == TrackingUiAction.StartTrackingClicked &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun TrackingContent(
    uiState: TrackingUiState,
    onAction: (TrackingUiAction) -> Unit,
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
                currentLocation = uiState.displayLocation ?: uiState.currentLocation,
                routePoints = uiState.routePoints,
                routeLineSegments = uiState.routeLineSegments,
                hasLocationPermission = uiState.hasLocationPermission,
                centerMapRequestId = uiState.centerMapRequestId,
                texts = uiState.texts,
                onRoutePointClick = { point ->
                    onAction(TrackingUiAction.RoutePointClicked(point))
                },
                modifier = Modifier.fillMaxSize()
            )

            TrackingControls(
                isTracking = uiState.isTracking,
                routePointCount = uiState.routePoints.size,
                hasLocationPermission = uiState.hasLocationPermission,
                texts = uiState.texts,
                onStartClick = { onAction(TrackingUiAction.StartTrackingClicked) },
                onStopClick = { onAction(TrackingUiAction.StopTrackingClicked) },
                onResetClick = { onAction(TrackingUiAction.ResetRouteClicked) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            )

            AddressPanel(
                address = uiState.selectedAddress,
                isLoading = uiState.isLoadingAddress,
                texts = uiState.texts,
                onCloseClick = { onAction(TrackingUiAction.CloseAddressClicked) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp).padding(bottom = 48.dp)
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
    routePoints: List<RoutePoint>,
    routeLineSegments: List<List<RoutePoint>>,
    hasLocationPermission: Boolean,
    centerMapRequestId: Int,
    texts: TrackingUiState.Texts,
    onRoutePointClick: (RoutePoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(41.0082, 28.9784)
    val mapLocation = currentLocation?.toLatLng() ?: defaultLocation
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapLocation, DEFAULT_MAP_ZOOM)
    }
    val hasMovedToInitialLocation = remember { mutableStateOf(false) }
    val lastHandledCenterMapRequestId = remember { mutableStateOf(0) }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null && !hasMovedToInitialLocation.value) {
            hasMovedToInitialLocation.value = true
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(currentLocation.toLatLng(), DEFAULT_MAP_ZOOM)
            )
        }
    }

    LaunchedEffect(centerMapRequestId, currentLocation) {
        if (
            currentLocation != null &&
            centerMapRequestId > lastHandledCenterMapRequestId.value
        ) {
            lastHandledCenterMapRequestId.value = centerMapRequestId
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(currentLocation.toLatLng(), DEFAULT_MAP_ZOOM)
            )
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(
                top = MAP_CONTROLS_TOP_PADDING,
                bottom = MAP_CONTROLS_BOTTOM_PADDING
            ),
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                compassEnabled = true,
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = true
            )
        ) {
            routeLineSegments.forEach { segment ->
                if (segment.size > 1) {
                    Polyline(
                        points = segment.map { point -> point.toLatLng() },
                        color = Color(0xFF4D9BFF),
                        width = 16f
                    )
                }
            }

            routePoints.forEachIndexed { index, point ->
                Marker(
                    state = MarkerState(position = point.toLatLng()),
                    title = "${texts.markerTitlePrefix} ${index + 1}",
                    onClick = {
                        onRoutePointClick(point)
                        false
                    }
                )
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
private fun TrackingContentPreview() {
    MartiLocationTrackerCaseTheme {
        TrackingContent(
            uiState = TrackingUiState(
                texts = defaultTrackingTexts(),
                currentLocation = UserLocation(
                    latitude = 41.0082,
                    longitude = 28.9784
                ),
                displayLocation = UserLocation(
                    latitude = 41.0082,
                    longitude = 28.9784
                ),
                routePoints = previewRoutePoints,
                routeLinePoints = previewRoutePoints,
                routeLineSegments = listOf(previewRoutePoints),
                hasLocationPermission = true,
                isTracking = true
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingLoadingPreview() {
    MartiLocationTrackerCaseTheme {
        TrackingContent(
            uiState = TrackingUiState(
                texts = defaultTrackingTexts(),
                isLoadingLocation = true
            ),
            onAction = {}
        )
    }
}

private val previewRoutePoints = listOf(
    RoutePoint(latitude = 40.820750, longitude = 29.305320, createdAt = 0L),
    RoutePoint(latitude = 40.822100, longitude = 29.304400, createdAt = 1L),
    RoutePoint(latitude = 40.823600, longitude = 29.303700, createdAt = 2L)
)
