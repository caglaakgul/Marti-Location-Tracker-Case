package com.caglaakgul.martilocationtrackercase.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caglaakgul.martilocationtrackercase.BuildConfig
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.usecase.ClearLiveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.GetAddressUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.GetCurrentLocationUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveCurrentLocationUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveLiveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveTrackingStateUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ResetRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.SnapRouteToRoadUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.StartTrackingUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.StopTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val observeCurrentLocationUseCase: ObserveCurrentLocationUseCase,
    private val observeLiveRouteUseCase: ObserveLiveRouteUseCase,
    private val observeRouteUseCase: ObserveRouteUseCase,
    private val observeTrackingStateUseCase: ObserveTrackingStateUseCase,
    private val startTrackingUseCase: StartTrackingUseCase,
    private val stopTrackingUseCase: StopTrackingUseCase,
    private val clearLiveRouteUseCase: ClearLiveRouteUseCase,
    private val resetRouteUseCase: ResetRouteUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val snapRouteToRoadUseCase: SnapRouteToRoadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()
    private var snapRouteJob: Job? = null

    init {
        observeCurrentLocation()
        observeLiveRoute()
        observeRoute()
        observeTrackingState()
    }

    fun onAction(action: TrackingUiAction) {
        when (action) {
            is TrackingUiAction.LocationPermissionChanged -> onLocationPermissionResult(action.isGranted)
            TrackingUiAction.StartTrackingClicked -> startTracking()
            TrackingUiAction.StopTrackingClicked -> stopTracking()
            TrackingUiAction.ResetRouteClicked -> resetRoute()
            is TrackingUiAction.RoutePointClicked -> loadAddress(action.point)
        }
    }

    private fun onLocationPermissionResult(isGranted: Boolean) {
        _uiState.update { state ->
            state.copy(hasLocationPermission = isGranted)
        }

        if (isGranted) {
            loadCurrentLocation()
        }
    }

    private fun loadCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoadingLocation = true)
            }

            val currentLocation = getCurrentLocationUseCase()

            _uiState.update { state ->
                state.copy(
                    currentLocation = currentLocation,
                    displayLocation = currentLocation,
                    isLoadingLocation = false
                )
            }
        }
    }

    private fun observeCurrentLocation() {
        viewModelScope.launch {
            observeCurrentLocationUseCase().collect { currentLocation ->
                _uiState.update { state ->
                    state.copy(
                        currentLocation = currentLocation ?: state.currentLocation,
                        displayLocation = if (state.isTracking) {
                            state.displayLocation
                        } else {
                            currentLocation ?: state.displayLocation
                        }
                    )
                }
            }
        }
    }

    private fun observeLiveRoute() {
        viewModelScope.launch {
            observeLiveRouteUseCase().collect { liveRoute ->
                _uiState.update { state ->
                    state.copy(liveRouteLocations = liveRoute)
                }
                updateRouteLine(
                    routePoints = _uiState.value.routePoints,
                    liveRoute = liveRoute
                )
            }
        }
    }

    private fun observeRoute() {
        viewModelScope.launch {
            observeRouteUseCase().collect { routePoints ->
                _uiState.update { state ->
                    state.copy(routePoints = routePoints)
                }
                updateRouteLine(
                    routePoints = routePoints,
                    liveRoute = _uiState.value.liveRouteLocations
                )
            }
        }
    }

    private fun observeTrackingState() {
        viewModelScope.launch {
            observeTrackingStateUseCase().collect { isTracking ->
                _uiState.update { state ->
                    state.copy(isTracking = isTracking)
                }
            }
        }
    }

    private fun startTracking() {
        if (_uiState.value.hasLocationPermission) {
            startTrackingUseCase()
        }
    }

    private fun stopTracking() {
        stopTrackingUseCase()
    }

    private fun resetRoute() {
        viewModelScope.launch {
            resetRouteUseCase()
            clearLiveRouteUseCase()
            _uiState.update { state ->
                state.copy(
                    selectedAddress = null,
                    liveRouteLocations = emptyList(),
                    routeLinePoints = emptyList(),
                    displayLocation = state.currentLocation
                )
            }
        }
    }

    private fun loadAddress(point: RoutePoint) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoadingAddress = true, selectedAddress = null)
            }

            val address = runCatching {
                getAddressUseCase(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    apiKey = BuildConfig.MAPS_API_KEY
                )
            }.getOrDefault("")

            _uiState.update { state ->
                state.copy(
                    isLoadingAddress = false,
                    selectedAddress = address.ifBlank { "Adres bulunamadı" }
                )
            }
        }
    }

    private fun updateRouteLine(
        routePoints: List<RoutePoint>,
        liveRoute: List<UserLocation>
    ) {
        val rawLinePoints = buildRouteLinePoints(
            routePoints = routePoints,
            liveRoute = liveRoute
        )

        if (
            rawLinePoints.size < MIN_POINTS_TO_SNAP ||
            liveRoute.size < MIN_LIVE_POINTS_TO_SNAP ||
            BuildConfig.MAPS_API_KEY.isBlank()
        ) {
            _uiState.update { state ->
                state.copy(
                    routeLinePoints = rawLinePoints,
                    displayLocation = liveRoute.lastOrNull() ?: state.displayLocation
                )
            }
            return
        }

        snapRouteJob?.cancel()
        snapRouteJob = viewModelScope.launch {
            runCatching {
                snapRouteToRoadUseCase(rawLinePoints.takeLast(MAX_ROADS_API_POINTS), BuildConfig.MAPS_API_KEY)
            }.onSuccess { snappedPoints ->
                if (snappedPoints.isNotEmpty()) {
                    val snappedCurrentLocation = snappedPoints.last().toUserLocation(
                        fallbackRecordedAt = liveRoute.lastOrNull()?.recordedAt ?: System.currentTimeMillis()
                    )
                    _uiState.update { state ->
                        state.copy(
                            routeLinePoints = snappedPoints,
                            displayLocation = snappedCurrentLocation
                        )
                    }
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        routeLinePoints = rawLinePoints,
                        displayLocation = liveRoute.lastOrNull() ?: state.displayLocation
                    )
                }
            }
        }
    }

    private fun buildRouteLinePoints(
        routePoints: List<RoutePoint>,
        liveRoute: List<UserLocation>
    ): List<RoutePoint> {
        if (liveRoute.isEmpty()) return routePoints

        val liveRoutePoints = liveRoute.map { location ->
            RoutePoint(
                latitude = location.latitude,
                longitude = location.longitude,
                createdAt = location.recordedAt
            )
        }
        val firstLivePoint = liveRoutePoints.first()
        val routeBeforeLivePath = routePoints.filter { routePoint ->
            routePoint.createdAt < firstLivePoint.createdAt
        }

        return routeBeforeLivePath + liveRoutePoints
    }

    private fun RoutePoint.toUserLocation(fallbackRecordedAt: Long): UserLocation {
        return UserLocation(
            latitude = latitude,
            longitude = longitude,
            recordedAt = fallbackRecordedAt
        )
    }

    private companion object {
        const val MIN_POINTS_TO_SNAP = 2
        const val MIN_LIVE_POINTS_TO_SNAP = 2
        const val MAX_ROADS_API_POINTS = 100
    }
}