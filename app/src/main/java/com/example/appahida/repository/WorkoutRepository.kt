package com.example.appahida.repository

import com.example.appahida.db.dailyworkoutdb.ExerciseToDo
import com.example.appahida.db.workoutsdb.Day
import com.example.appahida.db.workoutsdb.DaywithExercices
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.WorkoutsExercicesDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
        val workoutExercicesDAO: WorkoutsExercicesDAO
){

    fun getToday(day : Calendar) : Flow<DaywithExercices> {
        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = day.timeInMillis
        endOfDay.timeInMillis = day.timeInMillis
        
        beginOfDay.set(Calendar.HOUR_OF_DAY, 1)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        return workoutExercicesDAO.getDayWithExercices(beginOfDay.timeInMillis, endOfDay.timeInMillis)
    }

    fun insertWorkout(exerciseToDo: ExerciseToDo, dayId : Int) = CoroutineScope(Dispatchers.IO).launch {
        val ex = Exercice(null, dayId, exerciseToDo.name, exerciseToDo.desc, exerciseToDo.image)

        workoutExercicesDAO.insertExercice(ex)
    }

    fun deleteWorkout(ex: Exercice) = CoroutineScope(Dispatchers.IO).launch {
        workoutExercicesDAO.deleteExercice(ex)
    }

    fun insertDay(timestamp : Long)= CoroutineScope(Dispatchers.IO).launch {
        workoutExercicesDAO.insertDay(Day(null, timestamp, 0, 0, 0))
    }

}