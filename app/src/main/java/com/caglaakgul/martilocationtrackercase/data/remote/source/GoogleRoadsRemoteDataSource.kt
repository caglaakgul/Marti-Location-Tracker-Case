package com.caglaakgul.martilocationtrackercase.data.remote.source

import com.caglaakgul.martilocationtrackercase.data.remote.api.GoogleRoadsApi
import com.caglaakgul.martilocationtrackercase.data.remote.dto.SnapToRoadsResponseDto
import javax.inject.Inject

class GoogleRoadsRemoteDataSource @Inject constructor(
    private val api: GoogleRoadsApi
) : RoadsRemoteDataSource {

    override suspend fun snapToRoads(
        path: String,
        apiKey: String
    ): SnapToRoadsResponseDto {
        return api.snapToRoads(
            path = path,
            apiKey = apiKey
        )
    }
}