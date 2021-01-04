package com.example.appahida.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.renderscript.RenderScript
import androidx.core.app.NotificationCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.lifecycle.asLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.appahida.MainActivity
import com.example.appahida.db.waterdb.WaterDAO
import com.example.appahida.repository.WaterRepository
import com.google.android.datatransport.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*

class WaterWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val waterRepository : WaterRepository
) : Worker(appContext, workerParams){
    override fun doWork(): Result {
        val waterDrink = waterRepository.getWaterByDate(Calendar.getInstance().timeInMillis).asLiveData().value

        var valueToShow = 100

        if (waterDrink != null) {
            if(waterDrink > 0){
                valueToShow = waterDrink
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java)

        val notManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title = "Water reminder"
        val subtitle = "Ai baut doar $valueToShow ml de apa :( "

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, "water")
            .setContentTitle(title)
            .setContentText(subtitle)
            .setContentIntent(pendingIntent).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            notification.setChannelId("water")


        // Generate an Id for each notification
        val id = System.currentTimeMillis() / 1000

        notManager.notify(id.toInt(), notification.build())

        return Result.success()
    }

}