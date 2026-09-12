package com.caglaakgul.martilocationtrackercase.domain.usecase

import android.location.Location
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import javax.inject.Inject

class AddRoutePointUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(location: UserLocation, segmentId: Long) {
        val lastMarkerPoint = routeRepository.getLastMarkerPoint(segmentId)
        val shouldMarkPoint = lastMarkerPoint == null ||
            lastMarkerPoint.distanceTo(location) >= MIN_DISTANCE_BETWEEN_MARKERS_METERS

        routeRepository.addPoint(
            location.toRoutePoint(
                isMarker = shouldMarkPoint,
                segmentId = segmentId
            )
        )
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

    private fun UserLocation.toRoutePoint(isMarker: Boolean, segmentId: Long): RoutePoint {
        return RoutePoint(
            latitude = latitude,
            longitude = longitude,
            createdAt = recordedAt,
            isMarker = isMarker,
            segmentId = segmentId
        )
    }

    private companion object {
        const val MIN_DISTANCE_BETWEEN_MARKERS_METERS = 100f
    }
}