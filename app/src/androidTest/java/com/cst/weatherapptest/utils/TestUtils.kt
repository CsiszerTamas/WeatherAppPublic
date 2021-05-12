package com.cst.weatherapptest.utils

import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.MainInfoEntity
import com.cst.domain.models.WeatherEntity
import com.cst.domain.models.forecast.CityEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.domain.models.forecast.listelement.*

object TestUtils {

    fun createCurrentLocationEntity(locationId: Long): CurrentLocationEntity {
        return CurrentLocationEntity(
            locationId = locationId
        )
    }

    fun createLocationWithWeatherEntity(): LocationWithWeatherEntity {
        return getLocationLondon()
    }

    fun createFiveDayForecastEntity(): FiveDayForecastEntity {
        val weatherListElementEntityList = mutableListOf<WeatherListElementEntity>()

        val cityEntity = CityEntity(
            id = 671964,
            name = "Odorheiu Secuiesc",
            country = "RO",
            timezone = 10800,
            sunrise = 1620615122,
            sunset = 1620668306
        )

        val weatherList = mutableListOf<WeatherEntity>()

        val main = MainInfoEntity(
            temp = 294.1,
            feelsLike = 293.4,
            tempMin = 293.78,
            tempMax = 294.1,
            pressure = 997,
            humidity = 44
        )

        val weather = WeatherEntity(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
        weatherList.add(weather)

        val clouds = CloudsEntity(
            all = 6
        )

        val wind = WindEntity(
            speed = 2.21,
            deg = 155,
            gust = 2.57
        )

        val rain = RainEntity(
            threeHour = 3.89
        )

        val snow = SnowEntity(
            threeHour = 3.89
        )

        val sys = SysEntity(
            partOfTheDay = "d"
        )

        val weatherListElementEntity = WeatherListElementEntity(
            dt = 1620874800,
            main = main,
            weather = weatherList,
            clouds = clouds,
            wind = wind,
            visibility = 10000,
            pop = 0.39,
            rain = rain,
            snow = snow,
            sys = sys,
            timeOfDataForecasted = "2021-05-13 00:00:00"
        )
        weatherListElementEntityList.add(weatherListElementEntity)

        return FiveDayForecastEntity(
            locationId = cityEntity.id,
            city = cityEntity,
            list = weatherListElementEntityList
        )
    }

    fun createLocationWithWeatherEntities(): List<LocationWithWeatherEntity> {

        val locationsWithWeather = mutableListOf<LocationWithWeatherEntity>()

        val locationParis = getLocationParis()
        val locationLondon = getLocationLondon()
        val locationBerlin = getLocationBerlin()

        locationsWithWeather.add(locationLondon)
        locationsWithWeather.add(locationParis)
        locationsWithWeather.add(locationBerlin)

        return locationsWithWeather
    }

    fun getLocationLondon(): LocationWithWeatherEntity {
        val weatherResponseData = mutableListOf<WeatherEntity>()
        val weatherEntity = WeatherEntity(
            id = 804,
            main = "Clouds",
            description = "overcast clouds",
            icon = "04d"
        )
        weatherResponseData.add(weatherEntity)

        val mainInfoEntity = MainInfoEntity(
            temp = 290.47,
            feelsLike = 289.8,
            tempMin = 289.82,
            tempMax = 291.15,
            pressure = 996,
            humidity = 59
        )

        return LocationWithWeatherEntity(
            id = 2643743,
            weatherResponseData = weatherResponseData,
            main = mainInfoEntity,
            dt = 1620585940,
            timezone = 3600,
            name = "London"
        )
    }

    fun getLocationParis(): LocationWithWeatherEntity {
        val weatherResponseData = mutableListOf<WeatherEntity>()
        val weatherEntity = WeatherEntity(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
        weatherResponseData.add(weatherEntity)

        val mainInfoEntity = MainInfoEntity(
            temp = 291.81,
            feelsLike = 291.38,
            tempMin = 291.15,
            tempMax = 293.15,
            pressure = 1000,
            humidity = 63
        )

        return LocationWithWeatherEntity(
            id = 2988507,
            weatherResponseData = weatherResponseData,
            main = mainInfoEntity,
            dt = 1620586402,
            timezone = 7200,
            name = "Paris"
        )
    }

    fun getLocationBerlin(): LocationWithWeatherEntity {
        val weatherResponseData = mutableListOf<WeatherEntity>()
        val weatherEntity = WeatherEntity(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
        weatherResponseData.add(weatherEntity)

        val mainInfoEntity = MainInfoEntity(
            temp = 295.15,
            feelsLike = 294.53,
            tempMin = 295.15,
            tempMax = 295.15,
            pressure = 1007,
            humidity = 43
        )

        return LocationWithWeatherEntity(
            id = 2950159,
            weatherResponseData = weatherResponseData,
            main = mainInfoEntity,
            dt = 1620586694,
            timezone = 7200,
            name = "Berlin"
        )
    }
}
