package com.example.appahida.db.dailyworkoutdb

import androidx.room.Embedded
import androidx.room.Relation

data class DayWithExercices(
        @Embedded val day : CustomDay,
        @Relation(
                parentColumn = "day",
                entityColumn = "day"
        )
        val exercices : List<ExerciseToDo>
)
