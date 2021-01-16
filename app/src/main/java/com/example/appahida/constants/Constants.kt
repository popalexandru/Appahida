package com.example.appahida.constants

object Constants {

    const val PIEPT = 0
    const val BRATE = 1
    const val UMERI = 2
    const val SPATE = 3
    const val ABDOMEN = 4
    const val CARDIO = 5
    const val PICIOARE = 6

    val weightValues = arrayOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120")
    val countValues = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13", "14", "15")

    const val EXERCICES_DATABASE_NAME = "exercices_database"
    const val WATER_DATABASE_NAME = "water_database"
    const val WORKOUT_DATABASE_NAME = "workout_database"
    const val EXERCICES_FIREBASE_NAME = "Exercices"
    const val EXERCICES_VERSION_COLLECTION = "Versions"
    const val EXERCICES_VERSION_DOCUMENT = "ExercicesVersions"

    const val EXERCICE_NAME = "Name"
    const val EXERCICE_IMG = "Image"
    const val EXERCICE_DES = "Description"
    const val EXERCICE_CAT = "Category"

    const val EXERCICE_EXCHANGE = "EXERCICE_EXCHANGE"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val NOTIFICAITON_CHANNEL_ID = "working_channel"
    const val NOTIFICATION_CHANNEL_AME = "Working"
    const val NOTIFICATION_ID = 1

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    val months = arrayOf("Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie", "Iulie", "August", "Septembrie", "Octombire", "Noiembrie", "Decembrie")
}