package com.example.appahida.repository

import com.example.appahida.db.workoutsdb.*
import com.example.appahida.utils.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
        val workoutExercicesDAO: WorkoutsExercicesDAO
) {

    fun updateDay(dayId : Long, duration : Long) = CoroutineScope(Dispatchers.IO).launch {
        workoutExercicesDAO.updateDay(dayId, duration, true)
    }

    fun getDayWorkouts(dayId : Long): Flow<DayWithExercices> {
/*        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = todayMilis
        endOfDay.timeInMillis = todayMilis

        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        val reStamp = Calendar.getInstance()
        reStamp.timeInMillis = dayId
        reStamp.set(Calendar.HOUR_OF_DAY, 0)
        reStamp.set(Calendar.MINUTE, 0)
        reStamp.set(Calendar.SECOND, 1)
        reStamp.set(Calendar.MILLISECOND, 0)*/

        //Timber.d("Fetching data for ${reStamp.timeInMillis} ${Utility.getDateString(reStamp.timeInMillis)}")

        //return workoutExercicesDAO.getExercicesForDay(dayId)
        return workoutExercicesDAO.getDayWithExercices(dayId)
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

    fun deleteTodayRecord(day : DayWithExercices) = CoroutineScope(Dispatchers.IO).launch {
        /*val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = time
        endOfDay.timeInMillis = time

        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)


        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)*/

        val exercicesToDelete = day.exercices

        for(exercice in exercicesToDelete){
            workoutExercicesDAO.deleteExercice(exercice.exercice)
        }

        workoutExercicesDAO.deleteDay(day.day)
    }

    fun checkToday(timestamp : Long) : Flow<Day>{
/*        val beginOfDay = Calendar.getInstance()
        val endOfDay = Calendar.getInstance()

        beginOfDay.timeInMillis = timestamp
        endOfDay.timeInMillis = timestamp

*//*        beginOfDay.set(Calendar.HOUR_OF_DAY, 0)
        beginOfDay.set(Calendar.MINUTE, 1)

        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)

        val timestamp = beginOfDay.with*//*

        beginOfDay.timeInMillis -= beginOfDay.timeInMillis % (24*60*60*1000)
        endOfDay.timeInMillis = beginOfDay.timeInMillis + 24*60*60*1000*/

        val reStamp = Calendar.getInstance()
        reStamp.timeInMillis = timestamp
        reStamp.set(Calendar.HOUR_OF_DAY, 0)
        reStamp.set(Calendar.MINUTE, 0)
        reStamp.set(Calendar.SECOND, 1)
        reStamp.set(Calendar.MILLISECOND, 0)

        return workoutExercicesDAO.getTodayValue(reStamp.timeInMillis)
    }

    fun insertDay(timestamp: Long)= CoroutineScope(Dispatchers.IO).launch {
        val reStamp = Calendar.getInstance()
        reStamp.timeInMillis = timestamp
        reStamp.set(Calendar.HOUR_OF_DAY, 0)
        reStamp.set(Calendar.MINUTE, 0)
        reStamp.set(Calendar.SECOND, 1)
        reStamp.set(Calendar.MILLISECOND, 0)

        workoutExercicesDAO.insertDay(Day(reStamp.timeInMillis, Utility.getDateString(reStamp.timeInMillis), false, 0))
    }
}