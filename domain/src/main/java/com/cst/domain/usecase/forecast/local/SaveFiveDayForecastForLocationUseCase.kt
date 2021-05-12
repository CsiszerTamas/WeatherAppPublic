package com.cst.domain.usecase.forecast.local

import com.cst.domain.models.forecast.FiveDayForecastEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class SaveFiveDayForecastForLocationUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<Long, SaveFiveDayForecastForLocationUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Long> {
        return Single.fromCallable { weatherRepository.insertFiveDayForecastForLocation(params?.fiveDayForecastEntity) }
            .flatMap { insertedRowId -> insertedRowId }
    }

    data class Params(val fiveDayForecastEntity: FiveDayForecastEntity?)
}