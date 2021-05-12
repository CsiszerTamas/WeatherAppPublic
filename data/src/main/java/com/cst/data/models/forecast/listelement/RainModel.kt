package com.cst.data.models.forecast.listelement

import com.google.gson.annotations.SerializedName

data class RainModel(
    @SerializedName("3h")
    var threeHour: Double? = null
)
