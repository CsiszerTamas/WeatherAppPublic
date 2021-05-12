package com.cst.weatherapptest.util

import android.content.Context
import com.cst.weatherapptest.BuildConfig
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun getTemperatureWithUnit(temperature: Double, unitOfMeasurement: UnitOfMeasurement): String {
        return temperature.of(unitOfMeasurement).roundToInt().toString().of(unitOfMeasurement)
    }

    /**
     * Helper method to get the  correct weather icon from the drawable directory
     * by the iconIdentifier provided by the API
     */
    fun getWeatherTypeIconByIdentifier(context: Context, iconIdentifier: String): Int {
        val iconPath = "ic_$iconIdentifier"
        return context.resources.getIdentifier(
            iconPath.toLowerCase(Locale.getDefault()),
            "drawable",
            BuildConfig.APPLICATION_ID
        )
    }

    fun getUnitOfMeasurementById(unitOfMeasurementId: Int): UnitOfMeasurement {
        return UnitOfMeasurement.valueOf(unitOfMeasurementId)
    }
}
