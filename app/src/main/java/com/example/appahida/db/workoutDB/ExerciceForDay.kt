package com.example.appahida.db.workoutDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_exercices")
class ExerciceForDay (
        @PrimaryKey val zi : Int,
        val exName : String,
        var exPicture : String,
        var exDesc : String,
        var exCateg : String
){
}