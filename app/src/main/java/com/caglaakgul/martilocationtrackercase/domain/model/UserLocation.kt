package com.caglaakgul.martilocationtrackercase.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long = System.currentTimeMillis(),
    val accuracyMeters: Float? = null
)