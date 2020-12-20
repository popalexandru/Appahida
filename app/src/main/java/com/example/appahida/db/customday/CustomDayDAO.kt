package com.example.appahida.db.customday

import androidx.room.*
import com.example.appahida.preferences.sortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise : CustomDay)

}