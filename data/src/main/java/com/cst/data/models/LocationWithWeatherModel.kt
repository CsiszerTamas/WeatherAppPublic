package com.cst.data.models

import com.google.gson.annotations.SerializedName

data class LocationWithWeatherModel(
    @SerializedName("id")
    var id: Long? = null,

    @SerializedName("weather")
    var weatherModelResponseData: List<WeatherModel>? = null,

    @SerializedName("main")
    var mainModel: MainInfoModel? = null,

    @SerializedName("dt")
    var dt: Long? = null,

    @SerializedName("timezone")
    var timezone: Long? = null,

    @SerializedName("name")
    var name: String? = null
)