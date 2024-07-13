package com.example.citybusfinder

import com.example.citybusfinder.sampledata.History
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val historyDAO: HistoryDAO

) {
    suspend fun insertHistory(history: History) {
        historyDAO.insertHistory(history)
    }
       fun getAllHistory(): Flow<List<History>> =
         historyDAO.getAllHistory()


}