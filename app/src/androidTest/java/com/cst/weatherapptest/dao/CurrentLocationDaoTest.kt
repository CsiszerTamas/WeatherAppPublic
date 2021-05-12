package com.cst.weatherapptest.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cst.data.local.CurrentLocationDao
import com.cst.domain.models.CurrentLocationEntity
import com.cst.weatherapptest.local.WeatherDatabase
import com.cst.weatherapptest.utils.TestUtils
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CurrentLocationDaoTest {
    private lateinit var currentLocationDao: CurrentLocationDao
    private lateinit var weatherDatabase: WeatherDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        currentLocationDao = weatherDatabase.currentLocationDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        weatherDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertCurrentLocationEntityTest() {
        // Create a CurrentLocationEntity object
        val currentLocationEntity: CurrentLocationEntity =
            TestUtils.createCurrentLocationEntity(locationId = 671964)

        // Insert into the database
        currentLocationDao.insertCurrentLocationEntity(currentLocationEntity)

        // Read back from the database
        val currentLocationEntityFromDb = currentLocationDao.getCurrentLocationEntity()

        assertThat(currentLocationEntityFromDb).isEqualTo(currentLocationEntity)
    }

    @Test
    @Throws(Exception::class)
    fun getCurrentLocationEntityTest() {
        // Create and insert two CurrentLocationEntity objects
        val currentLocationEntity1: CurrentLocationEntity =
            TestUtils.createCurrentLocationEntity(locationId = 665004)
        val currentLocationEntity2: CurrentLocationEntity =
            TestUtils.createCurrentLocationEntity(locationId = 671964)

        // Insert into the database
        currentLocationDao.insertCurrentLocationEntity(currentLocationEntity1)
        currentLocationDao.insertCurrentLocationEntity(currentLocationEntity2)

        // Read back from the database
        val currentLocationEntityFromDb = currentLocationDao.getCurrentLocationEntity()

        assertThat(currentLocationEntityFromDb).isEqualTo(currentLocationEntity2)
    }

    @Test
    @Throws(Exception::class)
    fun updateCurrentLocationEntityTest() {
        val newLocationId = 671964L
        // Create CurrentLocationEntity object
        val currentLocationEntity: CurrentLocationEntity =
            TestUtils.createCurrentLocationEntity(locationId = 665004)

        currentLocationDao.insertCurrentLocationEntity(currentLocationEntity)

        // Modify locationId and update the entity in the database
        currentLocationEntity.locationId = newLocationId
        currentLocationDao.updateCurrentLocationEntity(currentLocationEntity)

        // Read back the updated entity from the database
        val currentLocationEntityFromDb = currentLocationDao.getCurrentLocationEntity()

        assertThat(currentLocationEntityFromDb.locationId).isEqualTo(newLocationId)
    }

    @Test
    @Throws(Exception::class)
    fun deleteCurrentLocationEntityTest() {
        // Create CurrentLocationEntity object
        val currentLocationEntity: CurrentLocationEntity =
            TestUtils.createCurrentLocationEntity(locationId = 665004)
        currentLocationDao.insertCurrentLocationEntity(currentLocationEntity)

        // Delete the CurrentLocationEntity object from database
        currentLocationDao.deleteCurrentLocationEntity(currentLocationEntity)

        // Try to get the object from the database
        // It should be null because we deleted it previously
        val currentLocationEntityFromDb = currentLocationDao.getCurrentLocationEntity()

        assertThat(currentLocationEntityFromDb).isNull()
    }
}
