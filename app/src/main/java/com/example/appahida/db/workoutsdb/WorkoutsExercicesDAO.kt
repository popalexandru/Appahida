package com.example.appahida.db.workoutsdb

import androidx.room.*
import com.example.appahida.preferences.sortOrder
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

@Dao
interface WorkoutsExercicesDAO {
    @Insert
    suspend fun insertReps(reps: Reps)

    @Delete
    suspend fun deleteReps(reps: Reps)

    @Insert
    suspend fun insertExercice(exercice: Exercice)

    @Delete
    suspend fun deleteExercice(exercice: Exercice)

    @Query("SELECT * FROM Exercice WHERE timestamp BETWEEN :startDay AND :endDay")
    fun getExercicesForDay(startDay : Long, endDay : Long) : Flow<List<ExercicesWithReps>>

    @Query("DELETE FROM Reps WHERE timestamp BETWEEN :startDay AND :endDay")
    fun deleteTodays(startDay : Long, endDay : Long)

    @Query("DELETE FROM Exercice WHERE timestamp BETWEEN :startDay AND :endDay")
    fun deleteTodaysEx(startDay : Long, endDay : Long)
}