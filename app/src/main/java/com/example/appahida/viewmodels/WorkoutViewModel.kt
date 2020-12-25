package com.example.appahida.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.repository.WorkoutRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*


class WorkoutViewModel @ViewModelInject constructor(
        @ApplicationContext private val context : Context,
        private val workoutRepository: WorkoutRepository
) : ViewModel() {
    //val workoutByDay = workoutRepository.getToday(Calendar.getInstance()).asLiveData()
    val exercicesForToday = workoutRepository.getDayWorkouts(Calendar.getInstance()).asLiveData()

    fun insertExercice(exercice : ExerciseToAdd){
        val exer = Exercice(null,
                Calendar.getInstance().timeInMillis,
                exercice.name,
                exercice.description,
                exercice.picture
                )

            workoutRepository.insertWorkout(exer)
    }

    fun deleteExercice(exerciseToDo: Exercice){
        workoutRepository.deleteWorkout(exerciseToDo)
    }

    fun addReps(reps : Int, weight : Int, exId : Int){
        val newRep = Reps(null,
                Calendar.getInstance().timeInMillis, exId, reps, weight)

        workoutRepository.insertRep(newRep)
    }

    fun deleteTodayExercices(){
        val today = Calendar.getInstance().timeInMillis

        workoutRepository.deleteTodaysWorkout(today)
    }
/*    fun insertDay(){
        val timestamp = Calendar.getInstance().timeInMillis

        workoutRepository.insertDay(timestamp)
    }*/
}