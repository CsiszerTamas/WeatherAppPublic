package com.cst.weatherapptest.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object DateUtils {

    fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000L)
        val format = SimpleDateFormat("HH:mm", Locale.US)
        return format.format(date)
    }

    fun convertLongToHour(time: Long): String {
        val date = Date(time * 1000L)
        val format = SimpleDateFormat("HH:mm", Locale.US)
        return format.format(date)
    }

    fun convertLongToHour24hWithDay(time: Long): String {
        val date = Date(time * 1000L)
        val cal = Calendar.getInstance()
        cal.time = date
        val updatedDate = cal.time
        val format = SimpleDateFormat("MMM dd\nkk:mm", Locale.US)
        return format.format(updatedDate)
    }

    fun convertLongToHour24hIfLastItem(time: Long): String {
        val date = Date(time * 1000L)
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val updatedDate = cal.time
        val format = SimpleDateFormat("kk:mm", Locale.US)
        return format.format(updatedDate)
    }

    fun convertLongToHour24hIfFirstItem(time: Long): String {
        val date = Date(time * 1000L)
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val updatedDate = cal.time
        val format = SimpleDateFormat("MMM dd\nkk:mm", Locale.US)
        return format.format(updatedDate)
    }

    fun isToday(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(1)
        return currentDate.isBefore(tomorrowStartDate) || currentDate.isEqual(tomorrowStartDate)
    }

    fun isTomorrow(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(1)
        val dayAfterTomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(2)
        return (currentDate.isEqual(tomorrowStartDate) || currentDate.isAfter(tomorrowStartDate)) &&
                (currentDate.isBefore(dayAfterTomorrowStartDate) || currentDate.isEqual(
                    dayAfterTomorrowStartDate
                ))
    }

    fun isTomorrowPlus1Day(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(2)
        val dayAfterTomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(3)
        return (currentDate.isEqual(tomorrowStartDate) || currentDate.isAfter(tomorrowStartDate)) &&
                (currentDate.isBefore(dayAfterTomorrowStartDate) || currentDate.isEqual(
                    dayAfterTomorrowStartDate
                ))
    }

    fun isTomorrowPlus2Days(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(3)
        val dayAfterTomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(4)
        return (currentDate.isEqual(tomorrowStartDate) || currentDate.isAfter(tomorrowStartDate)) &&
                (currentDate.isBefore(dayAfterTomorrowStartDate) || currentDate.isEqual(
                    dayAfterTomorrowStartDate
                ))
    }

    fun isTomorrowPlus3Days(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(4)
        val dayAfterTomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(5)
        return (currentDate.isEqual(tomorrowStartDate) || currentDate.isAfter(tomorrowStartDate)) &&
                (currentDate.isBefore(dayAfterTomorrowStartDate) || currentDate.isEqual(
                    dayAfterTomorrowStartDate
                ))
    }

    fun isTomorrowPlus4Days(actualTime: Long): Boolean {
        val currentDate: LocalDateTime =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTime * 1000L),
                ZoneId.systemDefault()
            )
        val tomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(5)
        val dayAfterTomorrowStartDate = LocalDate.now().atStartOfDay().plusDays(6)
        return (currentDate.isEqual(tomorrowStartDate) || currentDate.isAfter(tomorrowStartDate)) &&
                (currentDate.isBefore(dayAfterTomorrowStartDate) || currentDate.isEqual(
                    dayAfterTomorrowStartDate
                ))
    }
}