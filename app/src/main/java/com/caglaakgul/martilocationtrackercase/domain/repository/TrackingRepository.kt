package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    fun observeTrackingState(): Flow<Boolean>

    fun observeCurrentLocation(): Flow<UserLocation?>

    fun observeLiveRouteLocations(): Flow<List<UserLocation>>

    fun startTracking()

    fun stopTracking()

    fun clearLiveRoute()
}