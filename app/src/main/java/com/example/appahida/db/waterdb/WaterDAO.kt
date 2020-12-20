package com.example.appahida.db.waterdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDAO {

    @Query("SELECT * FROM water_table WHERE (zi = :zi AND luna = :luna AND an = :an)")
    fun getWaterByDate(zi : Int, luna : Int, an : Int) : Flow<List<WaterDayItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(waterDayItem: WaterDayItem)

    @Delete
    suspend fun deleteWater(waterDayItem: WaterDayItem)

    @Update
    suspend fun updateWater(waterDayItem: WaterDayItem)
}