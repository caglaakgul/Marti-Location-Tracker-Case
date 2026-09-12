package com.caglaakgul.martilocationtrackercase.presentation.tracking

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint

sealed interface TrackingUiAction {
    data class LocationPermissionChanged(val isGranted: Boolean) : TrackingUiAction
    data object StartTrackingClicked : TrackingUiAction
    data object StopTrackingClicked : TrackingUiAction
    data object ResetRouteClicked : TrackingUiAction
    data class RoutePointClicked(val point: RoutePoint) : TrackingUiAction
}