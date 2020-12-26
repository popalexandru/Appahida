package com.example.appahida.repository

import com.example.appahida.db.waterdb.WaterDAO
import com.example.appahida.db.waterdb.WaterDayItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject

class WaterRepository @Inject constructor(
        val waterDao : WaterDAO
) {

    fun getWaterByDate(dateInMilis: Long): Flow<Int> {
        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = dateInMilis
        endOfDay.timeInMillis = dateInMilis

        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        return waterDao.getWaterByDay(beginOfDay.timeInMillis, endOfDay.timeInMillis)
    }

    fun insertWater(waterDayItem: WaterDayItem) = CoroutineScope(Dispatchers.IO).launch {
        waterDao.insertWater(waterDayItem)
    }

    fun deleteWater(waterDayItem: WaterDayItem) = CoroutineScope(Dispatchers.IO).launch {
        waterDao.deleteWater(waterDayItem)
    }

    fun updateWater(waterDayItem: WaterDayItem) = CoroutineScope(Dispatchers.IO).launch {
        waterDao.updateWater(waterDayItem)
    }
}