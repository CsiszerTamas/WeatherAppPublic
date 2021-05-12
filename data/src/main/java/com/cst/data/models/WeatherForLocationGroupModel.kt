package com.cst.data.models

import com.google.gson.annotations.SerializedName

data class WeatherForLocationGroupModel(
    @SerializedName("list")
    var weatherForLocationList: List<LocationWithWeatherModel>? = null
)