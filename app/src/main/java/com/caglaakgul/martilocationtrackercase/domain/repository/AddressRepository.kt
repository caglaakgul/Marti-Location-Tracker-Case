package com.caglaakgul.martilocationtrackercase.domain.repository

interface AddressRepository {
    suspend fun getAddress(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): String
}