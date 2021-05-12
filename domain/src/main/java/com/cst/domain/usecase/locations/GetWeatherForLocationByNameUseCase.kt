package com.cst.domain.usecase.locations

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase

import io.reactivex.Single
import javax.inject.Inject

class GetWeatherForLocationByNameUseCase
@Inject constructor(private val weatherRepository: WeatherRepository) :
    SingleUseCase<LocationWithWeatherEntity, GetWeatherForLocationByNameUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<LocationWithWeatherEntity> {
        return weatherRepository.getWeatherForLocationByName(params?.locationName)
    }

    data class Params(val locationName: String)
}