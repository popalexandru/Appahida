package com.example.appahida.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.appahida.db.customday.CustomDay
import com.example.appahida.db.workoutDB.ExerciceForDay

class DayWithExercices (
        @Embedded val day : CustomDay,
        @Relation(
            parentColumn = "zi",
            entityColumn = "zi"
    )
    val exercices : List<ExerciceForDay>
)