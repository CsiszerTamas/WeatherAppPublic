package com.cst.weatherapptest.ui.locationsearch

import android.content.Context
import android.location.Address
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.items.WeatherAttributeItem
import com.cst.domain.usecase.forecast.GetFiveDayForecastByLocationIdUseCase
import com.cst.domain.usecase.forecast.local.SaveFiveDayForecastForLocationUseCase
import com.cst.domain.usecase.locations.GetLocationsFromGeocoderByCoordinatesUseCase
import com.cst.domain.usecase.locations.GetLocationsFromGeocoderByLocationNameUseCase
import com.cst.domain.usecase.locations.GetWeatherForLocationByNameUseCase
import com.cst.domain.usecase.locations.local.SaveLocationWithWeatherToFavoritesUseCase
import com.cst.weatherapptest.R
import com.cst.weatherapptest.util.Utils
import com.cst.weatherapptest.util.connection.ConnectionVerificationService
import com.cst.weatherapptest.util.enums.UnitOfMeasurement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val connectionVerificationService: ConnectionVerificationService,
    private val getWeatherForLocationByNameUseCase: GetWeatherForLocationByNameUseCase,
    private val getLocationsFromGeocoderByLocationNameUseCase: GetLocationsFromGeocoderByLocationNameUseCase,
    private val getLocationsFromGeocoderByCoordinatesUseCase: GetLocationsFromGeocoderByCoordinatesUseCase,
    private val saveLocationWithWeatherToFavoritesUseCase: SaveLocationWithWeatherToFavoritesUseCase,
    private val getFiveDayForecastByLocationIdUseCase: GetFiveDayForecastByLocationIdUseCase,
    private val saveFiveDayForecastForLocationUseCase: SaveFiveDayForecastForLocationUseCase,
) : ViewModel() {

    var locationWithWeatherEntity: LocationWithWeatherEntity? = null

    val isLoading = MutableLiveData<Boolean>()
    val searchErrorAppeared = MutableLiveData<Boolean>()

    val locationData = MutableLiveData<LocationWithWeatherEntity>()
    val addressesByName = MutableLiveData<MutableList<Address>>()
    val addressesByCoordinates = MutableLiveData<MutableList<Address>>()

    var locationList: MutableList<Address> = mutableListOf()

    val locationAddedToFavorites = MutableLiveData<Boolean>()

    fun getLocationWeatherByName(locationName: String) {

        getWeatherForLocationByNameUseCase.execute(
            GetWeatherForLocationByNameUseCase.Params(locationName),
            onSuccess = {
                isLoading.value = false
                locationData.value = it
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getLocationAddressesFromGeocoderByName(locationName: String) {
        getLocationsFromGeocoderByLocationNameUseCase.execute(
            GetLocationsFromGeocoderByLocationNameUseCase.Params(locationName),
            onSuccess = {
                isLoading.value = false
                addressesByName.value = it
            },
            onError = {
                isLoading.value = false
                searchErrorAppeared.value = true
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
                isLoading.value = false
                addressesByCoordinates.value = it
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun saveLocationWithWeatherToFavorites(locationWithWeatherEntity: LocationWithWeatherEntity) {
        saveLocationWithWeatherToFavoritesUseCase.execute(
            SaveLocationWithWeatherToFavoritesUseCase.Params(
                locationWithWeatherEntity = locationWithWeatherEntity
            ),
            onSuccess = {
                isLoading.value = false
                locationAddedToFavorites.value = true
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun getAndSaveForecastForLocationById(locationId: Long) {
        getFiveDayForecastByLocationIdUseCase.execute(
            GetFiveDayForecastByLocationIdUseCase.Params(locationId),
            onSuccess = {
                saveFiveDayForecastForLocation(it)
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    private fun saveFiveDayForecastForLocation(fiveDayForecastEntity: FiveDayForecastEntity) {
        saveFiveDayForecastForLocationUseCase.execute(
            SaveFiveDayForecastForLocationUseCase.Params(
                fiveDayForecastEntity = fiveDayForecastEntity
            ),
            onSuccess = {
                Log.d(
                    "DEBUG_",
                    "LocationSearchViewModel saveFiveDayForecastForLocation: fiveDayForecastEntity = $fiveDayForecastEntity"
                )
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    fun isDeviceConnectedToNetwork(context: Context): Boolean {
        return connectionVerificationService.isConnectedToNetwork(context)
    }

    fun createWeatherAttributeList(
        context: Context,
        locationWithWeatherEntity: LocationWithWeatherEntity,
        selectedUnitOfMeasurement: UnitOfMeasurement
    ): MutableList<WeatherAttributeItem> {
        val weatherAttributeList = mutableListOf<WeatherAttributeItem>()
        weatherAttributeList.add(
            WeatherAttributeItem(
                R.drawable.ic_humidity,
                context.resources.getString(R.string.weather_attribute_title_humidity),
                "${locationWithWeatherEntity.main?.humidity} ${UnitOfMeasurement.UNIT_OF_MEASURE_HUMIDITY}"
            )
        )
        locationWithWeatherEntity.main?.feelsLike?.let { feelsLike ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_temperature_feels_like_2,
                    context.resources.getString(R.string.weather_attribute_title_feels_like),
                    Utils.getTemperatureWithUnit(feelsLike, selectedUnitOfMeasurement)
                )
            )
        }
        locationWithWeatherEntity.main?.tempMin?.let { tempMin ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_min_temperature,
                    context.resources.getString(R.string.weather_attribute_title_min_temperature),
                    Utils.getTemperatureWithUnit(tempMin, selectedUnitOfMeasurement)
                )
            )
        }
        locationWithWeatherEntity.main?.tempMax?.let { tempMax ->
            weatherAttributeList.add(
                WeatherAttributeItem(
                    R.drawable.ic_max_temperature,
                    context.resources.getString(R.string.weather_attribute_title_max_temperature),
                    Utils.getTemperatureWithUnit(tempMax, selectedUnitOfMeasurement)
                )
            )
        }
        weatherAttributeList.add(
            WeatherAttributeItem(
                R.drawable.ic_pressure,
                context.resources.getString(R.string.weather_attribute_title_pressure),
                "${locationWithWeatherEntity.main?.pressure} ${UnitOfMeasurement.UNIT_OF_MEASURE_PRESSURE}"
            )
        )
        return weatherAttributeList
    }
}
