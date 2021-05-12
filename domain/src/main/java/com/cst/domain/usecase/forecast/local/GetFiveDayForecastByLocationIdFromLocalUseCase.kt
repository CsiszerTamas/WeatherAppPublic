package com.cst.domain.usecase.forecast.local

import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetFiveDayForecastByLocationIdFromLocalUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<FiveDayForecastEntity, GetFiveDayForecastByLocationIdFromLocalUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<FiveDayForecastEntity> {
        return Single.fromCallable { weatherRepository.getFiveDayForecastForLocationFromLocal(params?.locationId) }
            .flatMap { fiveDayForecastForLocation -> fiveDayForecastForLocation }
    }

    data class Params(val locationId: Long)
}