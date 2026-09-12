package com.caglaakgul.martilocationtrackercase.domain.usecase

import android.location.Location
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import javax.inject.Inject

class AddRoutePointUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(location: UserLocation) {
        val lastPoint = routeRepository.getLastPoint()
        val shouldAddPoint = lastPoint == null ||
            lastPoint.distanceTo(location) >= MIN_DISTANCE_BETWEEN_MARKERS_METERS

        if (shouldAddPoint) {
            routeRepository.addPoint(location.toRoutePoint())
        }
    }

    private fun RoutePoint.distanceTo(location: UserLocation): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            latitude,
            longitude,
            location.latitude,
            location.longitude,
            results
        )
        return results.first()
    }

    private fun UserLocation.toRoutePoint(): RoutePoint {
        return RoutePoint(
            latitude = latitude,
            longitude = longitude,
            createdAt = recordedAt
        )
    }

    private companion object {
        const val MIN_DISTANCE_BETWEEN_MARKERS_METERS = 100f
    }
}