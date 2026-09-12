package com.caglaakgul.martilocationtrackercase.data.remote.source

import com.caglaakgul.martilocationtrackercase.data.remote.api.GoogleGeocodingApi
import com.caglaakgul.martilocationtrackercase.data.remote.dto.GeocodingResponseDto
import javax.inject.Inject

class GoogleGeocodingRemoteDataSource @Inject constructor(
    private val api: GoogleGeocodingApi
) : GeocodingRemoteDataSource {

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): GeocodingResponseDto {
        return api.reverseGeocode(
            latLng = "$latitude,$longitude",
            apiKey = apiKey
        )
    }
}