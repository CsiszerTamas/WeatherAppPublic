package com.cst.domain.usecase.locations

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetLocationWeatherByLatLongUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<LocationWithWeatherEntity, GetLocationWeatherByLatLongUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<LocationWithWeatherEntity> {
        return weatherRepository.getWeatherForLocationByLatitudeAndLongitude(
            latitude = params?.latitude,
            longitude = params?.longitude
        )
    }

    data class Params(val latitude: Double, val longitude: Double)
}
