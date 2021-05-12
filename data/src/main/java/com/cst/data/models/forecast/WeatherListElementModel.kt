package com.cst.data.models.forecast

import com.cst.data.models.MainInfoModel
import com.cst.data.models.WeatherModel
import com.cst.data.models.forecast.listelement.*
import com.google.gson.annotations.SerializedName

data class WeatherListElementModel(
    @SerializedName("dt")
    var dt: Long,

    @SerializedName("main")
    var main: MainInfoModel? = null,

    @SerializedName("weather")
    var weather: List<WeatherModel>? = null,

    @SerializedName("clouds")
    var clouds: CloudsModel? = null,

    @SerializedName("wind")
    var wind: WindModel? = null,

    @SerializedName("visibility")
    var visibility: Int? = null,

    @SerializedName("pop")
    var pop: Double? = null,

    @SerializedName("rain")
    var rain: RainModel? = null,

    @SerializedName("snow")
    var snow: SnowModel? = null,

    @SerializedName("sys")
    var sys: SysModel? = null,

    @SerializedName("dt_txt")
    var timeOfDataForecasted: String? = null,
)