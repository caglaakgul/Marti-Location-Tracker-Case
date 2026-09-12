package com.caglaakgul.martilocationtrackercase.domain.usecase

import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import javax.inject.Inject

class ObserveRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    operator fun invoke() = routeRepository.observeRoute()
}