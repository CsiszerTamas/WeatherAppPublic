package com.cst.domain.models.forecast

import androidx.room.PrimaryKey

data class CityEntity(
    @PrimaryKey
    var id: Long? = null,
    var name: String? = null,
    var country: String? = null,
    var timezone: Long? = null,
    var sunrise: Long? = null,
    var sunset: Long? = null,
)