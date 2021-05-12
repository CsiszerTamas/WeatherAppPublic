package com.cst.domain.models.forecast

import com.cst.domain.models.MainInfoEntity
import com.cst.domain.models.WeatherEntity
import com.cst.domain.models.forecast.listelement.*

data class WeatherListElementEntity(
    var dt: Long,
    var main: MainInfoEntity? = null,
    var weather: List<WeatherEntity>? = null,
    var clouds: CloudsEntity? = null,
    var wind: WindEntity? = null,
    var visibility: Int? = null,
    var pop: Double? = null,
    var rain: RainEntity? = null,
    var snow: SnowEntity? = null,
    var sys: SysEntity? = null,
    var timeOfDataForecasted: String? = null,
)