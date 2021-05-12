package com.cst.domain.usecase.locations.local

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class SaveLocationWithWeatherToFavoritesUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<Long, SaveLocationWithWeatherToFavoritesUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Long> {
        return Single.fromCallable { weatherRepository.insertLocationWithWeather(params?.locationWithWeatherEntity) }
            .flatMap { insertedRowId -> insertedRowId }
    }

    data class Params(val locationWithWeatherEntity: LocationWithWeatherEntity?)
}