package com.caglaakgul.martilocationtrackercase.data.remote.api

import com.caglaakgul.martilocationtrackercase.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodingApi {
    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latLng: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "tr"
    ): GeocodingResponseDto
}