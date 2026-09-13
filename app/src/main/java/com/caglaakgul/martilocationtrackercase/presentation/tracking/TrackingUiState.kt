package com.caglaakgul.martilocationtrackercase.presentation.tracking

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation

data class TrackingUiState(
    val texts: Texts,
    val currentLocation: UserLocation? = null,
    val displayLocation: UserLocation? = null,
    val liveRouteLocations: List<UserLocation> = emptyList(),
    val savedRoutePoints: List<RoutePoint> = emptyList(),
    val routePoints: List<RoutePoint> = emptyList(),
    val routeLinePoints: List<RoutePoint> = emptyList(),
    val routeLineSegments: List<List<RoutePoint>> = emptyList(),
    val isLoadingLocation: Boolean = false,
    val isLoadingAddress: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val isTracking: Boolean = false,
    val centerMapRequestId: Int = 0,
    val selectedAddress: String? = null
) {
    data class Texts(
        val addressTitle: String,
        val trackingActive: String,
        val trackingStopped: String,
        val routePointCountSuffix: String,
        val startButton: String,
        val stopButton: String,
        val resetButton: String,
        val markerTitlePrefix: String,
        val addressNotFound: String,
        val noInternetConnection: String
    )
}
