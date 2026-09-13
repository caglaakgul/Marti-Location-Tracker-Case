package com.caglaakgul.martilocationtrackercase.data.remote.source

import com.caglaakgul.martilocationtrackercase.data.remote.dto.SnapToRoadsResponseDto

interface RoadsRemoteDataSource {
    suspend fun snapToRoads(
        path: String,
        apiKey: String
    ): SnapToRoadsResponseDto
}