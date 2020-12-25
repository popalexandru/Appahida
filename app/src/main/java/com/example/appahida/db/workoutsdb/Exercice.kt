package com.example.appahida.db.workoutsdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Exercice(
        @PrimaryKey(autoGenerate = true)
        val exId : Int?,
        val timestamp : Long,
        val name : String,
        val desc : String,
        val image : String
)
