package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun observeRoute(): Flow<List<RoutePoint>>

    suspend fun getLastMarkerPoint(segmentId: Long): RoutePoint?

    suspend fun addPoint(point: RoutePoint)

    suspend fun clearRoute()
}