package com.cst.data.models.forecast

import com.google.gson.annotations.SerializedName

data class FiveDayForecastModel(
    @SerializedName("list")
    var list: List<WeatherListElementModel>? = null,

    @SerializedName("city")
    var city: CityModel? = null
)