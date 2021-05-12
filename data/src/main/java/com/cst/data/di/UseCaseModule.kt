package com.cst.data.di

import android.location.Geocoder
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.currentlocation.local.GetCurrentLocationEntityFromLocalUseCase
import com.cst.domain.usecase.currentlocation.local.SaveCurrentLocationUseCase
import com.cst.domain.usecase.forecast.GetFiveDayForecastByLocationIdUseCase
import com.cst.domain.usecase.forecast.local.SaveFiveDayForecastForLocationUseCase
import com.cst.domain.usecase.locations.*
import com.cst.domain.usecase.locations.local.*
import com.cst.domain.usecase.sync.SyncWeatherDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideGetWeatherForLocationByNameUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherForLocationByNameUseCase {
        return GetWeatherForLocationByNameUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetWeatherForLocationByIdUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherForLocationByIdUseCase {
        return GetWeatherForLocationByIdUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetWeatherForLocationGroupUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherForLocationGroupUseCase {
        return GetWeatherForLocationGroupUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetLocationWeatherByLatLongUseCase(
        weatherRepository: WeatherRepository
    ): GetLocationWeatherByLatLongUseCase {
        return GetLocationWeatherByLatLongUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetFiveDayForecastByLocationIdUseCase(
        weatherRepository: WeatherRepository
    ): GetFiveDayForecastByLocationIdUseCase {
        return GetFiveDayForecastByLocationIdUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetLocationsFromGeocoderUseCase(
        geocoder: Geocoder
    ): GetLocationsFromGeocoderByLocationNameUseCase {
        return GetLocationsFromGeocoderByLocationNameUseCase(
            geocoder = geocoder
        )
    }

    @Singleton
    @Provides
    fun provideGetLocationsFromGeocoderByCoordinatesUseCase(
        geocoder: Geocoder
    ): GetLocationsFromGeocoderByCoordinatesUseCase {
        return GetLocationsFromGeocoderByCoordinatesUseCase(
            geocoder = geocoder
        )
    }

    @Singleton
    @Provides
    fun provideSaveLocationWithWeatherToFavoritesUseCase(
        weatherRepository: WeatherRepository
    ): SaveLocationWithWeatherToFavoritesUseCase {
        return SaveLocationWithWeatherToFavoritesUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideSaveMultipleLocationsWithWeatherToFavoritesUseCase(
        weatherRepository: WeatherRepository
    ): SaveMultipleLocationsWithWeatherToFavoritesUseCase {
        return SaveMultipleLocationsWithWeatherToFavoritesUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetLocationsWithWeatherFromFavoritesUseCase(
        weatherRepository: WeatherRepository
    ): GetLocationsWithWeatherFromFavoritesUseCase {
        return GetLocationsWithWeatherFromFavoritesUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideSaveFiveDayForecastForLocationUseCase(
        weatherRepository: WeatherRepository
    ): SaveFiveDayForecastForLocationUseCase {
        return SaveFiveDayForecastForLocationUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideSaveCurrentLocationUseCase(
        weatherRepository: WeatherRepository
    ): SaveCurrentLocationUseCase {
        return SaveCurrentLocationUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideIsLocationInFavoritesUseCase(
        weatherRepository: WeatherRepository
    ): GetCurrentLocationEntityFromLocalUseCase {
        return GetCurrentLocationEntityFromLocalUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideGetCurrentLocationEntityFromLocalUseCase(
        weatherRepository: WeatherRepository
    ): IsLocationInFavoritesUseCase {
        return IsLocationInFavoritesUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideRemoveLocationWithWeatherFromFavorites(
        weatherRepository: WeatherRepository
    ): RemoveLocationWithWeatherFromFavoritesUseCase {
        return RemoveLocationWithWeatherFromFavoritesUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Singleton
    @Provides
    fun provideSyncWeatherDataUseCase(
        getLocationsWithWeatherFromFavoritesUseCase: GetLocationsWithWeatherFromFavoritesUseCase,
        getWeatherForLocationGroupUseCase: GetWeatherForLocationGroupUseCase,
        saveMultipleLocationsWithWeatherToFavoritesUseCase: SaveMultipleLocationsWithWeatherToFavoritesUseCase,
        getFiveDayForecastByLocationIdUseCase: GetFiveDayForecastByLocationIdUseCase,
        saveFiveDayForecastForLocationUseCase: SaveFiveDayForecastForLocationUseCase
    ): SyncWeatherDataUseCase {
        return SyncWeatherDataUseCase(
            getLocationsWithWeatherFromFavoritesUseCase = getLocationsWithWeatherFromFavoritesUseCase,
            getWeatherForLocationGroupUseCase = getWeatherForLocationGroupUseCase,
            saveMultipleLocationsWithWeatherToFavoritesUseCase = saveMultipleLocationsWithWeatherToFavoritesUseCase,
            getFiveDayForecastByLocationIdUseCase = getFiveDayForecastByLocationIdUseCase,
            saveFiveDayForecastForLocationUseCase = saveFiveDayForecastForLocationUseCase
        )
    }
}