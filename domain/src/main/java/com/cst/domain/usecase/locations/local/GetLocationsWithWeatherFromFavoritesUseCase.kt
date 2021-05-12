package com.cst.domain.usecase.locations.local

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetLocationsWithWeatherFromFavoritesUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<List<LocationWithWeatherEntity>, GetLocationsWithWeatherFromFavoritesUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<List<LocationWithWeatherEntity>> {
        return Single.fromCallable { weatherRepository.getLocationsWithWeatherFromFavorites() }
            .flatMap { list -> list }
    }

    data class Params(val params: Unit)
}