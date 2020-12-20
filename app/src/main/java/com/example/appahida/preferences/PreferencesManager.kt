package com.example.appahida.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class sortOrder{
    NUME,
    PIEPT,
    BRATE,
    UMERI,
    SPATE,
    ABDOMEN,
    CARDIO,
    PICIOARE
}

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context : Context){
    private val dataStore = context.createDataStore("user_preferences")

    /* read exercices list version */
    val exercicesVersion : Flow<Int> = dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.EXERCICES_VERSION] ?: 0
            }

    val waterQty : Flow<Int> = dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.WATER_QUANTITY] ?: 2000
            }

    val isReminderSet : Flow<Boolean> = dataStore.data
            .map{ preferences ->
                preferences[PreferencesKeys.REMINDER_SET] ?: false
            }

    val sortOrderFlow : Flow<sortOrder> = dataStore.data
            .map {
                sortOrder.valueOf(
                        it[PreferencesKeys.SORT_ORDER] ?: sortOrder.NUME.name
                )
            }

    /* write exercices list version */
    suspend fun setExercicesVersion(version : Int){
        dataStore.edit { settings ->
            settings[PreferencesKeys.EXERCICES_VERSION] = version
        }
    }

    suspend fun setWaterQty(qty : Int){
        dataStore.edit{settings ->
            settings[PreferencesKeys.WATER_QUANTITY] = qty
        }
    }

    suspend fun setReminder(){
        dataStore.edit { settings ->
            settings[PreferencesKeys.REMINDER_SET] = true
        }
    }

    suspend fun updateSortOrder(sortOrder: sortOrder){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    /* object stored inside */
    private object PreferencesKeys {
        val EXERCICES_VERSION = preferencesKey<Int>("exercice_version")
        val WATER_QUANTITY = preferencesKey<Int>("water_qty")
        val REMINDER_SET = preferencesKey<Boolean>("reminder_set")
        val SORT_ORDER = preferencesKey<String>("sort_order")
    }
}