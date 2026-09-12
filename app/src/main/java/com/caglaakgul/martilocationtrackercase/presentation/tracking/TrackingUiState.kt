package com.caglaakgul.martilocationtrackercase.presentation.tracking

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation

data class TrackingUiState(
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
    val selectedAddress: String? = null
)