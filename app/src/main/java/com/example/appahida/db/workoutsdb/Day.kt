package com.example.appahida.db.workoutsdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Day(
        @PrimaryKey(autoGenerate = true)
        val dayId : Int?,
        val timestamp : Long,
        val zi : Int,
        val luna : Int,
        val an : Int
){}
