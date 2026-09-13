package com.caglaakgul.martilocationtrackercase.data.remote.dto

data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList(),
    val status: String = ""
)