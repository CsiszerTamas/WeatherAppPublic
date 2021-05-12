package com.cst.weatherapptest.util.storage

import androidx.room.TypeConverter
import com.cst.domain.models.WeatherEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.google.gson.Gson

/**
 * Converter for Room Database to convert complex objects into string from and convert them back from string to object
 */
class Converters {

    // Converter methods for - WeatherEntity
    @TypeConverter
    fun weatherEntityfromList(value: List<WeatherEntity>?) = Gson().toJson(value)

    @TypeConverter
    fun weatherEntityfromString(value: String) =
        Gson().fromJson(value, Array<WeatherEntity>::class.java).toList()

    // Converter methods for - WeatherListElementEntity
    @TypeConverter
    fun weatherListElementEntityfromList(value: List<WeatherListElementEntity>?) =
        Gson().toJson(value)

    @TypeConverter
    fun weatherListElementEntityfromString(value: String) =
        Gson().fromJson(value, Array<WeatherListElementEntity>::class.java).toList()
}
