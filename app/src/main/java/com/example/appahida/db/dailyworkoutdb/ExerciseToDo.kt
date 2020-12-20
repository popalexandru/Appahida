package com.example.appahida.db.dailyworkoutdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ExerciseToDo(
    @PrimaryKey(autoGenerate = false)
    val day : Int,
    val name : String,
    val desc : String,
    val image : String
    )