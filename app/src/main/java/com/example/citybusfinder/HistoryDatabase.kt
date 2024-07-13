package com.example.citybusfinder

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.citybusfinder.sampledata.History

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
//    abstract fun wishDao(): HistoryDAO
    abstract fun historyDao(): HistoryDAO
}
