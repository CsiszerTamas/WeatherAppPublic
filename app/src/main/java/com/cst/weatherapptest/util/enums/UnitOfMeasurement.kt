package com.cst.weatherapptest.util.enums

enum class UnitOfMeasurement(val value: Int) {
    CELSIUS(0),
    FAHRENHEIT(1),
    KELVIN(2);

    companion object {

        var DEFAULT_UNIT_OF_MEASUREMENT_ID = CELSIUS.ordinal

        var UNIT_OF_MEASURE_HUMIDITY = "%"
        var UNIT_OF_MEASURE_PRESSURE = "hPa"
        var UNIT_OF_MEASURE_CLOUDINESS = "%"
        var UNIT_OF_MEASURE_WIND_SPEED = "meter/sec"
        var UNIT_OF_MEASURE_WIND_DIRECTION = "degrees"
        var UNIT_OF_MEASURE_WIND_GUST = "meter/sec"
        var UNIT_OF_MEASURE_VISIBILITY = "metres"
        var UNIT_OF_MEASURE_RAIN_VOLUME = "mm"
        var UNIT_OF_MEASURE_SNOW_VOLUME = ""

        private val map = values().associateBy(UnitOfMeasurement::ordinal)
        fun valueOf(type: Int) = map[type] ?: CELSIUS
    }
}
