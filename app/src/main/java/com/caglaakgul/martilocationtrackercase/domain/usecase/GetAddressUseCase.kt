package com.caglaakgul.martilocationtrackercase.domain.usecase

import com.caglaakgul.martilocationtrackercase.domain.repository.AddressRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): String {
        return repository.getAddress(
            latitude = latitude,
            longitude = longitude,
            apiKey = apiKey
        )
    }
}