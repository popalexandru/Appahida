package com.example.appahida.db.customday

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appahida.objects.ExerciseToAdd
import timber.log.Timber
import java.io.Serializable
import java.util.*

@Entity
data class CustomDay(
        @PrimaryKey var zi : Int
/*        @ColumnInfo(name="luna") var luna : Int,
        @ColumnInfo(name="an") var an : Int,
        @Embedded var exercices : List<ExerciseToAdd>*/
) : Serializable{
    /*@PrimaryKey var id: Int = zi*/
}
