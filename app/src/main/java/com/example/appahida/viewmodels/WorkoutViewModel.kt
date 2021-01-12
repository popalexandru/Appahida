package com.example.appahida.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.repository.WorkoutRepository
import com.example.appahida.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import java.util.*


class WorkoutViewModel @ViewModelInject constructor(
        @ApplicationContext private val context : Context,
        private val workoutRepository: WorkoutRepository
) : ViewModel() {
    //val selectedDate = MutableStateFlow(Calendar.getInstance().timeInMillis)

    var lastWeightAdded = 0
    var lastRepsAdded = 0

    val selectedDate = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 1)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis)

    val exercicesForTo = combine(selectedDate){
        date -> selectedDate
    }.flatMapLatest {
        Timber.d("Fetching data for ${selectedDate.value} ${Utility.getDateString(selectedDate.value)}")
        workoutRepository.getDayWorkouts(selectedDate.value)
    }
    val exercicesForToday = exercicesForTo.asLiveData()

    val todaysFlow = combine(selectedDate){
        date -> selectedDate
    }.flatMapLatest {
        workoutRepository.checkToday(it.value)
    }
    val todaysValue = todaysFlow.asLiveData()

    fun finishTodaysWorkout(duration : Long){
        val todaysTimestamp = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        workoutRepository.updateDay(todaysTimestamp, duration)
    }

    fun insertExercice(exercice : ExerciseToAdd){
        val exer = Exercice(null,
                selectedDate.value,
                exercice.name,
                exercice.description,
                exercice.picture
                )

            workoutRepository.insertWorkout(exer)
    }

    fun insertDay(timestamp : Long, select : Boolean){
        if(select){
            workoutRepository.insertDay(selectedDate.value)
        }else{
            workoutRepository.insertDay(timestamp)
        }

    }

    fun deleteExercice(exerciseToDo: Exercice){
        workoutRepository.deleteWorkout(exerciseToDo)
    }

    fun addReps(reps : Int, weight : Int, exId : Int){
        val newRep = Reps(null,
                Calendar.getInstance().timeInMillis, exId, reps, weight)

        workoutRepository.insertRep(newRep)
    }

    fun deleteToday(){
        val todayRecord = exercicesForToday.value

        if (todayRecord != null) {
            workoutRepository.deleteTodayRecord(todayRecord)
        }
    }
}