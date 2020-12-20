package com.example.appahida.db.dailyworkoutdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyExercicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day : CustomDay)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercice(exercice : ExerciseToDo)

    @Transaction
    @Query("SELECT * FROM CustomDay WHERE day = :day")
    suspend fun getDayWithExercices(day : Int)
}