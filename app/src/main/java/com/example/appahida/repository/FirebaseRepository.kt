package com.example.appahida.repository

import android.content.Context
import com.example.appahida.constants.Constants.EXERCICES_FIREBASE_NAME
import com.example.appahida.constants.Constants.EXERCICES_VERSION_COLLECTION
import com.example.appahida.constants.Constants.EXERCICES_VERSION_DOCUMENT
import com.example.appahida.constants.Constants.EXERCICE_CAT
import com.example.appahida.constants.Constants.EXERCICE_DES
import com.example.appahida.constants.Constants.EXERCICE_IMG
import com.example.appahida.constants.Constants.EXERCICE_NAME
import com.example.appahida.db.exercicesDB.ExerciseItem
import com.example.appahida.db.exercicesDB.ExercisesDAO
import com.example.appahida.onVersionChanged
import com.example.appahida.preferences.sortOrder
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
        val exercisesDAO: ExercisesDAO,
        val database : FirebaseFirestore,
        @ApplicationContext private val context: Context
){

    fun insertExercise(exerciseItem: ExerciseItem) = CoroutineScope(Dispatchers.IO).launch{
        exercisesDAO.insertExercise(exerciseItem)
    }

    suspend fun deleteExercice(exerciseItem: ExerciseItem) = exercisesDAO.deleteExercise(exerciseItem)

    fun getExercicesByCategory(category: String, search: String) : Flow<List<ExerciseItem>> = exercisesDAO.getExercicesByCategory(category, search)

    fun getExercicesList(listener : onVersionChanged) = CoroutineScope(Dispatchers.IO).launch{
        database.collection(EXERCICES_FIREBASE_NAME)
                .get()
                .addOnSuccessListener {
                    val exercicesDocuments = it.documents

                    for(exercice in exercicesDocuments){
                        val imageUrl = exercice.getString(EXERCICE_IMG)!!
                        val exerciceDesc = exercice.getString(EXERCICE_DES)!!
                        val exerciceCat = exercice.getString(EXERCICE_CAT)!!
                        val exerciceName = exercice.getString(EXERCICE_NAME)!!

                        insertExercise(ExerciseItem(exerciceName, imageUrl, exerciceDesc, exerciceCat))

                        Timber.d("Inserting $exerciceName")
                    }

                    listener.stopLoading()
                }
    }

    fun getExercicesVersion(version : Int, listener : onVersionChanged){

        val documentReference = database.collection(EXERCICES_VERSION_COLLECTION).document(EXERCICES_VERSION_DOCUMENT)

        documentReference.get()
                .addOnSuccessListener { document->
                    if(document != null){
                        val versiune = document.getString("versiune")
                        val versionNumber = Integer.parseInt(versiune)


                        if(version < versionNumber){

                            listener.startLoading()
                            listener.onVersionChanged(versionNumber)

                            Timber.d("Version changed to $versionNumber")

                            CoroutineScope(Dispatchers.IO).launch {
                                exercisesDAO.deleteAll()
                                Timber.d("Deleting existing database")

                                getExercicesList(listener)
                            }

                        }else{
                            Timber.d("Version is the same")
                        }
                    }
                }

    }
}