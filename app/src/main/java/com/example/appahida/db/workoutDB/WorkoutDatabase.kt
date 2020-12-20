package com.example.appahida.db.workoutDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appahida.db.Converters
import com.example.appahida.db.DayWithExercices
import com.example.appahida.db.customday.CustomDay
import com.example.appahida.db.customday.CustomDayDAO

@Database(
        entities = [CustomDay::class, ExerciceForDay::class],
        version = 1
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase(){
    abstract fun getWorkoutDao() : WorkoutDAO
    abstract fun getExerciceForTodayDao() : ExForTodayDAO
    abstract fun getcustomdaydao() : CustomDayDAO
}