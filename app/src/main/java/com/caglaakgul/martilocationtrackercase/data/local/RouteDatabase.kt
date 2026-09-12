package com.caglaakgul.martilocationtrackercase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RoutePointEntity::class],
    version = 1,
    exportSchema = false
)

abstract class RouteDatabase : RoomDatabase() {
    abstract fun routePointDao(): RoutePointDao
}