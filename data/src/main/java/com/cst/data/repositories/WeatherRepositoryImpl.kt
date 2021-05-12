package com.cst.data.repositories

import com.cst.data.apiservice.WeatherApiService
import com.cst.data.local.CurrentLocationDao
import com.cst.data.local.ForecastDao
import com.cst.data.local.LocationWithWeatherDao
import com.cst.data.mapper.WeatherMapper
import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.WeatherForLocationGroupEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.repositories.WeatherRepository
import io.reactivex.Single

class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val weatherMapper: WeatherMapper,
    private val locationWithWeatherDao: LocationWithWeatherDao,
    private val forecastDao: ForecastDao,
    private val currentLocationDao: CurrentLocationDao
) : WeatherRepository {

    override fun getWeatherForLocationByName(locationName: String?): Single<LocationWithWeatherEntity> {
        return weatherApiService.getWeatherForLocationByName(locationName = locationName)
            .map { model -> weatherMapper.modelToEntity(model) }
    }

    override fun getWeatherForLocationById(locationId: Long?): Single<LocationWithWeatherEntity> {
        return weatherApiService.getWeatherForLocationById(locationId = locationId)
            .map { model -> weatherMapper.modelToEntity(model) }
    }

    override fun getWeatherForLocationGroupByIds(locationIds: List<Long>?): Single<WeatherForLocationGroupEntity> {
        val locationIdsAsString = locationIds?.joinToString(",")
        return weatherApiService.getWeatherForLocationGroupById(locationIds = locationIdsAsString)
            .map { model -> weatherMapper.modelToEntity(model) }
    }

    override fun getWeatherForLocationByLatitudeAndLongitude(
        latitude: Double?,
        longitude: Double?
    ): Single<LocationWithWeatherEntity> {
        return weatherApiService.getWeatherForLocationWithLatitudeAndLongitude(
            latitude = latitude,
            longitude = longitude
        )
            .map { model -> weatherMapper.modelToEntity(model) }
    }

    override fun getFiveDayForecastForLocationById(locationId: Long?): Single<FiveDayForecastEntity> {
        return weatherApiService.getFiveDayForecastForLocationById(locationId = locationId)
            .map { model ->
                weatherMapper.modelToEntity(model)
            }

    }

    override fun insertLocationWithWeather(locationWithWeatherEntity: LocationWithWeatherEntity?): Single<Long> {
        var insertedEntityId: Long? = null
        if (locationWithWeatherEntity != null) {
            insertedEntityId =
                locationWithWeatherDao.insertLocationWithWeather(locationWithWeatherEntity)
        }
        return Single.just(insertedEntityId)
    }

    override fun insertMultipleLocationsWithWeather(locationWithWeatherEntityList: List<LocationWithWeatherEntity>?): Single<List<Long>> {
        return Single.just(
            locationWithWeatherDao.insertMultipleLocationsWithWeather(
                locationWithWeatherEntityList
            )
        )
    }

    override fun removeLocationFromFavorites(locationId: Long?): Single<Int> {
        return Single.just(locationWithWeatherDao.deleteLocationWithWeather(locationId))
    }

    override fun isFavoriteLocation(locationId: Long?): Single<Boolean> {
        return Single.just(locationWithWeatherDao.isLocationInFavorites(locationId))
    }

    override fun getLocationsWithWeatherFromFavorites(): Single<List<LocationWithWeatherEntity>> {
        return Single.just(locationWithWeatherDao.getAllLocationsWithWeather())
    }

    override fun insertFiveDayForecastForLocation(fiveDayForecastEntity: FiveDayForecastEntity?): Single<Long> {
        var insertedEntityId: Long? = null
        if (fiveDayForecastEntity != null) {
            insertedEntityId =
                forecastDao.insertFiveDayForecastEntity(fiveDayForecastEntity)
        }
        return Single.just(insertedEntityId)
    }

    override fun getFiveDayForecastForLocationFromLocal(locationId: Long?): Single<FiveDayForecastEntity> {
        return Single.just(forecastDao.getFiveDayForecastEntityByLocationId(locationId))
    }

    override fun insertCurrentLocationEntity(currentLocationEntity: CurrentLocationEntity?): Single<Long> {
        var insertedEntityId: Long? = null
        if (currentLocationEntity != null) {
            insertedEntityId =
                currentLocationDao.insertCurrentLocationEntity(currentLocationEntity)
        }
        return Single.just(insertedEntityId)
    }

    override fun getCurrentLocationEntity(): Single<CurrentLocationEntity> {
        return Single.just(currentLocationDao.getCurrentLocationEntity())
    }
}
