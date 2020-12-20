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

    fun getWaterByDate(dateInMilis: Long): Flow<List<WaterDayItem>> {
        val date = Calendar.getInstance()

        val zi = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)
        val year = date.get(Calendar.YEAR)

        return waterDao.getWaterByDate(zi, month, year)
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