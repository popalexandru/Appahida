package com.example.appahida.db.workoutsdb

import androidx.room.Embedded
import androidx.room.Relation

data class DaywithExercices(
        @Embedded val day : Day,
        @Relation(
                parentColumn = "dayId",
                entityColumn = "dayId"
        )
        val exercices : List<Exercice>
)
