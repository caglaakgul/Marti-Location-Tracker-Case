package com.caglaakgul.martilocationtrackercase.di

import com.caglaakgul.martilocationtrackercase.data.location.DefaultLocationRepository
import com.caglaakgul.martilocationtrackercase.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLocationRepository(
        repository: DefaultLocationRepository
    ): LocationRepository
}
