package com.cst.data.mapper

import com.cst.data.models.LocationWithWeatherModel
import com.cst.data.models.WeatherForLocationGroupModel
import com.cst.data.models.forecast.FiveDayForecastModel
import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.models.MainInfoEntity
import com.cst.domain.models.WeatherEntity
import com.cst.domain.models.WeatherForLocationGroupEntity
import com.cst.domain.models.forecast.CityEntity
import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.models.forecast.WeatherListElementEntity
import com.cst.domain.models.forecast.listelement.*

/**
 * Mapper class to convert the models into entities
 */
class WeatherMapper {

    fun modelToEntity(locationWithWeatherModel: LocationWithWeatherModel): LocationWithWeatherEntity {

        val weatherResponseData = mutableListOf<WeatherEntity>()

        locationWithWeatherModel.weatherModelResponseData?.forEach { weather ->
            val weatherEntity = WeatherEntity(
                id = weather.id,
                main = weather.main,
                description = weather.description,
                icon = weather.icon
            )

            weatherResponseData.add(weatherEntity)
        }

        val mainInfoEntity = MainInfoEntity(
            temp = locationWithWeatherModel.mainModel?.temp,
            feelsLike = locationWithWeatherModel.mainModel?.feelsLike,
            tempMin = locationWithWeatherModel.mainModel?.tempMin,
            tempMax = locationWithWeatherModel.mainModel?.tempMax,
            pressure = locationWithWeatherModel.mainModel?.pressure,
            humidity = locationWithWeatherModel.mainModel?.humidity,
        )

        return LocationWithWeatherEntity(
            id = locationWithWeatherModel.id,
            weatherResponseData = weatherResponseData,
            main = mainInfoEntity,
            dt = locationWithWeatherModel.dt,
            timezone = locationWithWeatherModel.timezone,
            name = locationWithWeatherModel.name
        )
    }

    fun modelToEntity(fiveDayForecastModel: FiveDayForecastModel): FiveDayForecastEntity {

        val weatherListElementEntityList = mutableListOf<WeatherListElementEntity>()

        val cityEntity = CityEntity(
            id = fiveDayForecastModel.city?.id,
            name = fiveDayForecastModel.city?.name,
            country = fiveDayForecastModel.city?.country,
            timezone = fiveDayForecastModel.city?.timezone,
            sunrise = fiveDayForecastModel.city?.sunrise,
            sunset = fiveDayForecastModel.city?.sunset
        )

        fiveDayForecastModel.list?.forEach { weatherListElementModel ->

            val weatherList = mutableListOf<WeatherEntity>()

            val main = MainInfoEntity(
                temp = weatherListElementModel.main?.temp,
                feelsLike = weatherListElementModel.main?.feelsLike,
                tempMin = weatherListElementModel.main?.tempMin,
                tempMax = weatherListElementModel.main?.tempMax,
                pressure = weatherListElementModel.main?.pressure,
                humidity = weatherListElementModel.main?.humidity,
            )

            weatherListElementModel.weather?.forEach { weatherModel ->
                val weather = WeatherEntity(
                    id = weatherModel.id,
                    main = weatherModel.main,
                    description = weatherModel.description,
                    icon = weatherModel.icon
                )
                weatherList.add(weather)
            }


            val clouds = CloudsEntity(
                all = weatherListElementModel.clouds?.all
            )

            val wind = WindEntity(
                speed = weatherListElementModel.wind?.speed,
                deg = weatherListElementModel.wind?.deg,
                gust = weatherListElementModel.wind?.gust
            )

            val rain = RainEntity(
                threeHour = weatherListElementModel.rain?.threeHour
            )

            val snow = SnowEntity(
                threeHour = weatherListElementModel.snow?.threeHour
            )

            val sys = SysEntity(
                partOfTheDay = weatherListElementModel.sys?.partOfTheDay
            )

            val weatherListElementEntity = WeatherListElementEntity(
                dt = weatherListElementModel.dt,
                main = main,
                weather = weatherList,
                clouds = clouds,
                wind = wind,
                visibility = weatherListElementModel.visibility,
                pop = weatherListElementModel.pop,
                rain = rain,
                snow = snow,
                sys = sys,
                timeOfDataForecasted = weatherListElementModel.timeOfDataForecasted
            )
            weatherListElementEntityList.add(weatherListElementEntity)
        }

        return FiveDayForecastEntity(
            locationId = cityEntity.id,
            city = cityEntity,
            list = weatherListElementEntityList,
        )
    }

    fun modelToEntity(weatherForLocationGroupModel: WeatherForLocationGroupModel): WeatherForLocationGroupEntity {

        val list = mutableListOf<LocationWithWeatherEntity>()

        weatherForLocationGroupModel.weatherForLocationList?.forEach { locationWithWeatherModel ->

            val weatherResponseData = mutableListOf<WeatherEntity>()

            locationWithWeatherModel.weatherModelResponseData?.forEach { weather ->
                val weatherEntity = WeatherEntity(
                    id = weather.id,
                    main = weather.main,
                    description = weather.description,
                    icon = weather.icon
                )

                weatherResponseData.add(weatherEntity)
            }

            val mainInfoEntity = MainInfoEntity(
                temp = locationWithWeatherModel.mainModel?.temp,
                feelsLike = locationWithWeatherModel.mainModel?.feelsLike,
                tempMin = locationWithWeatherModel.mainModel?.tempMin,
                tempMax = locationWithWeatherModel.mainModel?.tempMax,
                pressure = locationWithWeatherModel.mainModel?.pressure,
                humidity = locationWithWeatherModel.mainModel?.humidity,
            )

            val entity = LocationWithWeatherEntity(
                id = locationWithWeatherModel.id,
                weatherResponseData = weatherResponseData,
                main = mainInfoEntity,
                dt = locationWithWeatherModel.dt,
                timezone = locationWithWeatherModel.timezone,
                name = locationWithWeatherModel.name
            )
            list.add(entity)
        }
        return WeatherForLocationGroupEntity(list)
    }
}
