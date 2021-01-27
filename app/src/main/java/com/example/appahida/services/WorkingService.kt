package com.example.appahida.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.appahida.MainActivity
import com.example.appahida.R
import com.example.appahida.constants.Constants
import com.example.appahida.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.appahida.constants.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.appahida.constants.Constants.ACTION_START_OR_RESUME
import com.example.appahida.constants.Constants.ACTION_START_PAUSE
import com.example.appahida.constants.Constants.ACTION_STOP_SERVICE
import com.example.appahida.constants.Constants.NOTIFICAITON_CHANNEL_ID
import com.example.appahida.constants.Constants.NOTIFICATION_CHANNEL_AME
import com.example.appahida.constants.Constants.NOTIFICATION_ID
import com.example.appahida.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WorkingService : LifecycleService() {
    var isFirstWorkout = true
    var serviceKilled = false
    var isFirstPause = true

    private val timeWorkedInSeconds = MutableLiveData<Long>(0)

    @Inject
    lateinit var baseNotificationBuilder : NotificationCompat.Builder

    private var isPauseEnabled = true
    private var isPause = false

    private var pauseTimer = 30L

    lateinit var currentNotification : NotificationCompat.Builder

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L


    companion object {
        val isWorking = MutableLiveData<Boolean>(false)
        val timeWorkedInMilliseconds = MutableLiveData<Long>(0)
        val timePause = MutableLiveData<Long>(0)
    }

    override fun onCreate() {
        super.onCreate()

        currentNotification = baseNotificationBuilder

        isWorking.observe(this, Observer {
            updateNotification(it)
        })
    }

    fun pauseStart(){
        isPause = true
        timePause.postValue(pauseTimer)
    }

    fun startTimer(){
        isWorking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

/*        if(isFirstPause){
            timePause.postValue(pauseTimer)
            isFirstPause = false
        }*/

        CoroutineScope(Dispatchers.Main).launch {
            while(isWorking.value!!){
                lapTime = System.currentTimeMillis() - timeStarted

                timeWorkedInMilliseconds.postValue(timeRun + lapTime)
                if(timeWorkedInMilliseconds.value!! >= lastSecondTimestamp + 1000L){
                    timeWorkedInSeconds.postValue(timeWorkedInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L

                    if(isPauseEnabled && isPause){
                        if(pauseTimer > 0 && timePause.value!! > 0){
                            timePause.postValue(timePause.value!! - 1)
                        }else if (timePause.value!! == 0L){
                            isPause = false
                        }
                    }
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService(){
        isWorking.postValue(false)
        isTimerEnabled = false
    }

    private fun killService(){
        serviceKilled = true
        isFirstWorkout = true
        postInitialValues()
        pauseService()
        stopForeground(true)
        stopSelf()
    }

    private fun postInitialValues(){
        isWorking.postValue(false)
        timeWorkedInSeconds.postValue(0L)
        timeWorkedInMilliseconds.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME -> {
                    if(isFirstWorkout){
                        startForegroundService()
                        isFirstWorkout = false
                    }else{
                        Timber.d("Resuming service..")
                        startTimer()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("Paused service")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    killService()
                }

                ACTION_START_PAUSE -> {
                    pauseStart()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        startTimer()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())


            timeWorkedInSeconds.observe(this, Observer {
                if(!serviceKilled){
                    val notification = currentNotification
                        .setContentText(Utility.getFormattedStopWatchTime(it * 1000))

                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
                NOTIFICAITON_CHANNEL_ID,
                NOTIFICATION_CHANNEL_AME,
                IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(isWorking : Boolean){
        val notificationActionText = if(isWorking) "Pause" else "Resume"
        val pendingIntent = if(isWorking){
            val pauseIntent = Intent(this, WorkingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else{
            val resumeIntent = Intent(this, WorkingService::class.java).apply {
                action = ACTION_START_OR_RESUME
            }
            PendingIntent.getService(this, 1, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotification, ArrayList<NotificationCompat.Action>())
        }

        if(!serviceKilled){
            currentNotification = baseNotificationBuilder
                    .addAction(R.drawable.ic_baseline_pause_24, notificationActionText, pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, currentNotification.build())
        }


    }
}