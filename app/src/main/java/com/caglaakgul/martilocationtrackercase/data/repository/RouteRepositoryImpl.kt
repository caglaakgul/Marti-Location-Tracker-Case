package com.caglaakgul.martilocationtrackercase.data.repository

import com.caglaakgul.martilocationtrackercase.data.local.RoutePointDao
import com.caglaakgul.martilocationtrackercase.data.local.RoutePointEntity
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routePointDao: RoutePointDao
) : RouteRepository {

    override fun observeRoute(): Flow<List<RoutePoint>> {
        return routePointDao.observeRoute().map { points ->
            points.map { point -> point.toDomain() }
        }
    }

    override suspend fun getLastMarkerPoint(segmentId: Long): RoutePoint? {
        return routePointDao.getLastMarkerPoint(segmentId)?.toDomain()
    }

    override suspend fun getFirstPoint(segmentId: Long): RoutePoint? {
        return routePointDao.getFirstPoint(segmentId)?.toDomain()
    }

    override suspend fun getLastPoint(segmentId: Long): RoutePoint? {
        return routePointDao.getLastPoint(segmentId)?.toDomain()
    }

    override suspend fun addPoint(point: RoutePoint) {
        routePointDao.insert(point.toEntity())
    }

    override suspend fun clearRoute() {
        routePointDao.clear()
    }

    private fun RoutePointEntity.toDomain(): RoutePoint {
        return RoutePoint(
            id = id,
            latitude = latitude,
            longitude = longitude,
            createdAt = createdAt,
            isMarker = isMarker,
            segmentId = segmentId
        )
    }

    private fun RoutePoint.toEntity(): RoutePointEntity {
        return RoutePointEntity(
            id = id,
            latitude = latitude,
            longitude = longitude,
            createdAt = createdAt,
            isMarker = isMarker,
            segmentId = segmentId
        )
    }
}