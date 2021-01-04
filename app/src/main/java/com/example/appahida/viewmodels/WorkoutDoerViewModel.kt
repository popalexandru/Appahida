package com.example.appahida.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.constants.Constants.TIMER_UPDATE_INTERVAL
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.db.workoutsdb.Reps
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.repository.WorkoutRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class WorkoutDoerViewModel @ViewModelInject constructor(
        @ApplicationContext private val context : Context,
        private val workoutRepository: WorkoutRepository
) : ViewModel() {

/*    val timeSpent = MutableLiveData<Long>(0)
    val timeSpentInMilis = MutableLiveData<Long>(0)
    val isWorking = MutableLiveData<Boolean>(false)

    companion object {
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L*/

/*    fun startTimer(){
        isWorking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while(isWorking.value!!){
                lapTime = System.currentTimeMillis() - timeStarted

                timeSpentInMilis.postValue(timeRun + lapTime)
                if(timeSpentInMilis.value!! >= lastSecondTimestamp + 1000L){
                    timeSpent.postValue(timeSpent.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }*/
}