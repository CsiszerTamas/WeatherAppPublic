package com.cst.weatherapptest.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cst.data.local.CurrentLocationDao
import com.cst.data.local.ForecastDao
import com.cst.data.local.LocationWithWeatherDao
import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.weatherapptest.util.storage.Converters

@Database(
    entities = [LocationWithWeatherEntity::class, FiveDayForecastEntity::class, CurrentLocationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val locationWithWeatherDao: LocationWithWeatherDao
    abstract val forecastDao: ForecastDao
    abstract val currentLocationDao: CurrentLocationDao

    companion object {

        private const val DATABASE_NAME = "weather_database"

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
