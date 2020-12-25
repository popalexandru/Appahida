package com.example.appahida.db.workoutsdb

import androidx.room.Embedded
import androidx.room.Relation

data class ExercicesWithReps(
        @Embedded val exercice: Exercice,
        @Relation(
                parentColumn = "exId",
                entityColumn = "exId"
        )
        val reps: List<Reps>
)
