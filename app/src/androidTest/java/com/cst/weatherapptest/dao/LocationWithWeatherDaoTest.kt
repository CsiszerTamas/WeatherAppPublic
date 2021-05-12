package com.cst.weatherapptest.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cst.data.local.LocationWithWeatherDao
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.weatherapptest.local.WeatherDatabase
import com.cst.weatherapptest.utils.TestUtils
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LocationWithWeatherDaoTest {
    private lateinit var locationWithWeatherDao: LocationWithWeatherDao
    private lateinit var weatherDatabase: WeatherDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        ).build()
        locationWithWeatherDao = weatherDatabase.locationWithWeatherDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        weatherDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertLocationWithWeatherTest() {
        // Create a LocationWithWeatherEntity object
        val locationWithWeatherEntityToInsert: LocationWithWeatherEntity =
            TestUtils.createLocationWithWeatherEntity()

        // Insert a LocationWithWeatherEntity object
        locationWithWeatherDao.insertLocationWithWeather(locationWithWeatherEntityToInsert)

        // Get the inserted LocationWithWeatherEntity object from the database
        val locationWithWeatherEntityListFromDb =
            locationWithWeatherDao.getAllLocationsWithWeather()

        Truth.assertThat(locationWithWeatherEntityListFromDb)
            .contains(locationWithWeatherEntityToInsert)
    }

    @Test
    @Throws(Exception::class)
    fun insertMultipleLocationsWithWeatherTest() {
        // Create a LocationWithWeatherEntity object list
        val locations: List<LocationWithWeatherEntity> =
            TestUtils.createLocationWithWeatherEntities()

        // Insert multiple LocationWithWeatherEntity objects at once
        locationWithWeatherDao.insertMultipleLocationsWithWeather(locations)

        // Get the inserted LocationWithWeatherEntity object list from the database
        val locationsWithWeatherFromDb = locationWithWeatherDao.getAllLocationsWithWeather()

        Truth.assertThat(locationsWithWeatherFromDb).containsExactlyElementsIn(locations)
    }

    @Test
    @Throws(Exception::class)
    fun updateLocationWithWeatherTest() {
        // Create a LocationWithWeatherEntity object - London
        val locationWithWeatherEntityToInsert: LocationWithWeatherEntity =
            TestUtils.createLocationWithWeatherEntity()
        locationWithWeatherDao.insertLocationWithWeather(locationWithWeatherEntityToInsert)

        // Modify and update the LocationWithWeatherEntity object in database
        locationWithWeatherEntityToInsert.name = "Cairo"
        locationWithWeatherDao.updateLocationWithWeather(locationWithWeatherEntityToInsert)

        // Get the update LocationWithWeatherEntity object from the database
        val locationWithWeatherEntityListFromDb =
            locationWithWeatherDao.getAllLocationsWithWeather()
        val updatedLocationFromDb = locationWithWeatherEntityListFromDb.first()

        Truth.assertThat(updatedLocationFromDb).isEqualTo(locationWithWeatherEntityToInsert)
    }

    @Test
    @Throws(Exception::class)
    fun deleteLocationWithWeatherTest() {
        // Create and insert a LocationWithWeatherEntity object list
        val locationParis: LocationWithWeatherEntity = TestUtils.getLocationParis()
        val locationLondon: LocationWithWeatherEntity = TestUtils.getLocationLondon()
        val locationBerlin: LocationWithWeatherEntity = TestUtils.getLocationBerlin()
        locationWithWeatherDao.insertMultipleLocationsWithWeather(
            listOf(
                locationParis,
                locationLondon,
                locationBerlin
            )
        )

        // Delete locationLondon from database
        locationWithWeatherDao.deleteLocationWithWeather(locationLondon.id)

        // Get the LocationWithWeatherEntity object list from the database
        val locationsWithWeatherEntityListFromDb =
            locationWithWeatherDao.getAllLocationsWithWeather()

        Truth.assertThat(locationsWithWeatherEntityListFromDb).doesNotContain(locationLondon)
    }

    @Test
    @Throws(Exception::class)
    fun getAllLocationsWithWeatherTest() {
        // Create and insert a LocationWithWeatherEntity object list
        val locationParis: LocationWithWeatherEntity = TestUtils.getLocationParis()
        val locationLondon: LocationWithWeatherEntity = TestUtils.getLocationLondon()
        val locationBerlin: LocationWithWeatherEntity = TestUtils.getLocationBerlin()
        val listOfLocationsToInsert = mutableListOf(locationParis, locationLondon, locationBerlin)
        locationWithWeatherDao.insertMultipleLocationsWithWeather(listOfLocationsToInsert)

        // Get the LocationWithWeatherEntity object list from the database
        val locationsWithWeatherEntityListFromDb =
            locationWithWeatherDao.getAllLocationsWithWeather()

        Truth.assertThat(locationsWithWeatherEntityListFromDb)
            .containsExactlyElementsIn(listOfLocationsToInsert)
    }

    @Test
    @Throws(Exception::class)
    fun isLocationInFavoritesTest() {
        // Create and  a LocationWithWeatherEntity object list
        val locationParis: LocationWithWeatherEntity = TestUtils.getLocationParis()
        val locationLondon: LocationWithWeatherEntity = TestUtils.getLocationLondon()
        val locationBerlin: LocationWithWeatherEntity = TestUtils.getLocationBerlin()

        // Insert locationParis and locationLondon into the database
        val listOfLocationsToInsert = mutableListOf(locationParis, locationLondon)
        locationWithWeatherDao.insertMultipleLocationsWithWeather(listOfLocationsToInsert)

        // Check if locationParis is in the database, and check if locationBerlin is not in the database
        val isParisInFavorites = locationWithWeatherDao.isLocationInFavorites(locationParis.id)
        val isBerlinInFavorites = locationWithWeatherDao.isLocationInFavorites(locationBerlin.id)

        Truth.assertThat(isParisInFavorites).isEqualTo(true)
        Truth.assertThat(isBerlinInFavorites).isEqualTo(false)
    }
}
