package com.caglaakgul.martilocationtrackercase.domain.repository

import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?
}
