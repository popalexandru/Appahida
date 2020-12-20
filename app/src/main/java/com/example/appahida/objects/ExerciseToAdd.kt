package com.example.appahida.objects

data class ExerciseToAdd(
    val name : String,
    val picture : String,
    val description : String,
    val category : String,
    val repList : MutableList<Pair<Int, Int>>?
)
