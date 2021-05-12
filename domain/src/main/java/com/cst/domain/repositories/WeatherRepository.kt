package com.cst.domain.repositories

import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.WeatherForLocationGroupEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import io.reactivex.Single

interface WeatherRepository {

    // Communicating with Weather API

    fun getWeatherForLocationByName(locationName: String?): Single<LocationWithWeatherEntity>

    fun getWeatherForLocationById(locationId: Long?): Single<LocationWithWeatherEntity>

    fun getWeatherForLocationGroupByIds(locationIds: List<Long>?): Single<WeatherForLocationGroupEntity>

    fun getWeatherForLocationByLatitudeAndLongitude(
        latitude: Double?,
        longitude: Double?
    ): Single<LocationWithWeatherEntity>

    fun getFiveDayForecastForLocationById(locationId: Long?): Single<FiveDayForecastEntity>

    // Local - Room

    // LocationWithWeatherEntity
    fun insertLocationWithWeather(locationWithWeatherEntity: LocationWithWeatherEntity?): Single<Long>
    fun insertMultipleLocationsWithWeather(locationWithWeatherEntityList: List<LocationWithWeatherEntity>?): Single<List<Long>>
    fun removeLocationFromFavorites(locationId: Long?): Single<Int>
    fun isFavoriteLocation(locationId: Long?): Single<Boolean>
    fun getLocationsWithWeatherFromFavorites(): Single<List<LocationWithWeatherEntity>>

    // FiveDayForecastEntity
    fun insertFiveDayForecastForLocation(fiveDayForecastEntity: FiveDayForecastEntity?): Single<Long>
    fun getFiveDayForecastForLocationFromLocal(locationId: Long?): Single<FiveDayForecastEntity>

    // CurrentLocationEntity
    fun insertCurrentLocationEntity(currentLocationEntity: CurrentLocationEntity?): Single<Long>
    fun getCurrentLocationEntity(): Single<CurrentLocationEntity>
}