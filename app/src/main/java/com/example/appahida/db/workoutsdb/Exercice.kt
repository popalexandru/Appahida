package com.example.appahida.db.workoutsdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Exercice(
        @PrimaryKey(autoGenerate = true)
        val exId : Int?,
        val dayId : Long?,
        val name : String,
        val desc : String,
        val image : String
)
