package com.caglaakgul.martilocationtrackercase.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_points")
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long
)