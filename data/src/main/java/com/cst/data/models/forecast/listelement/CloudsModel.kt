package com.cst.data.models.forecast.listelement

import com.google.gson.annotations.SerializedName

data class CloudsModel(
    @SerializedName("all")
    var all: Int? = null,
)