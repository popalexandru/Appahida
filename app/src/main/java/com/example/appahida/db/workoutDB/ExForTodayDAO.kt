package com.example.appahida.db.workoutDB

import androidx.room.*
import com.example.appahida.db.DayWithExercices
import com.example.appahida.db.customday.CustomDay
import kotlinx.coroutines.flow.Flow

@Dao
interface ExForTodayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(customDay : ExerciceForDay)
}