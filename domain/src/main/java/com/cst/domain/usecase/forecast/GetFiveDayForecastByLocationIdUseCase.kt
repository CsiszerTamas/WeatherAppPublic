package com.cst.domain.usecase.forecast

import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetFiveDayForecastByLocationIdUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<FiveDayForecastEntity, GetFiveDayForecastByLocationIdUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<FiveDayForecastEntity> {
        return weatherRepository.getFiveDayForecastForLocationById(params?.locationId)
    }

    data class Params(val locationId: Long)
}
