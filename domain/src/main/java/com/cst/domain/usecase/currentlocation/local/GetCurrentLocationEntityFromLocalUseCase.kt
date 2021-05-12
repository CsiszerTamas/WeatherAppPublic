package com.cst.domain.usecase.currentlocation.local

import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetCurrentLocationEntityFromLocalUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<CurrentLocationEntity, GetCurrentLocationEntityFromLocalUseCase.Params>() {

    /*
     * Single.fromCallable(y) invokes the y callable in the subscribeOn scheduler at the time of
     * subscription and separately for each subscriber.
     *
     * Having Single.just() here would not work since it would be executed on the current thread.
     */
    override fun buildUseCaseSingle(params: Params?): Single<CurrentLocationEntity> {
        return Single.fromCallable { weatherRepository.getCurrentLocationEntity() }
            .flatMap { currentLocationEntity -> currentLocationEntity }
    }

    data class Params(val params: Unit)
}