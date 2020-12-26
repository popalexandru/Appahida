package com.example.appahida.db.waterdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "water_table")
data class WaterDayItem(
       var waterQty : Int = 0,
       var zi : Long = 0
) : Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}
