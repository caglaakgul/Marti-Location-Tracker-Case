package com.caglaakgul.martilocationtrackercase.data.repository

import com.caglaakgul.martilocationtrackercase.data.remote.source.GeocodingRemoteDataSource
import com.caglaakgul.martilocationtrackercase.domain.repository.AddressRepository
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val remoteDataSource: GeocodingRemoteDataSource
) : AddressRepository {

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): String {
        return remoteDataSource
            .reverseGeocode(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            .results
            .firstOrNull()?.formattedAddress
            .orEmpty()
    }
}