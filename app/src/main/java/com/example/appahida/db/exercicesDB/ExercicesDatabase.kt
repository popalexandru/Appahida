package com.example.appahida.db.exercicesDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appahida.db.Converters

@Database(
        entities = [ExerciseItem::class],
        version = 1
)
@TypeConverters(Converters::class)
abstract class ExercicesDatabase : RoomDatabase(){
    abstract fun getExercicesDao() : ExercisesDAO
}