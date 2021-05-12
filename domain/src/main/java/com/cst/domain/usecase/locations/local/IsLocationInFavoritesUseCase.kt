package com.cst.domain.usecase.locations.local

import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class IsLocationInFavoritesUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<Boolean, IsLocationInFavoritesUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Boolean> {
        return Single.fromCallable { weatherRepository.isFavoriteLocation(locationId = params?.locationId) }
            .flatMap { isFavoriteLocation -> isFavoriteLocation }
    }

    data class Params(val locationId: Long)
}