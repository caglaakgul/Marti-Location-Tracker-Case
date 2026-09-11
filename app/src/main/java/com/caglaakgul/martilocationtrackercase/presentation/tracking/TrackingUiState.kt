package com.caglaakgul.martilocationtrackercase.presentation.tracking

import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation

data class TrackingUiState(
    val currentLocation: UserLocation? = null,
    val isLoadingLocation: Boolean = false,
    val hasLocationPermission: Boolean = false
)