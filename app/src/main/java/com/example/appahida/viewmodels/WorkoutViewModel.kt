package com.example.appahida.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.db.dailyworkoutdb.ExerciseToDo
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.repository.WorkoutRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.util.*


class WorkoutViewModel @ViewModelInject constructor(
        @ApplicationContext private val context : Context,
        private val workoutRepository: WorkoutRepository
) : ViewModel() {
    val workoutByDay = workoutRepository.getToday(Calendar.getInstance()).asLiveData()

    fun insertExercice(exercice : ExerciseToAdd){
        val exer = ExerciseToDo(
                Calendar.getInstance(),
                exercice.name,
                exercice.description,
                exercice.picture
                )

        workoutByDay.value?.day?.dayId?.let { workoutRepository.insertWorkout(exer, it) }
    }

    fun deleteExercice(exerciseToDo: Exercice){
        workoutRepository.deleteWorkout(exerciseToDo)
    }

    fun insertDay(){
        val timestamp = Calendar.getInstance().timeInMillis

        workoutRepository.insertDay(timestamp)
    }
}