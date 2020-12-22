package com.example.appahida.db.exercicesDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appahida.preferences.sortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface ExercisesDAO {

    fun getExercices(sort: sortOrder, query: String){
        when(sort){
            sortOrder.NUME -> {

            }

            sortOrder.PICIOARE -> {

            }

            sortOrder.CARDIO -> {

            }

            sortOrder.ABDOMEN -> {

            }

            sortOrder.SPATE -> {

            }

            sortOrder.PIEPT -> {

            }

            sortOrder.BRATE -> {

            }

            sortOrder.UMERI -> {

            }
        }
    }

    @Query("SELECT * FROM exercices_table WHERE (category LIKE '%' || :category || '%' AND name LIKE '%' || :query || '%') ORDER BY name ASC")
    fun getExercicesByCategory(category: String, query: String) : Flow<List<ExerciseItem>>

    @Query("SELECT * FROM exercices_table ORDER BY name ASC")
    fun getAllExercices() : Flow<List<ExerciseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseItem)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseItem)

    @Query("DELETE FROM exercices_table")
    fun deleteAll()
}