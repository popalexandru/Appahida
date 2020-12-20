package com.example.appahida.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.appahida.alarms.AlarmBroadcastReceiver
import com.example.appahida.db.exercicesDB.ExerciseItem
import com.example.appahida.db.waterdb.WaterDayItem
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.objects.RepCount
import com.example.appahida.onVersionChanged
import com.example.appahida.preferences.PreferencesManager
import com.example.appahida.preferences.sortOrder
import com.example.appahida.repository.FirebaseRepository
import com.example.appahida.repository.WaterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class MainViewModel @ViewModelInject constructor(
        private val preferencesManager: PreferencesManager,
        @ApplicationContext private val context : Context,
        private val repository: FirebaseRepository,
        private val waterRepository: WaterRepository
) : ViewModel() {

    private val water_quantity = waterRepository.getWaterByDate(Date().time)
    val waterLive = water_quantity.asLiveData()

    val preferencesFlow = preferencesManager.exercicesVersion
    val waterQtyFlow = preferencesManager.waterQty.asLiveData()

    val reminderFlow = preferencesManager.isReminderSet
    val reminderData = reminderFlow.asLiveData()

    val sortFlow = preferencesManager.sortOrderFlow
    val sortType = sortFlow.asLiveData()

    fun setReminder() = viewModelScope.launch{
        preferencesManager.setReminder()
        setWaterReminder()
        Timber.d("Reminder value was set")
    }

    val categoryQuery = MutableStateFlow("")
    val searchQuery = MutableStateFlow("")

    fun setCategoryQuery(query : String){
        categoryQuery.value = query
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    val workoutsList = mutableListOf<ExerciseToAdd>()
    val workoutsFlow : MutableLiveData<List<ExerciseToAdd>> = MutableLiveData()

    val list : MutableList<RepCount> = mutableListOf<RepCount>()

    fun addWorkoutToList(exerciseItem: ExerciseToAdd) = viewModelScope.launch{
        workoutsList.add(exerciseItem)
        Timber.d("$workoutsList")
        workoutsFlow.value = workoutsList
    }

    fun clearList(){
        workoutsList.clear()
        workoutsFlow.value = workoutsList
    }

    fun getWorkouts() : LiveData<List<ExerciseToAdd>>{
        return workoutsFlow
    }
    fun insertExercice(exerciseItem: ExerciseItem) = viewModelScope.launch(Dispatchers.IO){
        repository.insertExercise(exerciseItem)
    }

    private val worksFlow = combine(
            categoryQuery,
            searchQuery
    ){ query, search ->
        Pair(query, search)
    }.flatMapLatest { (query, search) ->
        repository.getExercicesByCategory(query, search)
    }

    //val exercices = repository.getExercicesByCategory("").asLiveData()
    val exercices = worksFlow.asLiveData()

    fun getExercices(listener : onVersionChanged) = viewModelScope.launch(){
        repository.getExercicesVersion(preferencesFlow.first(), listener)
    }

    fun updateVersion(version : Int) = viewModelScope.launch{
        preferencesManager.setExercicesVersion(version)
    }

    fun updateWaterMax(qty : Int) = viewModelScope.launch {
        preferencesManager.setWaterQty(qty)
    }

    /***************************************************
     *********** WATER RELATED FUNCTIONS ***************
     ***************************************************/
    var waterMax : MutableLiveData<Int> = MutableLiveData(2000)

/*    fun getWater() : Flow<Int> {
        return water_quantity
    }*/

    fun addWater(waterToAdd : Int){
        val currentWater = waterLive.value

        if(currentWater?.size!! > 0){
        val quantity = currentWater?.get(0).waterQty as Int

        if(quantity < 10000){
            currentWater[0].waterQty = quantity + waterToAdd
            //water_quantity.postValue(currentWater + waterSize)
            waterRepository.updateWater(currentWater[0])
        }
        }
        else{
            val today = Calendar.getInstance()
            val waterItem = WaterDayItem(
                    waterToAdd,
                    today.get(Calendar.DAY_OF_MONTH),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.YEAR))

            waterRepository.insertWater(waterItem)
        }
    }

    fun setWaterReminder(){
        val time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmBroadcastReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0)

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, TimeUnit.HOURS.toMillis(1), pendingIntent)

        Timber.d("Alarma setata pentru $time")
    }

/*    fun subtractWater(){
        val currentWater = water_quantity.value

        if (currentWater != null) {
            if(currentWater > 0){
                if(currentWater < waterSize){
                    water_quantity.postValue(0)
                }
                else{
                    water_quantity.postValue(currentWater - waterSize)
                }
            }
        }
    }*/

    /***********WATER RELATED FUNCTIONS ****************/
}