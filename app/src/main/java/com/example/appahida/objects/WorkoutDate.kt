package com.example.appahida.objects

import com.example.appahida.constants.Constants.months
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

data class WorkoutDate(val calendar: Calendar)
{
    val currentDate = calendar
    val plannedDate = calendar

    val hour =calendar.get(Calendar.HOUR_OF_DAY)
    val minute =calendar.get(Calendar.MINUTE)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    init {
        CoroutineScope(Dispatchers.IO).launch {
        }
    }

    fun setHour(hour: Int, minute: Int) : String{
        plannedDate.set(Calendar.HOUR_OF_DAY, hour)
        plannedDate.set(Calendar.MINUTE, minute)

        return hour.toString() + ":" + minute.toString()
    }

    fun setDate(day : Int, month: Int, year: Int) : String{
        plannedDate.set(Calendar.DAY_OF_MONTH, day)
        plannedDate.set(Calendar.MONTH, month)
        plannedDate.set(Calendar.YEAR, year)

        return day.toString() + " " + months[month]
    }

    fun getTimestamp() : Long{
        return plannedDate.timeInMillis
    }

    fun setEndHour(hour: Int, minute: Int) : String{
        return hour.toString() + ":" + minute.toString()
    }

    fun getCurrentDate() : String{
        return currentDate.get(Calendar.DAY_OF_MONTH).toString() + " " + months[currentDate.get(Calendar.MONTH)]
    }

    fun getCurrentHour() : String{
        return hour.toString() + ":" + minute.toString()
    }
}
