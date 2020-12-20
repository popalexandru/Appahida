package com.example.appahida

import android.content.Context
import androidx.room.Room
import com.example.appahida.constants.Constants.EXERCICES_DATABASE_NAME
import com.example.appahida.constants.Constants.WATER_DATABASE_NAME
import com.example.appahida.constants.Constants.WORKOUT_DATABASE_NAME
import com.example.appahida.db.exercicesDB.ExercicesDatabase
import com.example.appahida.db.waterdb.WaterDatabase
import com.example.appahida.db.workoutDB.WorkoutDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideExercicesDatabase(@ApplicationContext context : Context) =
            Room.databaseBuilder(context, ExercicesDatabase::class.java, EXERCICES_DATABASE_NAME)
                    .build()

    @Singleton
    @Provides
    fun provideWaterDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(context, WaterDatabase::class.java, WATER_DATABASE_NAME)
                    .build()

    @Singleton
    @Provides
    fun provideWorkoutDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(context, WorkoutDatabase::class.java, WORKOUT_DATABASE_NAME)
                    .build()

    @Singleton
    @Provides
    fun provideExerciceForDayDao(db : WorkoutDatabase) = db.getExerciceForTodayDao()

    @Singleton
    @Provides
    fun providecustomdaydao(db : WorkoutDatabase) = db.getcustomdaydao()

    @Singleton
    @Provides
    fun provideExercicesDao(db : ExercicesDatabase) = db.getExercicesDao()

    @Singleton
    @Provides
    fun provideWaterDao(db : WaterDatabase) = db.getWaterDAO()

    @Singleton
    @Provides
    fun provideWorkoutDao(db : WorkoutDatabase) = db.getWorkoutDao()

    @Singleton
    @Provides
    fun provideFirebaseInstance() = FirebaseFirestore.getInstance();
}