package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint

interface RoadSnappingRepository {
    suspend fun snapToRoads(
        points: List<RoutePoint>,
        apiKey: String
    ): List<RoutePoint>
}