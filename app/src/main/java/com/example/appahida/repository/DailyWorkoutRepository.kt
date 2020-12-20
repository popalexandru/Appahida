package com.example.appahida.repository

import android.content.Context
import com.example.appahida.db.dailyworkoutdb.DailyExercicesDao
import com.example.appahida.db.exercicesDB.ExercisesDAO
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DailyWorkoutRepository @Inject constructor(
        val dailyExercicesDao: DailyExercicesDao,
        @ApplicationContext private val context: Context
){

}