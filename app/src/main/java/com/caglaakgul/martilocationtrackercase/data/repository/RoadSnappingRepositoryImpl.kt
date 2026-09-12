package com.caglaakgul.martilocationtrackercase.data.repository

import com.caglaakgul.martilocationtrackercase.data.remote.source.RoadsRemoteDataSource
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.repository.RoadSnappingRepository
import javax.inject.Inject

class RoadSnappingRepositoryImpl @Inject constructor(
    private val remoteDataSource: RoadsRemoteDataSource
) : RoadSnappingRepository {

    override suspend fun snapToRoads(
        points: List<RoutePoint>,
        apiKey: String
    ): List<RoutePoint> {
        if (points.isEmpty()) return emptyList()

        val path = points.joinToString(separator = "|") { point ->
            "${point.latitude},${point.longitude}"
        }

        return remoteDataSource
            .snapToRoads(path = path, apiKey = apiKey)
            .snappedPoints
            .map { point ->
                RoutePoint(
                    latitude = point.location.latitude,
                    longitude = point.location.longitude,
                    createdAt = System.currentTimeMillis()
                )
            }
    }
}