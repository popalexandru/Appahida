package com.example.appahida.db.workoutsdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appahida.db.Converters

@Database(
        entities = [Reps::class, Exercice::class],
        version = 1
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase(){
    abstract fun getWorkoutsDao() : WorkoutsExercicesDAO
}