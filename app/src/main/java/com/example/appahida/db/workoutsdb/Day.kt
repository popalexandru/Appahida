package com.example.appahida.db.workoutsdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Day(
        @PrimaryKey(autoGenerate = false)
        val dayId : Long,
        val dateString : String,
        var isWorkoutDone : Boolean = false,
        var workoutDuration : Long = 0
){
        fun finishWorkout(){
                isWorkoutDone = true
        }

        fun setworkoutDuration(duration : Long){
                workoutDuration = duration
        }
}
