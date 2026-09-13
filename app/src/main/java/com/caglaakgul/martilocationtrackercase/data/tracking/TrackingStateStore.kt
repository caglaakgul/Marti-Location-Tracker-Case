package com.caglaakgul.martilocationtrackercase.data.tracking

import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingStateStore @Inject constructor() {

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _currentLocation = MutableStateFlow<UserLocation?>(null)
    val currentLocation: StateFlow<UserLocation?> = _currentLocation.asStateFlow()

    private val _liveRouteLocations = MutableStateFlow<List<UserLocation>>(emptyList())
    val liveRouteLocations: StateFlow<List<UserLocation>> = _liveRouteLocations.asStateFlow()

    fun setTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }

    fun setCurrentLocation(location: UserLocation) {
        _currentLocation.value = location
    }

    fun addLiveRouteLocation(location: UserLocation) {
        _liveRouteLocations.value += location
    }

    fun clearLiveRoute() {
        _liveRouteLocations.value = emptyList()
    }
}