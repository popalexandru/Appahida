package com.example.appahida.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.db.DayWithExercices
import com.example.appahida.db.customday.CustomDay
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.repository.WorkoutRepository
import java.util.*


class WorkoutsViewModel @ViewModelInject constructor(
        private val repository: WorkoutRepository
) : ViewModel() {

    //private val todayWorkout : LiveData<CustomDay> = repository.getTodaysWorkout(Calendar.getInstance().timeInMillis).asLiveData()
    val daysWithWorkouts : LiveData<List<DayWithExercices>> = repository.getDaysWithExercices().asLiveData()


    fun addExerciseToWorkout(exerciseToAdd: ExerciseToAdd){
        val today = Calendar.getInstance()

        val zi = today.get(Calendar.DAY_OF_MONTH)
        val luna = today.get(Calendar.MONTH)
        val an = today.get(Calendar.YEAR)

        repository.insertExercices(exerciseToAdd)
        /*todayWorkout.value?.updateExercicesList(exerciseToAdd)
        todayWorkout.value?.let { repository.updateWorkout(it) }*/

/*        val cW = todayWorkout.value
        var currentList = todayWorkout.value?.exercices
        if(currentList?.isNotEmpty() == true){
            currentList.add(exerciseToAdd)
        }else{
            currentList = mutableListOf()
        }

        val newWorkout = cW?.zi?.let { WorkoutItem(it, cW.luna, cW.an, currentList) }
        if (newWorkout != null) {
            repository.insertWorkout(newWorkout)
        }*/
    }

    fun deleteWorkout(customDay: CustomDay){
        //repository.deleteWorkout(workoutItem)
    }

    /* Create an empty workout entry for today */
    fun createWorkout(){
        val today = Calendar.getInstance()

        val zi = today.get(Calendar.DAY_OF_MONTH)
        val luna = today.get(Calendar.MONTH)
        val an = today.get(Calendar.YEAR)

        val emptyExercicesList = mutableListOf<ExerciseToAdd>()

        val newWorkout = CustomDay(zi)
        repository.insertWorkout(newWorkout)
    }
}