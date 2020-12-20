package com.example.appahida.db.dailyworkoutdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CustomDay(
        @PrimaryKey(autoGenerate = false)
        val day : Int
)