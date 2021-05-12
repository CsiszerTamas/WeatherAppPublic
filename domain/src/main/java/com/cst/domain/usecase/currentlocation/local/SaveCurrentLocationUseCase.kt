package com.cst.domain.usecase.currentlocation.local

import com.cst.domain.models.CurrentLocationEntity
import com.cst.domain.repositories.WeatherRepository
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class SaveCurrentLocationUseCase
@Inject constructor(
    private val weatherRepository: WeatherRepository
) :
    SingleUseCase<Long, SaveCurrentLocationUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params?): Single<Long> {
        return Single.fromCallable { weatherRepository.insertCurrentLocationEntity(params?.currentLocationEntity) }
            .flatMap { insertedRowId -> insertedRowId }
    }

    data class Params(val currentLocationEntity: CurrentLocationEntity?)
}