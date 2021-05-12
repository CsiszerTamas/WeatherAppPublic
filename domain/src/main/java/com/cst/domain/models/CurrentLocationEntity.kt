package com.cst.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_location")
data class CurrentLocationEntity(
    @PrimaryKey
    var id: Long? = 1,
    var locationId: Long? = null,
)
