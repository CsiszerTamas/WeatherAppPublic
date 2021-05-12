package com.cst.data.models

import com.google.gson.annotations.SerializedName

data class MainInfoModel(
    @SerializedName("temp")
    var temp: Double? = null,

    @SerializedName("feels_like")
    var feelsLike: Double? = null,

    @SerializedName("temp_min")
    var tempMin: Double? = null,

    @SerializedName("temp_max")
    var tempMax: Double? = null,

    @SerializedName("pressure")
    var pressure: Int? = null,

    @SerializedName("humidity")
    var humidity: Int? = null
)