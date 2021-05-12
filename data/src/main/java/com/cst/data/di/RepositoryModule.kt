package com.cst.data.di

import com.cst.data.apiservice.WeatherApiService
import com.cst.data.local.CurrentLocationDao
import com.cst.data.local.ForecastDao
import com.cst.data.local.LocationWithWeatherDao
import com.cst.data.mapper.WeatherMapper
import com.cst.data.repositories.WeatherRepositoryImpl
import com.cst.domain.repositories.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesApplicationRepository(
        weatherApiService: WeatherApiService,
        weatherMapper: WeatherMapper,
        locationWithWeatherDao: LocationWithWeatherDao,
        forecastDao: ForecastDao,
        currentLocationDao: CurrentLocationDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherApiService = weatherApiService,
            weatherMapper = weatherMapper,
            locationWithWeatherDao = locationWithWeatherDao,
            forecastDao = forecastDao,
            currentLocationDao = currentLocationDao
        )
    }
}
