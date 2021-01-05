package com.example.appahida.db.workoutsdb

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DayWithExercices(
    @Embedded val day: Day,
    @Relation(
            entity = Exercice::class,
            parentColumn = "dayId",
            entityColumn = "dayId"
    )
    val exercices : List<ExercicesWithReps>
)
