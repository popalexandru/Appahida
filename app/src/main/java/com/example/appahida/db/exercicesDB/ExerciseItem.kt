package com.example.appahida.db.exercicesDB

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "exercices_table")
data class ExerciseItem(
        @ColumnInfo(name = "name") var name : String,
        @ColumnInfo(name = "picture") var picture : String,
        @ColumnInfo(name = "description") var description : String,
        @ColumnInfo(name = "category") var category : String
) : Serializable{
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}
