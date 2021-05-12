package com.cst.domain.models.forecast.listelement

data class WindEntity(
    var speed: Double? = null,
    var deg: Int? = null,
    var gust: Double? = null,
)