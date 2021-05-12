package com.cst.data.apiservice

import com.cst.data.models.LocationWithWeatherModel
import com.cst.data.models.WeatherForLocationGroupModel
import com.cst.data.models.forecast.FiveDayForecastModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // TODO: Please paste a valid OpenWeather API key here,
    // It was attached with the link of the repository.
    companion object {
        const val OPENWEATHER_API_KEY = "PASTE_THE_API_KEY_HERE"
    }

    @GET("weather")
    fun getWeatherForLocationByName(
        @Query("q", encoded = true) locationName: String?,
        @Query("appid") appId: String = OPENWEATHER_API_KEY
    ): Single<LocationWithWeatherModel>

    @GET("weather")
    fun getWeatherForLocationById(
        @Query("id", encoded = true) locationId: Long?,
        @Query("appid") appId: String = OPENWEATHER_API_KEY
    ): Single<LocationWithWeatherModel>

    /* We need to send the locations ids as a string separated by commas */
    @GET("group")
    fun getWeatherForLocationGroupById(
        @Query("id", encoded = true) locationIds: String?,
        @Query("appid") appId: String = OPENWEATHER_API_KEY
    ): Single<WeatherForLocationGroupModel>

    @GET("weather")
    fun getWeatherForLocationWithLatitudeAndLongitude(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("appid") appId: String = OPENWEATHER_API_KEY
    ): Single<LocationWithWeatherModel>

    @GET("forecast")
    fun getFiveDayForecastForLocationById(
        @Query("id", encoded = true) locationId: Long?,
        @Query("appid") appId: String = OPENWEATHER_API_KEY
    ): Single<FiveDayForecastModel>
}
