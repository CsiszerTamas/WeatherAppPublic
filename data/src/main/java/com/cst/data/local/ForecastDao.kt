package com.cst.data.local

import androidx.room.*
import com.cst.domain.models.forecast.FiveDayForecastEntity

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFiveDayForecastEntity(fiveDayForecastEntity: FiveDayForecastEntity?): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFiveDayForecastEntity(fiveDayForecastEntity: FiveDayForecastEntity?)

    @Delete
    fun deleteFiveDayForecastEntity(fiveDayForecastEntity: FiveDayForecastEntity?)

    @Query("SELECT * FROM forecast_for_location WHERE locationId = :locationId")
    fun getFiveDayForecastEntityByLocationId(locationId: Long?): FiveDayForecastEntity
}