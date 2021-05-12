package com.cst.data.local

import androidx.room.*
import com.cst.domain.models.LocationWithWeatherEntity

@Dao
interface LocationWithWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocationWithWeather(locationWithWeatherEntity: LocationWithWeatherEntity?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMultipleLocationsWithWeather(locationWithWeatherEntityList: List<LocationWithWeatherEntity>?): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocationWithWeather(locationWithWeatherEntity: LocationWithWeatherEntity?)

    @Query("DELETE FROM locations_with_weather WHERE id = :locationId")
    fun deleteLocationWithWeather(locationId: Long?): Int

    @Query("SELECT * FROM locations_with_weather")
    fun getAllLocationsWithWeather(): List<LocationWithWeatherEntity>

    @Query("SELECT EXISTS(SELECT * FROM locations_with_weather WHERE id LIKE :locationId LIMIT 1)")
    fun isLocationInFavorites(locationId: Long?): Boolean
}