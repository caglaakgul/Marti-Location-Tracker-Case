package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?

    fun observeLocationUpdates(): Flow<UserLocation>
}