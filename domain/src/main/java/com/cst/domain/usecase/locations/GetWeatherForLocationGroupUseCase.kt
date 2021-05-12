package com.cst.domain.usecase.locations

import com.cst.domain.models.WeatherForLocationGroupEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetWeatherForLocationGroupUseCase
@Inject constructor
    (
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<WeatherForLocationGroupEntity, GetWeatherForLocationGroupUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<WeatherForLocationGroupEntity> {
        return weatherRepository.getWeatherForLocationGroupByIds(params?.locationIds)
    }

    data class Params(val locationIds: List<Long>?)
}

