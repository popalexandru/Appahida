package com.example.appahida.db.waterdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [WaterDayItem::class],
        version = 1
)
abstract class WaterDatabase : RoomDatabase(){
    abstract fun getWaterDAO() : WaterDAO
}