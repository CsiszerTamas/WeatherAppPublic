package com.cst.data.models.forecast.listelement

import com.google.gson.annotations.SerializedName

data class SysModel(
    @SerializedName("pod")
    var partOfTheDay: String? = null
)
