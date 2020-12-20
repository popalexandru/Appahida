package com.example.appahida.db.dailyworkoutdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [
            CustomDay::class,
            ExerciseToDo::class],
        version = 1
)
abstract class DailyExercicesDatabase : RoomDatabase(){
    abstract fun getDailyExercicesDAO() : DailyExercicesDao
}