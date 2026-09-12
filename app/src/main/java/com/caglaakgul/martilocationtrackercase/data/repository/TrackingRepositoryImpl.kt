package com.caglaakgul.martilocationtrackercase.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.caglaakgul.martilocationtrackercase.data.service.LocationTrackingService
import com.caglaakgul.martilocationtrackercase.data.tracking.TrackingStateStore
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.repository.TrackingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackingRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trackingStateStore: TrackingStateStore
) : TrackingRepository {

    override fun observeTrackingState(): Flow<Boolean> {
        return trackingStateStore.isTracking
    }

    override fun observeCurrentLocation(): Flow<UserLocation?> {
        return trackingStateStore.currentLocation
    }

    override fun observeLiveRouteLocations(): Flow<List<UserLocation>> {
        return trackingStateStore.liveRouteLocations
    }

    override fun startTracking() {
        val intent = LocationTrackingService.createStartIntent(context)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopTracking() {
        val intent = Intent(context, LocationTrackingService::class.java)
        context.stopService(intent)
    }

    override fun clearLiveRoute() {
        trackingStateStore.clearLiveRoute()
    }
}