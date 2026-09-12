package com.caglaakgul.martilocationtrackercase.di

import android.content.Context
import androidx.room.Room
import com.caglaakgul.martilocationtrackercase.data.local.RouteDatabase
import com.caglaakgul.martilocationtrackercase.data.local.RoutePointDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideRouteDatabase(
        @ApplicationContext context: Context
    ): RouteDatabase {
        return Room.databaseBuilder(
            context,
            RouteDatabase::class.java,
            "route_database"
        ).build()
    }

    @Provides
    fun provideRoutePointDao(database: RouteDatabase): RoutePointDao {
        return database.routePointDao()
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}