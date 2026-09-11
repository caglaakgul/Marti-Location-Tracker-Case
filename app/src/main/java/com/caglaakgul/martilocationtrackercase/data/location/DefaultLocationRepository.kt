package com.caglaakgul.martilocationtrackercase.data.location

import android.annotation.SuppressLint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultLocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): UserLocation? {
        return fusedLocationProviderClient.lastLocation.await()?.let { location ->
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }
}
