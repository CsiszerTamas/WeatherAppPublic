package com.cst.domain.usecase.locations

import android.location.Address
import android.location.Geocoder
import com.cst.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.io.IOException
import javax.inject.Inject

class GetLocationsFromGeocoderByLocationNameUseCase
@Inject constructor(
    private val geocoder: Geocoder
) :
    SingleUseCase<MutableList<Address>, GetLocationsFromGeocoderByLocationNameUseCase.Params>() {

    companion object {
        private const val MAX_LOCATION_SEARCH_RESULTS = 5
    }

    override fun buildUseCaseSingle(params: Params?): Single<MutableList<Address>> {
        return Single.create { emitter: SingleEmitter<MutableList<Address>> ->
            try {
                val locationList =
                    geocoder.getFromLocationName(
                        params?.locationName,
                        MAX_LOCATION_SEARCH_RESULTS
                    )
                emitter.onSuccess(locationList)
            } catch (ex: IOException) {
                emitter.onError(ex)
            }
        }
    }

    data class Params(val locationName: String)
}