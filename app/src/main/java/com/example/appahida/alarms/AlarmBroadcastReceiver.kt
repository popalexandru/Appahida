package com.example.appahida.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.appahida.MainActivity
import com.example.appahida.R

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel
            val name = "Alarme"
            val descriptionText = "Detalhes do Alarme"
            val mChannel = NotificationChannel("AlarmId", name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = descriptionText
            mChannel.enableVibration(true)

            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val resultIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(context!!, "AlarmId")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Water reminder")
            .setContentText("Nu uita sa bei apa !")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Get the Notification manager service
        val am = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Generate an Id for each notification
        val id = System.currentTimeMillis() / 1000

        // Show a notification
        am.notify(id.toInt(), mBuilder.build())
}}