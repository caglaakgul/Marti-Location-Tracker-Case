package com.caglaakgul.martilocationtrackercase.data.remote.api

import com.caglaakgul.martilocationtrackercase.data.remote.dto.SnapToRoadsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleRoadsApi {
    @GET("https://roads.googleapis.com/v1/snapToRoads")
    suspend fun snapToRoads(
        @Query("path", encoded = true) path: String,
        @Query("interpolate") interpolate: Boolean = true,
        @Query("key") apiKey: String
    ): SnapToRoadsResponseDto
}