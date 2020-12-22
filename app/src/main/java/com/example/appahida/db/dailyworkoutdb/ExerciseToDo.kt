package com.example.appahida.db.dailyworkoutdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ExerciseToDo(
    @PrimaryKey val timestamp : Calendar = Calendar.getInstance(Locale("ro", "RO")),
    val name : String,
    val desc : String,
    val image : String
    )