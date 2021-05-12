package com.cst.weatherapptest.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.listeners.SearchInputListener
import com.google.android.material.textview.MaterialTextView
import kotlin.math.roundToInt

private const val SPACE = " "
private const val SYMBOL_KELVIN = "\u212A"
private const val SYMBOL_CELSIUS = "\u2103"
private const val SYMBOL_FAHRENHEIT = "\u2109"

/**
 * This method can be used as an else part for .let() method. E.g : yourObject?.let{}.orElse{}
 */
inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

infix fun Double.of(unitOfMeasurement: UnitOfMeasurement): Double {
    return when (unitOfMeasurement) {
        UnitOfMeasurement.KELVIN -> this                         // By default OpenWeather API sends back temperature in Kelvin if we don't request it in other way
        UnitOfMeasurement.CELSIUS -> this - 273.15               // T(°C) = T(K) - 273.15
        UnitOfMeasurement.FAHRENHEIT -> (this * 1.8) - 459.67    // T(°F) = (T(K) × 9/5) - 459.67
    }
}

infix fun String.of(unitOfMeasurement: UnitOfMeasurement): String {
    return when (unitOfMeasurement) {
        UnitOfMeasurement.KELVIN -> this + SPACE + SYMBOL_KELVIN
        UnitOfMeasurement.CELSIUS -> this + SPACE + SYMBOL_CELSIUS
        UnitOfMeasurement.FAHRENHEIT -> this + SPACE + SYMBOL_FAHRENHEIT
    }
}

fun MaterialTextView.setTextUnitConverted(temp: Double, unitOfMeasurement: UnitOfMeasurement) {
    this.text = temp.of(unitOfMeasurement).roundToInt().toString().of(unitOfMeasurement)
}

inline fun EditText.setSearchInputListener(func: SearchInputListener.() -> Unit) {
    val listener = SearchInputListener()
    listener.func()
    addTextChangedListener(listener)
}
