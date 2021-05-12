package com.cst.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations_with_weather")
data class LocationWithWeatherEntity(
    @PrimaryKey
    var id: Long? = null,
    var weatherResponseData: List<WeatherEntity>? = null,
    @Embedded
    var main: MainInfoEntity? = null,
    var dt: Long? = null,
    var timezone: Long? = null,
    var name: String? = null
)