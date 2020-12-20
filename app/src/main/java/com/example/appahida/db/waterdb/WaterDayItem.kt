package com.example.appahida.db.waterdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "water_table")
data class WaterDayItem(
        @ColumnInfo(name="water_qty") var waterQty : Int,
        @ColumnInfo(name="zi") var zi : Int,
        @ColumnInfo(name="luna") var luna : Int,
        @ColumnInfo(name="an") var an : Int
) : Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}
