package com.caglaakgul.martilocationtrackercase.presentation.tracking

import com.caglaakgul.martilocationtrackercase.MainDispatcherRule
import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.domain.repository.AddressRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.LocationRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.RoadSnappingRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.TrackingRepository
import com.caglaakgul.martilocationtrackercase.domain.usecase.ClearLiveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.GetAddressUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.GetCurrentLocationUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveCurrentLocationUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveLiveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ObserveTrackingStateUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.ResetRouteUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.SnapRouteToRoadUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.StartTrackingUseCase
import com.caglaakgul.martilocationtrackercase.domain.usecase.StopTrackingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TrackingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var routeRepository: RouteRepository
    private lateinit var trackingRepository: TrackingRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var roadSnappingRepository: RoadSnappingRepository
    private lateinit var addressRepository: AddressRepository

    private lateinit var routeFlow: MutableStateFlow<List<RoutePoint>>
    private lateinit var trackingStateFlow: MutableStateFlow<Boolean>
    private lateinit var currentLocationFlow: MutableStateFlow<UserLocation?>
    private lateinit var liveRouteFlow: MutableStateFlow<List<UserLocation>>

    @Before
    fun setUp() {
        routeFlow = MutableStateFlow(emptyList())
        trackingStateFlow = MutableStateFlow(false)
        currentLocationFlow = MutableStateFlow(null)
        liveRouteFlow = MutableStateFlow(emptyList())

        routeRepository = mockk(relaxed = true)
        trackingRepository = mockk(relaxed = true)
        locationRepository = mockk(relaxed = true)
        roadSnappingRepository = mockk(relaxed = true)
        addressRepository = mockk(relaxed = true)

        every { routeRepository.observeRoute() } returns routeFlow
        coEvery { routeRepository.clearRoute() } answers {
            routeFlow.value = emptyList()
        }

        every { trackingRepository.observeTrackingState() } returns trackingStateFlow
        every { trackingRepository.observeCurrentLocation() } returns currentLocationFlow
        every { trackingRepository.observeLiveRouteLocations() } returns liveRouteFlow
        every { trackingRepository.clearLiveRoute() } answers {
            liveRouteFlow.value = emptyList()
        }

        coEvery { locationRepository.getCurrentLocation() } returns null
        coEvery { roadSnappingRepository.snapToRoads(any(), any()) } answers {
            firstArg()
        }
        coEvery { addressRepository.getAddress(any(), any(), any()) } returns "Test Adresi Caddesi"
    }

    @Test
    fun `saved route shows only marker points as markers`() = runTest {
        routeFlow.value = twoSegmentRoute

        val viewModel = createViewModel()

        assertEquals(2, viewModel.uiState.value.routePoints.size)
    }

    @Test
    fun `saved route keeps line segments separated`() = runTest {
        routeFlow.value = twoSegmentRoute

        val viewModel = createViewModel()

        assertEquals(2, viewModel.uiState.value.routeLineSegments.size)
    }

    @Test
    fun `reset clears saved route repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(TrackingUiAction.ResetRouteClicked)

        coVerify { routeRepository.clearRoute() }
    }

    @Test
    fun `reset clears live route repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(TrackingUiAction.ResetRouteClicked)

        verify { trackingRepository.clearLiveRoute() }
    }

    @Test
    fun `reset clears route line segments`() = runTest {
        routeFlow.value = singleSegmentRoute
        val viewModel = createViewModel()

        viewModel.onAction(TrackingUiAction.ResetRouteClicked)

        assertTrue(viewModel.uiState.value.routeLineSegments.isEmpty())
    }

    private fun createViewModel(): TrackingViewModel {
        return TrackingViewModel(
            getCurrentLocationUseCase = GetCurrentLocationUseCase(locationRepository),
            observeCurrentLocationUseCase = ObserveCurrentLocationUseCase(trackingRepository),
            observeLiveRouteUseCase = ObserveLiveRouteUseCase(trackingRepository),
            observeRouteUseCase = ObserveRouteUseCase(routeRepository),
            observeTrackingStateUseCase = ObserveTrackingStateUseCase(trackingRepository),
            startTrackingUseCase = StartTrackingUseCase(trackingRepository),
            stopTrackingUseCase = StopTrackingUseCase(trackingRepository),
            clearLiveRouteUseCase = ClearLiveRouteUseCase(trackingRepository),
            resetRouteUseCase = ResetRouteUseCase(routeRepository),
            getAddressUseCase = GetAddressUseCase(addressRepository),
            snapRouteToRoadUseCase = SnapRouteToRoadUseCase(roadSnappingRepository)
        )
    }

    private val singleSegmentRoute = listOf(
        routePoint(id = 1, createdAt = 1, isMarker = true, segmentId = 100),
        routePoint(id = 2, createdAt = 2, isMarker = false, segmentId = 100)
    )

    private val twoSegmentRoute = listOf(
        routePoint(id = 1, createdAt = 1, isMarker = true, segmentId = 100),
        routePoint(id = 2, createdAt = 2, isMarker = false, segmentId = 100),
        routePoint(id = 3, createdAt = 3, isMarker = true, segmentId = 200),
        routePoint(id = 4, createdAt = 4, isMarker = false, segmentId = 200)
    )

    private fun routePoint(
        id: Long,
        createdAt: Long,
        isMarker: Boolean,
        segmentId: Long
    ): RoutePoint {
        return RoutePoint(
            id = id,
            latitude = 40.0 + createdAt,
            longitude = 29.0 + createdAt,
            createdAt = createdAt,
            isMarker = isMarker,
            segmentId = segmentId
        )
    }
}
