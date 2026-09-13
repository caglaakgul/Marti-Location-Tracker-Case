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
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.defaultTrackingTexts
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.toUserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
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

    private val _uiState = MutableStateFlow(
        TrackingUiState(texts = defaultTrackingTexts())
    )
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()
    private var snapRouteJob: Job? = null
    private var isResettingRoute = false

    init {
        observeCurrentLocation()
        observeLiveRoute()
        observeRoute()
        observeTrackingState()
    }

    fun onAction(action: TrackingUiAction) {
        when (action) {
            is TrackingUiAction.LocationPermissionChanged -> onLocationPermissionResult(action.isGranted)
            is TrackingUiAction.StartTrackingClicked -> startTracking()
            is TrackingUiAction.StopTrackingClicked -> stopTracking()
            is TrackingUiAction.ResetRouteClicked -> resetRoute()
            is TrackingUiAction.CloseAddressClicked -> closeAddress()
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
                if (!isResettingRoute) {
                    updateRouteLine(
                        routePoints = _uiState.value.savedRoutePoints,
                        liveRoute = liveRoute
                    )
                }
            }
        }
    }

    private fun observeRoute() {
        viewModelScope.launch {
            observeRouteUseCase().collect { savedRoutePoints ->
                _uiState.update { state ->
                    state.copy(
                        savedRoutePoints = savedRoutePoints,
                        routePoints = savedRoutePoints.filter { point -> point.isMarker }
                    )
                }
                if (!isResettingRoute) {
                    updateRouteLine(
                        routePoints = savedRoutePoints,
                        liveRoute = _uiState.value.liveRouteLocations
                    )
                }
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
            _uiState.update { state ->
                state.copy(centerMapRequestId = state.centerMapRequestId + 1)
            }
            startTrackingUseCase()
        }
    }

    private fun stopTracking() {
        stopTrackingUseCase()
    }

    private fun resetRoute() {
        viewModelScope.launch {
            isResettingRoute = true
            snapRouteJob?.cancel()
            snapRouteJob = null
            clearLiveRouteUseCase()
            resetRouteUseCase()
            _uiState.update { state ->
                state.copy(
                    selectedAddress = null,
                    liveRouteLocations = emptyList(),
                    savedRoutePoints = emptyList(),
                    routePoints = emptyList(),
                    routeLinePoints = emptyList(),
                    routeLineSegments = emptyList(),
                    displayLocation = state.currentLocation
                )
            }
            isResettingRoute = false
        }
    }

    private fun loadAddress(point: RoutePoint) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoadingAddress = true, selectedAddress = null)
            }

            val addressResult = runCatching {
                getAddressUseCase(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    apiKey = BuildConfig.MAPS_API_KEY
                )
            }

            _uiState.update { state ->
                state.copy(
                    isLoadingAddress = false,
                    selectedAddress = addressResult.fold(
                        onSuccess = { address -> address.ifBlank { state.texts.addressNotFound } },
                        onFailure = { error ->
                            if (error is IOException) {
                                state.texts.noInternetConnection
                            } else {
                                state.texts.addressNotFound
                            }
                        }
                    )
                )
            }
        }
    }

    private fun closeAddress() {
        _uiState.update { state ->
            state.copy(
                selectedAddress = null,
                isLoadingAddress = false
            )
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
        val rawLineSegments = buildRouteLineSegments(
            routePoints = routePoints,
            liveRoute = liveRoute
        )

        if (
            rawLinePoints.size < MIN_POINTS_TO_SNAP ||
            liveRoute.size == 1 ||
            BuildConfig.MAPS_API_KEY.isBlank()
        ) {
            _uiState.update { state ->
                state.copy(
                    routeLinePoints = rawLinePoints,
                    routeLineSegments = rawLineSegments,
                    displayLocation = rawLinePoints.lastOrNull()?.toUserLocation()
                        ?: state.displayLocation
                )
            }
            return
        }

        snapRouteJob?.cancel()
        snapRouteJob = viewModelScope.launch {
            runCatching {
                rawLineSegments.map { segment ->
                    if (segment.size < MIN_POINTS_TO_SNAP) {
                        segment
                    } else {
                        snapRouteToRoadUseCase(segment.takeLast(MAX_ROADS_API_POINTS), BuildConfig.MAPS_API_KEY)
                    }
                }
            }.onSuccess { snappedSegments ->
                val snappedLinePoints = snappedSegments.flatten()
                if (snappedLinePoints.isNotEmpty()) {
                    val snappedCurrentLocation = snappedLinePoints.last().toUserLocation(
                        fallbackRecordedAt = liveRoute.lastOrNull()?.recordedAt ?: System.currentTimeMillis()
                    )
                    _uiState.update { state ->
                        state.copy(
                            routeLinePoints = snappedLinePoints,
                            routeLineSegments = snappedSegments,
                            displayLocation = snappedCurrentLocation
                        )
                    }
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        routeLinePoints = rawLinePoints,
                        routeLineSegments = rawLineSegments,
                        displayLocation = rawLinePoints.lastOrNull()?.toUserLocation()
                            ?: state.displayLocation
                    )
                }
            }
        }
    }

    private fun buildRouteLinePoints(
        routePoints: List<RoutePoint>,
        liveRoute: List<UserLocation>
    ): List<RoutePoint> {
        return buildRouteLineSegments(routePoints, liveRoute).flatten()
    }

    private fun buildRouteLineSegments(
        routePoints: List<RoutePoint>,
        liveRoute: List<UserLocation>
    ): List<List<RoutePoint>> {
        if (liveRoute.isEmpty()) {
            return routePoints
                .groupBy { point -> point.segmentId }
                .values
                .filter { segment -> segment.isNotEmpty() }
        }

        val liveRoutePoints = liveRoute.map { location ->
            RoutePoint(
                latitude = location.latitude,
                longitude = location.longitude,
                createdAt = location.recordedAt,
                segmentId = location.recordedAt
            )
        }
        val firstLivePoint = liveRoutePoints.first()
        val routeBeforeLivePath = routePoints.filter { routePoint ->
            routePoint.createdAt < firstLivePoint.createdAt
        }

        return routeBeforeLivePath
            .groupBy { point -> point.segmentId }
            .values
            .filter { segment -> segment.isNotEmpty() } + listOf(liveRoutePoints)
    }

}