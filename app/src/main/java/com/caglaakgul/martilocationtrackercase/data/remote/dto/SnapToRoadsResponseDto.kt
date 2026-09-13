package com.caglaakgul.martilocationtrackercase.data.remote.dto

data class SnapToRoadsResponseDto(
    val snappedPoints: List<SnappedPointDto> = emptyList()
)