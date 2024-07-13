package com.example.citybusfinder.sampledata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "history-source")
    val source: String,
    @ColumnInfo(name = "history-destination")
    val destination: String,
    @ColumnInfo(name = "history-bus-number")
    val busNumber: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
