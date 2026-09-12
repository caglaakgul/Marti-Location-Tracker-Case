package com.caglaakgul.martilocationtrackercase.data.remote.source

import com.caglaakgul.martilocationtrackercase.data.remote.dto.GeocodingResponseDto

interface GeocodingRemoteDataSource {
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): GeocodingResponseDto
}