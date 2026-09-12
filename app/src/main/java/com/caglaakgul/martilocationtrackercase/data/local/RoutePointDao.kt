package com.caglaakgul.martilocationtrackercase.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutePointDao {

    @Query("SELECT * FROM route_points ORDER BY createdAt ASC")
    fun observeRoute(): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM route_points ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastPoint(): RoutePointEntity?

    @Insert
    suspend fun insert(point: RoutePointEntity)

    @Query("DELETE FROM route_points")
    suspend fun clear()
}