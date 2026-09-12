package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun observeRoute(): Flow<List<RoutePoint>>

    suspend fun getLastPoint(): RoutePoint?

    suspend fun addPoint(point: RoutePoint)

    suspend fun clearRoute()
}