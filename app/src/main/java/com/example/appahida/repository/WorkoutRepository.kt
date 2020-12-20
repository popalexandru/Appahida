package com.example.appahida.repository

import com.example.appahida.db.DayWithExercices
import com.example.appahida.db.workoutDB.WorkoutDAO
import com.example.appahida.db.customday.CustomDay
import com.example.appahida.db.customday.CustomDayDAO
import com.example.appahida.db.workoutDB.ExForTodayDAO
import com.example.appahida.db.workoutDB.ExerciceForDay
import com.example.appahida.objects.ExerciseToAdd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
        val workoutDAO: WorkoutDAO,
        val customDayDAO: CustomDayDAO,
        val exForTodayDAO: ExForTodayDAO
) {

/*    fun getTodaysWorkout(dateInMilis: Long): Flow<CustomDay> {
        val date = Calendar.getInstance()

        val zi = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)
        val year = date.get(Calendar.YEAR)

        //return workoutDAO.getWorkoutByDate(zi, month, year)
    }*/

    fun getDaysWithExercices() : Flow<List<DayWithExercices>>{
        return workoutDAO.getDayswithExercices()
    }

    fun insertWorkout(customDay: CustomDay) = CoroutineScope(Dispatchers.IO).launch {
        val date = Calendar.getInstance()

        val zi = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)
        val year = date.get(Calendar.YEAR)

        customDayDAO.insertExercise(CustomDay(zi))
    }

    fun getAllWorkouts() : Flow<List<CustomDay>>{
       return workoutDAO.getAllWorkouts()
    }

    fun deleteWorkout(customDay: CustomDay){
        workoutDAO.deleteWorkout(customDay)
    }

    fun updateWorkout(customDay: CustomDay) = CoroutineScope(Dispatchers.IO).launch {
        workoutDAO.updateWorkout(customDay)
    }

    fun insertExercices(exerciseToAdd: ExerciseToAdd) = CoroutineScope(Dispatchers.IO).launch {
        val date = Calendar.getInstance()

        val zi = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)
        val year = date.get(Calendar.YEAR)

        exForTodayDAO.insertWorkout(ExerciceForDay(zi, "month", "year", "", ""))
    }
}