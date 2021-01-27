package com.example.appahida.db.workoutsdb

import androidx.room.*
import com.example.appahida.preferences.sortOrder
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

@Dao
interface WorkoutsExercicesDAO {
    @Insert
    suspend fun insertReps(reps: Reps)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: Day)

    @Delete
    suspend fun deleteDay(day : Day)

    @Query("UPDATE Day SET workoutDuration = :duration, isWorkoutDone = :done   WHERE dayId = :dayId")
    suspend fun updateDay(dayId : Long, duration : Long, done : Boolean = true)

    @Insert
    suspend fun insertExercice(exercice: Exercice)

    @Delete
    suspend fun deleteReps(reps: Reps)

    @Delete
    suspend fun deleteExercice(exercice: Exercice)

    @Transaction
    @Query("SELECT * FROM Day WHERE dayId = :timestamp")
    fun getDayWithExercices(timestamp : Long) : Flow<DayWithExercices>

    @Transaction
    @Query("SELECT * FROM Exercice WHERE dayId = :dayId")
    fun getExercicesForDay(dayId : Long) : Flow<List<ExercicesWithReps>>

    @Query("DELETE FROM Reps WHERE timestamp BETWEEN :startDay AND :endDay")
    fun deleteTodays(startDay : Long, endDay : Long)

    @Query("DELETE FROM Exercice WHERE dayId BETWEEN :startDay AND :endDay")
    fun deleteTodaysEx(startDay : Long, endDay : Long)

    @Query("SELECT * FROM Day WHERE dayId = :startDay")
    fun getTodayValue(startDay : Long) : Flow<Day>
}