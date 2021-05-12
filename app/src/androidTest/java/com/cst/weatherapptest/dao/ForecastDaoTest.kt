package com.cst.weatherapptest.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cst.data.local.ForecastDao
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.weatherapptest.local.WeatherDatabase
import com.cst.weatherapptest.utils.TestUtils
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ForecastDaoTest {
    private lateinit var forecastDao: ForecastDao
    private lateinit var weatherDatabase: WeatherDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        forecastDao = weatherDatabase.forecastDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        weatherDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun getFiveDayForecastEntityByLocationIdTest() {
        // Create a FiveDayForecastEntity object
        val fiveDayForecastEntity: FiveDayForecastEntity = TestUtils.createFiveDayForecastEntity()

        // Insert a FiveDayForecastEntity object
        forecastDao.insertFiveDayForecastEntity(fiveDayForecastEntity)

        val locationId = fiveDayForecastEntity.locationId

        // Try to get the FiveDayForecastEntity object from the database
        val fiveDayForecastEntityFromDb =
            forecastDao.getFiveDayForecastEntityByLocationId(locationId)

        Truth.assertThat(fiveDayForecastEntityFromDb).isEqualTo(fiveDayForecastEntity)
    }

    @Test
    @Throws(Exception::class)
    fun insertFiveDayForecastEntityTest() {
        // Create a FiveDayForecastEntity object
        val fiveDayForecastEntity: FiveDayForecastEntity = TestUtils.createFiveDayForecastEntity()

        // Insert a FiveDayForecastEntity object
        forecastDao.insertFiveDayForecastEntity(fiveDayForecastEntity)

        // Get the inserted FiveDayForecastEntity object from the database
        val fiveDayForecastEntityFromDb =
            forecastDao.getFiveDayForecastEntityByLocationId(fiveDayForecastEntity.locationId)

        Truth.assertThat(fiveDayForecastEntityFromDb).isEqualTo(fiveDayForecastEntity)
    }

    @Test
    @Throws(Exception::class)
    fun updateFiveDayForecastEntityTest() {
        val newCityName = "London"
        // Create a FiveDayForecastEntity object
        val fiveDayForecastEntity: FiveDayForecastEntity = TestUtils.createFiveDayForecastEntity()

        // Insert a FiveDayForecastEntity object
        forecastDao.insertFiveDayForecastEntity(fiveDayForecastEntity)

        // Modify and update the city name in the database
        fiveDayForecastEntity.city?.name = newCityName
        forecastDao.updateFiveDayForecastEntity(fiveDayForecastEntity)

        // Get the updated FiveDayForecastEntity object from the database
        val fiveDayForecastEntityFromDb =
            forecastDao.getFiveDayForecastEntityByLocationId(fiveDayForecastEntity.locationId)

        Truth.assertThat(fiveDayForecastEntityFromDb.city?.name).isEqualTo(newCityName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFiveDayForecastEntityTest() {
        // Create a FiveDayForecastEntity object
        val fiveDayForecastEntity: FiveDayForecastEntity = TestUtils.createFiveDayForecastEntity()

        // Insert a FiveDayForecastEntity object
        forecastDao.insertFiveDayForecastEntity(fiveDayForecastEntity)

        // Delete
        forecastDao.deleteFiveDayForecastEntity(fiveDayForecastEntity)

        // Try to get the FiveDayForecastEntity object from the database,
        // it should be null because it was previously deleted
        val fiveDayForecastEntityFromDb =
            forecastDao.getFiveDayForecastEntityByLocationId(fiveDayForecastEntity.locationId)

        Truth.assertThat(fiveDayForecastEntityFromDb).isNull()
    }
}
