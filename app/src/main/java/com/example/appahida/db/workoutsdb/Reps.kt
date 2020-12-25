package com.example.appahida.db.workoutsdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reps(
        @PrimaryKey(autoGenerate = true)
        val repId : Int?,
        val timestamp : Long,
        val exId : Int,
        val repCount : Int,
        val repWeight : Int
){}
