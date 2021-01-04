package com.example.appahida.utils

import timber.log.Timber
import java.util.concurrent.TimeUnit

object Utility {

    fun getFormattedStopWatchTime(ms : Long, includeMillis : Boolean = false) : String {
        var milliseconds = ms

        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if(!includeMillis){
            if(hours > 0){
                return "${if(hours < 10) "0" else ""}$hours:" +
                        "${if(minutes < 10) "0" else ""}$minutes:" +
                        "${if(seconds < 10) "0" else ""}$seconds:"
            }

            return "${if(minutes < 10) "0" else ""}$minutes:" +
                    "${if(seconds < 10) "0" else ""}$seconds"
        }

        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 100

        Timber.d("Hours: $hours")
        if(hours > 0){
            return "${if(hours < 10) "0" else ""}$hours:" +
                    "${if(minutes < 10) "0" else ""}$minutes:" +
                    "${if(seconds < 10) "0" else ""}$seconds:" +
                    "${if(milliseconds < 10) "0" else ""}$milliseconds"
        }

        return "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds:" +
                "${if(milliseconds < 10) "0" else ""}$milliseconds"
    }
}