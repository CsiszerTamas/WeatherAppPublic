package com.cst.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity(
    @PrimaryKey
    var id: Long? = null,
    var main: String? = null,
    var description: String? = null,
    var icon: String? = null
)