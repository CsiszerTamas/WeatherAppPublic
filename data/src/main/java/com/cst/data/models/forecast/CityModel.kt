package com.cst.data.models.forecast

import com.google.gson.annotations.SerializedName

data class CityModel(
    @SerializedName("id")
    var id: Long? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("country")
    var country: String? = null,

    @SerializedName("timezone")
    var timezone: Long? = null,

    @SerializedName("sunrise")
    var sunrise: Long? = null,

    @SerializedName("sunset")
    var sunset: Long? = null
)