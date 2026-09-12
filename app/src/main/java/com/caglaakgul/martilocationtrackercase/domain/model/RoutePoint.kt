package com.caglaakgul.martilocationtrackercase.domain.model

data class RoutePoint(
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long
)