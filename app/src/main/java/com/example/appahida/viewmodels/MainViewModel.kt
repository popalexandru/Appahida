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

    val selectedDate = MutableStateFlow(Calendar.getInstance().timeInMillis)


    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState : StateFlow<UiState> = _uiState

    fun postState(state : UiState){
        _uiState.value = state
    }

    sealed class UiState{
        object Success : UiState()
        object Loading : UiState()
        object Empty : UiState()
    }


    private val water_quantity = combine(selectedDate){
        date -> selectedDate
    }.flatMapLatest {
        Timber.d("Fetching water values for${selectedDate.value}")
        waterRepository.getWaterByDate(selectedDate.value)
    }

    val waterLive = water_quantity.asLiveData()

    val preferencesFlow = preferencesManager.exercicesVersion
    val waterQtyFlow = preferencesManager.waterQty.asLiveData()

    val reminderFlow = preferencesManager.isReminderSet
    val reminderData = reminderFlow.asLiveData()

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

    private val worksFlow = combine(
            categoryQuery,
            searchQuery
    ){ query, search ->
        Pair(query, search)
    }.flatMapLatest { (query, search) ->
        repository.getExercicesByCategory(query, search)
    }

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

    fun addWater(waterToAdd : Int){
            val today = Calendar.getInstance().timeInMillis
            val waterItem = WaterDayItem(
                    waterToAdd,today)

            waterRepository.insertWater(waterItem)
    }

    fun setWaterReminder(){
        //val time = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        val time = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(4)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmBroadcastReceiver::class.java)
        notificationIntent.putExtra("next_notification", time)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, TimeUnit.HOURS.toMillis(3), pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)

        Timber.d("Alarma setata pentru $time")
    }

    /***********WATER RELATED FUNCTIONS ****************/
}