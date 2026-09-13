package com.caglaakgul.martilocationtrackercase.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutePointDao {

    @Query("SELECT * FROM route_points ORDER BY segmentId ASC, createdAt ASC")
    fun observeRoute(): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM route_points WHERE isMarker = 1 AND segmentId = :segmentId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastMarkerPoint(segmentId: Long): RoutePointEntity?

    @Query("SELECT * FROM route_points WHERE segmentId = :segmentId ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirstPoint(segmentId: Long): RoutePointEntity?

    @Query("SELECT * FROM route_points WHERE segmentId = :segmentId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastPoint(segmentId: Long): RoutePointEntity?

    @Insert
    suspend fun insert(point: RoutePointEntity)

    @Query("DELETE FROM route_points")
    suspend fun clear()
}