package com.cst.weatherapptest.ui.locations

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.usecase.locations.GetWeatherForLocationGroupUseCase
import com.cst.domain.usecase.locations.local.GetLocationsWithWeatherFromFavoritesUseCase
import com.cst.domain.usecase.locations.local.SaveMultipleLocationsWithWeatherToFavoritesUseCase
import com.cst.weatherapptest.util.connection.ConnectionVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val connectionVerificationService: ConnectionVerificationService,
    private val getWeatherForLocationGroupUseCase: GetWeatherForLocationGroupUseCase,
    private val getLocationsWithWeatherFromFavoritesUseCase: GetLocationsWithWeatherFromFavoritesUseCase,
    private val saveMultipleLocationsWithWeatherToFavoritesUseCase: SaveMultipleLocationsWithWeatherToFavoritesUseCase
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val showEmptyListPlaceholder = MutableLiveData<Boolean>()
    val isLocationsDataRefreshing = MutableLiveData<Boolean>()

    val locationGroupData = MutableLiveData<List<LocationWithWeatherEntity>>()
    var allLocationsWithWeather: MutableList<LocationWithWeatherEntity>? = null

    fun getWeatherForLocationGroup(context: Context) {
        getLocationsWithWeatherFromFavoritesUseCase.execute(
            GetLocationsWithWeatherFromFavoritesUseCase.Params(params = Unit),
            onSuccess = { locations ->
                if (connectionVerificationService.isConnectedToNetwork(context)) {
                    if (locations.isEmpty().not()) {
                        showEmptyListPlaceholder.value = false
                        val favoriteLocationIds = mutableListOf<Long>()
                        locations.forEach { location ->
                            location.id?.let { favoriteLocationIds.add(it) }
                        }
                        getUpToDateDataForFavoriteLocations(favoriteLocationIds)
                    } else {
                        isLoading.value = false
                        showEmptyListPlaceholder.value = true
                    }
                } else {
                    isLoading.value = false
                    locationGroupData.value = locations
                }
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    private fun getUpToDateDataForFavoriteLocations(favoriteLocationIds: MutableList<Long>) {
        isLoading.value = true
        getWeatherForLocationGroupUseCase.execute(
            GetWeatherForLocationGroupUseCase.Params(favoriteLocationIds),
            onSuccess = { weatherForLocationGroupEntity ->
                weatherForLocationGroupEntity.weatherForLocationList?.let { weatherForLocationList ->
                    if (weatherForLocationList.isNotEmpty()) {
                        // First we show the updated location list on the UI for the user
                        isLoading.value = false
                        locationGroupData.value = weatherForLocationList
                        // In the background we save the updated location list into the local database
                        saveUpToDateLocationWeatherData(weatherForLocationList)
                    } else {
                        isLoading.value = false
                    }
                }
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    private fun saveUpToDateLocationWeatherData(weatherForLocationList: List<LocationWithWeatherEntity>) {
        saveMultipleLocationsWithWeatherToFavoritesUseCase.execute(
            SaveMultipleLocationsWithWeatherToFavoritesUseCase.Params(
                weatherForLocationList
            ),
            onSuccess = { insertedLocationsIds ->
                Log.d("DEBUG_", "insertedLocationsIds: $insertedLocationsIds")
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            }
        )
    }

    fun isDeviceConnectedToNetwork(context: Context): Boolean {
        return connectionVerificationService.isConnectedToNetwork(context)
    }
}
