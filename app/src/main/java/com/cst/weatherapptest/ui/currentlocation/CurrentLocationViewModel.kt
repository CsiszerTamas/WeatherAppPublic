package com.cst.weatherapptest.ui.currentlocation

import android.content.Context
import android.location.Address
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.domain.models.items.WeatherAttributeItem
import com.cst.domain.usecase.currentlocation.local.GetCurrentLocationEntityFromLocalUseCase
import com.cst.domain.usecase.currentlocation.local.SaveCurrentLocationUseCase
import com.cst.domain.usecase.forecast.GetFiveDayForecastByLocationIdUseCase
import com.cst.domain.usecase.forecast.local.GetFiveDayForecastByLocationIdFromLocalUseCase
import com.cst.domain.usecase.forecast.local.SaveFiveDayForecastForLocationUseCase
import com.cst.domain.usecase.locations.GetLocationsFromGeocoderByCoordinatesUseCase
import com.cst.domain.usecase.locations.GetWeatherForLocationByIdUseCase
import com.cst.domain.usecase.locations.GetWeatherForLocationByNameUseCase
import com.cst.domain.usecase.locations.local.IsLocationInFavoritesUseCase
import com.cst.domain.usecase.locations.local.RemoveLocationWithWeatherFromFavoritesUseCase
import com.cst.domain.usecase.locations.local.SaveLocationWithWeatherToFavoritesUseCase
import com.cst.weatherapptest.R
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.connection.ConnectionVerificationService
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import com.cst.weatherapptest.util.orElse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentLocationViewModel @Inject constructor(
    private val connectionVerificationService: ConnectionVerificationService,
    private val getLocationsFromGeocoderByCoordinatesUseCase: GetLocationsFromGeocoderByCoordinatesUseCase,
    private val getWeatherForLocationByNameUseCase: GetWeatherForLocationByNameUseCase,
    private val getFiveDayForecastByLocationIdUseCase: GetFiveDayForecastByLocationIdUseCase,
    private val saveCurrentLocationUseCase: SaveCurrentLocationUseCase,
    private val getCurrentLocationEntityFromLocalUseCase: GetCurrentLocationEntityFromLocalUseCase,
    private val getFiveDayForecastByLocationIdFromLocalUseCase: GetFiveDayForecastByLocationIdFromLocalUseCase,
    private val saveFiveDayForecastForLocationUseCase: SaveFiveDayForecastForLocationUseCase,
    private val isLocationInFavoritesUseCase: IsLocationInFavoritesUseCase,
    private val removeLocationWithWeatherFromFavoritesUseCase: RemoveLocationWithWeatherFromFavoritesUseCase,
    private val getWeatherForLocationByIdUseCase: GetWeatherForLocationByIdUseCase,
    private val saveLocationWithWeatherToFavoritesUseCase: SaveLocationWithWeatherToFavoritesUseCase
) : ViewModel() {

    companion object {
        private const val AFFECTED_ROW_NUMBER_DELETE = 1
    }

    var locationId: Long? = null
    var locationWithWeatherEntity: LocationWithWeatherEntity? = null
    var fiveDayForecastEntity: FiveDayForecastEntity? = null

    var todayForecastWeatherList: List<WeatherListElementEntity>? = null
    var tomorrowForecastWeatherList: List<WeatherListElementEntity>? = null
    var tomorrowPlus1DayForecastList: List<WeatherListElementEntity>? = null
    var tomorrowPlus2DayForecastWeatherList: List<WeatherListElementEntity>? = null
    var tomorrowPlus3DayForecastWeatherList: List<WeatherListElementEntity>? = null
    var tomorrowPlus4DayForecastWeatherList: List<WeatherListElementEntity>? = null

    var forecastDailyList: List<WeatherListElementEntity>? = null

    val isLoading = MutableLiveData<Boolean>()
    val noForecastDataCachedForThisLocation = MutableLiveData<Boolean>()

    val addressesByCoordinates = MutableLiveData<MutableList<Address>>()
    val locationData = MutableLiveData<LocationWithWeatherEntity>()

    val forecastData = MutableLiveData<FiveDayForecastEntity>()
    val updatedForecastData = MutableLiveData<FiveDayForecastEntity>()

    val locationIsInFavorites = MutableLiveData<Boolean>()
    val locationAddedToFavorites = MutableLiveData<Boolean>()
    val locationRemovedFromFavorites = MutableLiveData<Boolean>()

    fun getLocationWeatherByName(locationName: String) {

        getWeatherForLocationByNameUseCase.execute(
            GetWeatherForLocationByNameUseCase.Params(locationName),
            onSuccess = {
                locationData.value = it
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getLocationWeatherForecastByLocationId(locationId: Long) {
        isLoading.value = true
        getFiveDayForecastByLocationIdUseCase.execute(
            GetFiveDayForecastByLocationIdUseCase.Params(locationId),
            onSuccess = {
                isLoading.value = false
                forecastData.value = it

                // Save the forecast data for the current location into the db
                saveFiveDayForecastForLocationInDatabase(it)
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getLocationAddressesFromGeocoderByCoordinates(
        latitude: Double,
        longitude: Double
    ) {
        getLocationsFromGeocoderByCoordinatesUseCase.execute(
            GetLocationsFromGeocoderByCoordinatesUseCase.Params(latitude, longitude),
            onSuccess = {
                addressesByCoordinates.value = it
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun updateLocationWeatherForecastByLocationId(locationId: Long) {
        isLoading.value = true
        getFiveDayForecastByLocationIdUseCase.execute(
            GetFiveDayForecastByLocationIdUseCase.Params(locationId),
            onSuccess = {
                isLoading.value = false
                updatedForecastData.value = it
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun saveCurrentLocationEntity(locationId: Long) {
        val currentLocationEntity = CurrentLocationEntity(locationId = locationId)
        saveCurrentLocationUseCase.execute(
            SaveCurrentLocationUseCase.Params(currentLocationEntity),
            onSuccess = {
                Log.d("DEBUG", "currentLocationEntity inserted with id: $it")
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    private fun saveFiveDayForecastForLocationInDatabase(fiveDayForecastEntity: FiveDayForecastEntity) {
        saveFiveDayForecastForLocationUseCase.execute(
            SaveFiveDayForecastForLocationUseCase.Params(
                fiveDayForecastEntity = fiveDayForecastEntity
            ),
            onSuccess = {
                Log.d("DEBUG_", "saveFiveDayForecastForLocation(), inserted with id: $it")
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getCurrentLocationEntityFromLocal() {
        getCurrentLocationEntityFromLocalUseCase.execute(
            onSuccess = {
                Log.d("DEBUG", "currentLocationEntity.locationId: ${it.locationId}")
                it.locationId?.let { id ->
                    isLoading.value = true
                    locationId = id
                    getFiveDayForecastByLocationIdFromLocalUseCase.execute(
                        GetFiveDayForecastByLocationIdFromLocalUseCase.Params(locationId = id),
                        onSuccess = { fiveDaysForecastForLocation ->
                            isLoading.value = false
                            forecastData.value = fiveDaysForecastForLocation
                        },
                        onError = {
                            isLoading.value = false
                            noForecastDataCachedForThisLocation.value = true
                            it.printStackTrace()
                        }
                    )
                }.orElse {
                    Log.d("DEBUG", "CurrentLocation - locationId is null")
                }
            },
            onError = {
                isLoading.value = false
                noForecastDataCachedForThisLocation.value = true
                it.printStackTrace()
            }
        )
    }

    fun getIfLocationInFavorites(locationId: Long) {
        isLocationInFavoritesUseCase.execute(
            IsLocationInFavoritesUseCase.Params(
                locationId = locationId
            ),
            onSuccess = {
                locationIsInFavorites.value = it
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    fun removeLocationWeatherForecastFromFavorites(locationId: Long) {
        isLoading.value = true
        removeLocationWithWeatherFromFavoritesUseCase.execute(
            RemoveLocationWithWeatherFromFavoritesUseCase.Params(locationId),
            onSuccess = { affectedRowNumberOnDeleteFromDb ->
                isLoading.value = false
                if (affectedRowNumberOnDeleteFromDb == AFFECTED_ROW_NUMBER_DELETE) {
                    locationIsInFavorites.value = false
                    locationRemovedFromFavorites.value = true
                }
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getLocationWithWeatherAndSaveLocal(
        locationId: Long,
        fiveDayForecastEntity: FiveDayForecastEntity
    ) {
        getWeatherForLocationByIdUseCase.execute(
            GetWeatherForLocationByIdUseCase.Params(locationId),
            onSuccess = { locationWithWeatherEntity ->
                // Save locally locationWithWeatherEntity and save the previously fetched forecast data for it
                saveLocationWithWeatherAsFavoriteInDatabase(locationWithWeatherEntity)
                saveFiveDayForecastForLocationInDatabase(fiveDayForecastEntity)
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    private fun saveLocationWithWeatherAsFavoriteInDatabase(locationWithWeatherEntity: LocationWithWeatherEntity) {
        saveLocationWithWeatherToFavoritesUseCase.execute(
            SaveLocationWithWeatherToFavoritesUseCase.Params(
                locationWithWeatherEntity = locationWithWeatherEntity
            ),
            onSuccess = {
                locationAddedToFavorites.value = true
                locationIsInFavorites.value = true
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    /**
     * Method for create a weatherAttributeList from the weatherListElementEntity.
     * The purpose of this list is to show the weather details in a list.
     * I've choose this list implementation because some of attributes from the api can be null,
     * in that case they are not added to the list.
     */
    fun createWeatherAttributeList(
        context: Context,
        weatherListElementEntity: WeatherListElementEntity,
        selectedUnitOfMeasurement: UnitOfMeasurement
    ): MutableList<WeatherAttributeItem> {
        val weatherAttributeList = mutableListOf<WeatherAttributeItem>()

        // Humidity
        weatherListElementEntity.main?.humidity?.let { humidity ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_humidity,
                    context.resources.getString(R.string.weather_attribute_title_humidity),
                    "$humidity ${UnitOfMeasurement.UNIT_OF_MEASURE_HUMIDITY}"
                )
            )
        }

        // Feels like
        weatherListElementEntity.main?.feelsLike?.let { feelsLike ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_temperature_feels_like_2,
                    context.resources.getString(R.string.weather_attribute_title_feels_like),
                    Utils.getTemperatureWithUnit(feelsLike, selectedUnitOfMeasurement)
                )
            )

        }
        // Minimum temperature
        weatherListElementEntity.main?.tempMin?.let { tempMin ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_min_temperature,
                    context.resources.getString(R.string.weather_attribute_title_min_temperature),
                    Utils.getTemperatureWithUnit(tempMin, selectedUnitOfMeasurement)
                )
            )
        }

        // Maximum temperature
        weatherListElementEntity.main?.tempMax?.let { tempMax ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_max_temperature,
                    context.resources.getString(R.string.weather_attribute_title_max_temperature),
                    Utils.getTemperatureWithUnit(tempMax, selectedUnitOfMeasurement)
                )
            )
        }

        // Pressure
        weatherListElementEntity.main?.pressure?.let { pressure ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_pressure,
                    context.resources.getString(R.string.weather_attribute_title_pressure),
                    "$pressure ${UnitOfMeasurement.UNIT_OF_MEASURE_PRESSURE}"
                )
            )
        }

        // Cloudiness
        weatherListElementEntity.clouds?.all?.let { cloudiness ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_cloud,
                    context.resources.getString(R.string.weather_attribute_title_cloudiness),
                    "$cloudiness ${UnitOfMeasurement.UNIT_OF_MEASURE_CLOUDINESS}"
                )
            )
        }

        // Wind speed
        weatherListElementEntity.wind?.speed?.let { windSpeed ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_wind_speed,
                    context.resources.getString(R.string.weather_attribute_title_wind_speed),
                    "$windSpeed ${UnitOfMeasurement.UNIT_OF_MEASURE_WIND_SPEED}"
                )
            )
        }

        // Wind degree
        weatherListElementEntity.wind?.deg?.let { windDegree ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_wind_degree,
                    context.resources.getString(R.string.weather_attribute_title_wind_direction),
                    "$windDegree ${UnitOfMeasurement.UNIT_OF_MEASURE_WIND_DIRECTION}"
                )
            )
        }

        // Wind gust
        weatherListElementEntity.wind?.gust?.let { windGust ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_wind_gust,
                    context.resources.getString(R.string.weather_attribute_title_wind_gust),
                    "$windGust ${UnitOfMeasurement.UNIT_OF_MEASURE_WIND_GUST}"
                )
            )
        }

        // Visibility
        weatherListElementEntity.visibility?.let { visibility ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_visibility,
                    context.resources.getString(R.string.weather_attribute_title_visibility),
                    "$visibility ${UnitOfMeasurement.UNIT_OF_MEASURE_VISIBILITY}"
                )
            )
        }

        // Rain volume
        weatherListElementEntity.rain?.threeHour?.let { rainVolumeInLastThreeHour ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_rain,
                    context.resources.getString(R.string.weather_attribute_title_rain_volume),
                    "$rainVolumeInLastThreeHour ${UnitOfMeasurement.UNIT_OF_MEASURE_RAIN_VOLUME}"
                )
            )
        }

        // Snow volume
        weatherListElementEntity.snow?.threeHour?.let { snowVolumeInLastThreeHour ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_snowflake,
                    context.resources.getString(R.string.weather_attribute_title_snow_volume),
                    "$snowVolumeInLastThreeHour ${UnitOfMeasurement.UNIT_OF_MEASURE_SNOW_VOLUME}"
                )
            )
        }

        return weatherAttributeList
    }

    /**
     * This method creates the necessary list of data by the filter category (days).
     * The logic here is trying to get the weather for the current day from the half of the available 3 hourly forecast.
     * For example if we have data from morning to evening then we want a hourly forecast from somewhere in the middle, like 12:00.
     * In case of Today, it can happen that we have less data, but in this case also a middle hourly forecast is selected by the actual size of the list.
     */
    fun createForecastedWeatherListByDays(): List<WeatherListElementEntity> {

        val finalList = mutableListOf<WeatherListElementEntity>()

        val todayForecastedWeatherListSize = todayForecastWeatherList?.size?.div(2)
        val weatherForToday = todayForecastedWeatherListSize?.let {
            todayForecastWeatherList?.getOrNull(
                it
            )
        }
        weatherForToday?.let { finalList.add(it) }

        val tomorrowForecastedWeatherListSize = tomorrowForecastWeatherList?.size?.div(2)
        val weatherForTomorrow = tomorrowForecastedWeatherListSize?.let {
            tomorrowForecastWeatherList?.getOrNull(
                it
            )
        }
        weatherForTomorrow?.let { finalList.add(it) }

        val tomorrowPlus1DayForecastedWeatherListSize =
            tomorrowPlus1DayForecastList?.size?.div(2)
        val weatherForTomorrowPlus1Day = tomorrowPlus1DayForecastedWeatherListSize?.let {
            tomorrowPlus1DayForecastList?.getOrNull(
                it
            )
        }
        weatherForTomorrowPlus1Day?.let { finalList.add(it) }

        val tomorrowPlus2DayForecastedWeatherListSize =
            tomorrowPlus2DayForecastWeatherList?.size?.div(2)
        val weatherForTomorrowPlus2Day = tomorrowPlus2DayForecastedWeatherListSize?.let {
            tomorrowPlus2DayForecastWeatherList?.getOrNull(
                it
            )
        }
        weatherForTomorrowPlus2Day?.let { finalList.add(it) }

        val tomorrowPlus3DayForecastedWeatherListSize =
            tomorrowPlus3DayForecastWeatherList?.size?.div(2)
        val weatherForTomorrowPlus3Day = tomorrowPlus3DayForecastedWeatherListSize?.let {
            tomorrowPlus3DayForecastWeatherList?.getOrNull(
                it
            )
        }
        weatherForTomorrowPlus3Day?.let { finalList.add(it) }

        val tomorrowPlus4DayForecastedWeatherListSize =
            tomorrowPlus4DayForecastWeatherList?.size?.div(2)
        val weatherForTomorrowPlus4Day = tomorrowPlus4DayForecastedWeatherListSize?.let {
            tomorrowPlus4DayForecastWeatherList?.getOrNull(
                it
            )
        }
        weatherForTomorrowPlus4Day?.let { finalList.add(it) }

        return finalList
    }

    fun isDeviceConnectedToNetwork(context: Context): Boolean {
        return connectionVerificationService.isConnectedToNetwork(context)
    }
}