package com.cst.weatherapptest.util.enums

enum class DataSync(val value: Long) {
    NEVER(0),
    EVERY_HOUR(3600000L),
    EVERY_6HOUR(21600000L),
    EVERY_12HOUR(43200000L),
    EVERY_24HOUR(86400000L);

    companion object {
        private val map = values().associateBy(DataSync::ordinal)
        fun valueOf(type: Int) = map[type] ?: NEVER
    }
}
