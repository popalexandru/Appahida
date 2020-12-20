package com.example.appahida.db.workoutDB

import androidx.room.*
import com.example.appahida.db.DayWithExercices
import com.example.appahida.db.customday.CustomDay
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(customDay : CustomDay)

    @Query("DELETE FROM CustomDay")
    fun deleteAll()

    @Delete
    fun deleteWorkout(customDay: CustomDay)

    @Update
    suspend fun updateWorkout(customDay: CustomDay)

    @Query("SELECT * FROM CustomDay")
    fun getAllWorkouts() : Flow<List<CustomDay>>

/*    @Query("SELECT * FROM CustomDay WHERE (zi = :zi)")
    fun getWorkoutByDate(zi : Int, luna : Int, an : Int) : Flow<CustomDay>*/

    @Transaction
    @Query("SELECT * FROM CustomDay")
    fun getDayswithExercices() : Flow<List<DayWithExercices>>
}