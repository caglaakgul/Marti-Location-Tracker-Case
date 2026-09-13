package com.caglaakgul.martilocationtrackercase.di

import com.caglaakgul.martilocationtrackercase.data.remote.source.GeocodingRemoteDataSource
import com.caglaakgul.martilocationtrackercase.data.remote.source.GoogleGeocodingRemoteDataSource
import com.caglaakgul.martilocationtrackercase.data.remote.source.GoogleRoadsRemoteDataSource
import com.caglaakgul.martilocationtrackercase.data.remote.source.RoadsRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    abstract fun bindGeocodingRemoteDataSource(
        dataSource: GoogleGeocodingRemoteDataSource
    ): GeocodingRemoteDataSource

    @Binds
    abstract fun bindRoadsRemoteDataSource(
        dataSource: GoogleRoadsRemoteDataSource
    ): RoadsRemoteDataSource
}