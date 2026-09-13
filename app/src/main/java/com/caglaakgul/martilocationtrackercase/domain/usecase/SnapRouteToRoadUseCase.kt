package com.caglaakgul.martilocationtrackercase.domain.usecase

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.repository.RoadSnappingRepository
import javax.inject.Inject

class SnapRouteToRoadUseCase @Inject constructor(
    private val repository: RoadSnappingRepository
) {
    suspend operator fun invoke(
        points: List<RoutePoint>,
        apiKey: String
    ): List<RoutePoint> {
        return repository.snapToRoads(
            points = points,
            apiKey = apiKey
        )
    }
}