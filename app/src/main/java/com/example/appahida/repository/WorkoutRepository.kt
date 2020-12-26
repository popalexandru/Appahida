package com.example.appahida.repository

import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.ExercicesWithReps
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.db.workoutsdb.WorkoutsExercicesDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
        val workoutExercicesDAO: WorkoutsExercicesDAO
) {
    fun getDayWorkouts(todayMilis : Long): Flow<List<ExercicesWithReps>> {
        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = todayMilis
        endOfDay.timeInMillis = todayMilis

        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        return workoutExercicesDAO.getExercicesForDay(beginOfDay.timeInMillis, endOfDay.timeInMillis)
    }

    fun insertWorkout(exercice: Exercice) = CoroutineScope(Dispatchers.IO).launch {

        workoutExercicesDAO.insertExercice(exercice)
    }

    fun deleteWorkout(ex: Exercice) = CoroutineScope(Dispatchers.IO).launch {
        workoutExercicesDAO.deleteExercice(ex)
    }

    fun insertRep(reps: Reps) = CoroutineScope(Dispatchers.IO).launch {
        workoutExercicesDAO.insertReps(reps)
    }

    fun deleteTodaysWorkout(time : Long) = CoroutineScope(Dispatchers.IO).launch {
        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = time
        endOfDay.timeInMillis = time

        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        workoutExercicesDAO.deleteTodays(beginOfDay.timeInMillis, endOfDay.timeInMillis)
        workoutExercicesDAO.deleteTodaysEx(beginOfDay.timeInMillis, endOfDay.timeInMillis)
    }
}