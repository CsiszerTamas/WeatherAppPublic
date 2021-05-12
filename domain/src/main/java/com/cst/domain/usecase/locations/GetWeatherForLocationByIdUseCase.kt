package com.cst.domain.usecase.locations

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetWeatherForLocationByIdUseCase
@Inject constructor(private val weatherRepository: WeatherRepository) :
    SingleUseCase<LocationWithWeatherEntity, GetWeatherForLocationByIdUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<LocationWithWeatherEntity> {
        return weatherRepository.getWeatherForLocationById(params?.locationId)
    }

    data class Params(val locationId: Long?)
}