package com.example.appahida.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.example.appahida.objects.ExerciseToAdd
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {
    val gson = Gson()

    @TypeConverter
    fun toBitmap(bytes : ByteArray) : Bitmap{
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap) : ByteArray{
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)

        return outStream.toByteArray()
    }

    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter fun datestampToCalendar(value: Long): Calendar =
            Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun exercicesToJson(value : List<ExerciseToAdd>) = Gson().toJson(value)

    @TypeConverter
    fun JsontoExercices(value : String) = Gson().fromJson(value, Array<ExerciseToAdd>::class.java).toList()
}