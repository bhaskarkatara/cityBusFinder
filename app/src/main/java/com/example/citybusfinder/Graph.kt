package com.example.citybusfinder

import android.content.Context
import androidx.room.Room

object Graph {
    lateinit var database : HistoryDatabase

    val historyRepository by lazy {
        HistoryRepository(historyDAO = database.historyDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, HistoryDatabase::class.java, "history.db").build()
    }
}
