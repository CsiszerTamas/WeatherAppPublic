package com.cst.domain.models.forecast

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_for_location")
data class FiveDayForecastEntity(
    // extracted from city to have an unique identifier for this entity
    @PrimaryKey
    var locationId: Long? = null,
    @Embedded
    var city: CityEntity? = null,
    var list: List<WeatherListElementEntity>? = null
)