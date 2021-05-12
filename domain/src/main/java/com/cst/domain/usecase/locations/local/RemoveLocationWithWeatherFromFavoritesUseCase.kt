package com.cst.domain.usecase.locations.local

import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class RemoveLocationWithWeatherFromFavoritesUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<Int, RemoveLocationWithWeatherFromFavoritesUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Int> {
        return Single.fromCallable { weatherRepository.removeLocationFromFavorites(locationId = params?.locationId) }
            .flatMap { affectedRowNumber -> affectedRowNumber }
    }

    data class Params(val locationId: Long)
}