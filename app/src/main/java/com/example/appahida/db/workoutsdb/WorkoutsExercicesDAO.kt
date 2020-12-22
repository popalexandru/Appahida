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

    @Insert
    suspend fun insertDay(day: Day)

    @Delete
    suspend fun deleteDay(day: Day)

/*    @Query("SELECT * FROM Reps WHERE exId = :exerciceId")
    fun getRepsForExercice(exerciceId : Int) : Flow<List<Reps>>

    @Query("SELECT * FROM Exercice WHERE dayId = :dayId")
    fun getExercicesForDay(dayId : Int) : Flow<List<Exercice>>

    @Query("SELECT * FROM Day WHERE timestamp BETWEEN :startDay AND :endDay")
    fun getDay(startDay : Long, endDay : Long) : Flow<Day>*/

    @Transaction
    @Query("SELECT * FROM Day WHERE timestamp BETWEEN :startDay AND :endDay")
    fun getDayWithExercices(startDay : Long, endDay : Long) : Flow<DaywithExercices>
}