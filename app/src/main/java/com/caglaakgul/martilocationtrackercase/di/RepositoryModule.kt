package com.caglaakgul.martilocationtrackercase.di

import com.caglaakgul.martilocationtrackercase.data.repository.AddressRepositoryImpl
import com.caglaakgul.martilocationtrackercase.data.repository.LocationRepositoryImpl
import com.caglaakgul.martilocationtrackercase.data.repository.RoadSnappingRepositoryImpl
import com.caglaakgul.martilocationtrackercase.data.repository.RouteRepositoryImpl
import com.caglaakgul.martilocationtrackercase.data.repository.TrackingRepositoryImpl
import com.caglaakgul.martilocationtrackercase.domain.repository.AddressRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.LocationRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.RoadSnappingRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.RouteRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.TrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLocationRepository(
        repository: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    abstract fun bindRouteRepository(
        repository: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    abstract fun bindTrackingRepository(
        repository: TrackingRepositoryImpl
    ): TrackingRepository

    @Binds
    abstract fun bindAddressRepository(
        repository: AddressRepositoryImpl
    ): AddressRepository

    @Binds
    abstract fun bindRoadSnappingRepository(
        repository: RoadSnappingRepositoryImpl
    ): RoadSnappingRepository
}
