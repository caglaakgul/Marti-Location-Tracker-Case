package com.caglaakgul.martilocationtrackercase.di

import com.caglaakgul.martilocationtrackercase.data.remote.api.GoogleGeocodingApi
import com.caglaakgul.martilocationtrackercase.data.remote.api.GoogleRoadsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleGeocodingApi(retrofit: Retrofit): GoogleGeocodingApi {
        return retrofit.create(GoogleGeocodingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleRoadsApi(retrofit: Retrofit): GoogleRoadsApi {
        return retrofit.create(GoogleRoadsApi::class.java)
    }
}