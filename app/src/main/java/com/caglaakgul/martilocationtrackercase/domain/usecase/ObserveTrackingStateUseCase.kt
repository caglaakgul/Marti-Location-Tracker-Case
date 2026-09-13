package com.caglaakgul.martilocationtrackercase.domain.usecase

import com.caglaakgul.martilocationtrackercase.domain.repository.TrackingRepository
import javax.inject.Inject

class ObserveTrackingStateUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    operator fun invoke() = trackingRepository.observeTrackingState()
}