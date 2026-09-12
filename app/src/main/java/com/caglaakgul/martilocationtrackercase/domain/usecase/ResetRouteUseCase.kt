package com.caglaakgul.martilocationtrackercase.domain.usecase

import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import javax.inject.Inject

class ResetRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke() = routeRepository.clearRoute()
}