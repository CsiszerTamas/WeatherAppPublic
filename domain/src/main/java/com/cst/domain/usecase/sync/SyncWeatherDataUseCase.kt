package com.cst.domain.usecase.sync

import android.util.Log
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.usecase.base.SingleUseCase
import com.cst.domain.usecase.forecast.GetFiveDayForecastByLocationIdUseCase
import com.cst.domain.usecase.forecast.local.SaveFiveDayForecastForLocationUseCase
import com.cst.domain.usecase.locations.GetWeatherForLocationGroupUseCase
import com.cst.domain.usecase.locations.local.GetLocationsWithWeatherFromFavoritesUseCase
import com.cst.domain.usecase.locations.local.SaveMultipleLocationsWithWeatherToFavoritesUseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * UseCase for updating the cached location and forecast data by a background service in the scheduled time periods.
 */
class SyncWeatherDataUseCase
@Inject constructor(
    private val getLocationsWithWeatherFromFavoritesUseCase: GetLocationsWithWeatherFromFavoritesUseCase,
    private val getWeatherForLocationGroupUseCase: GetWeatherForLocationGroupUseCase,
    private val saveMultipleLocationsWithWeatherToFavoritesUseCase: SaveMultipleLocationsWithWeatherToFavoritesUseCase,
    private val getFiveDayForecastByLocationIdUseCase: GetFiveDayForecastByLocationIdUseCase,
    private val saveFiveDayForecastForLocationUseCase: SaveFiveDayForecastForLocationUseCase,
) :
    SingleUseCase<Unit, SyncWeatherDataUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Unit> {
        getLocationsWithWeatherFromFavoritesUseCase.execute(
            GetLocationsWithWeatherFromFavoritesUseCase.Params(params = Unit),
            onSuccess = { locations ->
                val locationIds =
                    locations.map { locationWithWeatherEntity -> locationWithWeatherEntity.id }
                val mutableLocationIds = locationIds as MutableList<Long>

                // Get and save latest information about locations with weather
                getUpToDateDataForFavoriteLocations(mutableLocationIds)
                // Get and save latest information about forecast for saved locations
                getAndSaveForecastForLocations(mutableLocationIds)
            },
            onError = {
                it.printStackTrace()
            }
        )
        return Single.just(Unit)
    }

    // Recursively call this method to get and insert the forecast data by locationId
    private fun getAndSaveForecastForLocations(mutableLocationIds: MutableList<Long>) {
        val currentLocationId = mutableLocationIds.firstOrNull()
        currentLocationId?.let { locationId ->
            //  We get the FiveDayForecastEntity objects by the id from the network
            getFiveDayForecastByLocationIdUseCase.execute(
                GetFiveDayForecastByLocationIdUseCase.Params(locationId),
                onSuccess = {
                    saveFiveDayForecastForLocation(it, mutableLocationIds)
                },
                onError = {
                    it.printStackTrace()
                }
            )
        }
    }

    private fun getUpToDateDataForFavoriteLocations(favoriteLocationIds: MutableList<Long>) {
        getWeatherForLocationGroupUseCase.execute(
            GetWeatherForLocationGroupUseCase.Params(favoriteLocationIds),
            onSuccess = { weatherForLocationGroupEntity ->
                weatherForLocationGroupEntity.weatherForLocationList?.let { weatherForLocationList ->
                    if (weatherForLocationList.isNotEmpty()) {
                        // We save the updated location list into the local database
                        saveUpToDateLocationWeatherData(weatherForLocationList)
                    }
                }
            },
            onError = {
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
                Log.d("DEBUG_SyncWeatherDataUseCase", "insertedLocationsIds: $insertedLocationsIds")
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    private fun saveFiveDayForecastForLocation(
        fiveDayForecastEntity: FiveDayForecastEntity,
        mutableLocationIds: MutableList<Long>
    ) {
        saveFiveDayForecastForLocationUseCase.execute(
            SaveFiveDayForecastForLocationUseCase.Params(
                fiveDayForecastEntity = fiveDayForecastEntity
            ),
            onSuccess = { insertedFiveDayForecastId ->
                /* Every time when we insert forecast data for a location,
                   we remove it from the list and call getAndSaveForecastForLocations
                 for the remaining items
                */
                mutableLocationIds.removeFirstOrNull()
                getAndSaveForecastForLocations(mutableLocationIds)
                Log.d(
                    "DEBUG_SyncWeatherDataUseCase",
                    "saveFiveDayForecastForLocation insertedFiveDayForecastId: $insertedFiveDayForecastId"
                )
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    data class Params(val params: Unit)
}