package com.cst.data.local

import androidx.room.*
import com.cst.domain.models.CurrentLocationEntity

@Dao
interface CurrentLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentLocationEntity(currentLocationEntity: CurrentLocationEntity?): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrentLocationEntity(currentLocationEntity: CurrentLocationEntity?)

    @Delete
    fun deleteCurrentLocationEntity(currentLocationEntity: CurrentLocationEntity?)

    @Query("SELECT * FROM current_location LIMIT 1")
    fun getCurrentLocationEntity(): CurrentLocationEntity
}