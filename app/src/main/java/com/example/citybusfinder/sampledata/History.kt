package com.example.citybusfinder.sampledata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val source: String,
    val destination: String,
    val busNumber: String
)
