package com.cst.domain.usecase.locations.local

import com.cst.domain.models.LocationWithWeatherEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class SaveMultipleLocationsWithWeatherToFavoritesUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<List<Long>, SaveMultipleLocationsWithWeatherToFavoritesUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<List<Long>> {
        return Single.fromCallable { weatherRepository.insertMultipleLocationsWithWeather(params?.locationWithWeatherEntity) }
            .flatMap { insertedRowIds -> insertedRowIds }
    }

    data class Params(val locationWithWeatherEntity: List<LocationWithWeatherEntity>?)
}